package com.example.android.passphrasegenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String rawText = readFileFromRawDirectory(R.raw.eff_large_wordlist); // Read file from res/raw into string
        final Map<Integer, String> map = createMapFromString(rawText); // Convert string into map

        final int NUM_DIE = 5;

        final TextView tvWordLength = findViewById(R.id.tv_word_length);
        final SeekBar seekBar = findViewById(R.id.sb_word_length);
        tvWordLength.setText(getString(R.string.word_length) + " " + seekBar.getProgress());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvWordLength.setText(getString(R.string.word_length) + " " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        final TextView tvPassphrase = findViewById(R.id.tv_passphrase);
        Button btnGeneratePassphrase = findViewById(R.id.btn_generate_passphrase);
        btnGeneratePassphrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPassphrase.setText(createPhrases(map, seekBar.getProgress(), NUM_DIE));
            }
        });

        tvPassphrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("phrase", tvPassphrase.getText()));
                Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String readFileFromRawDirectory(int resourceId) {
        InputStream inStream = getResources().openRawResource(resourceId);
        String text = "";
        try {
            int size = inStream.available();
            byte[] buffer = new byte[size];
            inStream.read(buffer);
            inStream.close();;
            text = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    private Map<Integer, String> createMapFromString(String rawText) {
        String[] words = rawText.split("\\s+");
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < words.length; i+=2) {
            map.put(Integer.parseInt(words[i]), words[i+1]);
        }

        return map;
    }

    private String createPhrases(Map<Integer, String> map, int numWords, int numDie) {
        String phrases = "";
        for (int i = 0; i < numWords; i++) {
            phrases += map.get(rollDice(numDie));
            phrases += (i != numWords - 1) ? " " : "";
        }
        return phrases;
    }

    private int rollDice(int numDie) {
        int roll;
        String rolls = "";
        Random rand = new Random();
        // Roll the dice 'numDie' number of times
        for (int i = 0; i < numDie; i++) {
            roll = rand.nextInt(6)+1;
            rolls += Integer.toString(roll);
        }
        int numsRolled = Integer.parseInt(rolls);

        return numsRolled;
    }
}

package com.donhuan.SmshubAndroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MyActivity extends Activity {
    public static final String EXAMPLE_TEST = "ЗСКБ 9876 11янв 13:02 оплата 500р, остаток 5200.50р";

    TextView tvHello;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tvHello = (TextView) findViewById(R.id.textView1);
    }

    public void onClick(View v) {
        String[] splitString = (EXAMPLE_TEST.split("\\s+"));
        String oldWord = "";
        String titleText = "";
        for (String word : splitString) {
            if (oldWord.equals("оплата")) {
                titleText = titleText + "Сумма оплаты: " + readAmounts(word) + "\n";
            }
            if (oldWord.equals("остаток")) {
                titleText = titleText + "Сумма остатка: " + readAmounts(word) + "\n";
            }
            tvHello.setText(titleText);
            oldWord = word;
        }

    }

    private double readAmounts(String str) {
        double mount = 0;
        for (int i = str.length(); i > 0; i--) {
            try {
                mount = Double.parseDouble(str.substring(0, i));
                return mount;
            } catch (NumberFormatException ignore) {
            }
        }
        return mount;
    }

}

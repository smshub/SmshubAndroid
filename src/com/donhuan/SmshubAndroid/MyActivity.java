package com.donhuan.SmshubAndroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MyActivity extends Activity {
    //1

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

    public void onClick(View v){
        tvHello.setText("Hello Kitty!");
    }

}

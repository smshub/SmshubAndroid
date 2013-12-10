package com.donhuan.SmshubAndroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.io.*;
import java.util.Collections;
import java.util.Vector;

public class MyActivity extends Activity implements OnCheckedChangeListener {
    public static final String EXAMPLE_TEST1 = "ЗСКБ 9876 11 января 2013 13:02 оплата 500р, MAGAZIN остаток 5200.50р.";
    public static final String EXAMPLE_TEST2 = "VISA 8339: 31.10 09:11 покупка на сумму 500 руб. PIZZA HUT PETROGRADSKAYA выполненна успешно. Доступно: 3417.83 руб.";

    CommonFunctions commonFunctions = new CommonFunctions();
    ListView smsListView;
    ToggleButton toogleButton;
    Vector<String> smsVector = new Vector<String>(0);
    boolean state;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toogleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        smsListView = (ListView) findViewById(R.id.listView);
        toogleButton.setOnCheckedChangeListener(this);
        smsListView.setOnItemClickListener(itemListener);
    }

    protected void onResume() {
        super.onResume();
        int i = readFile("is_active").length();
        if (i == 2) toogleButton.setChecked(true);
        else toogleButton.setChecked(false);

    }


    protected void onPause() {
        if (state) writeFile("0", "is_active");
        else writeFile("00", "is_active");
        super.onPause();
    }


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            state = true;
            startService(new Intent(this, SmsService.class));
        } else {
            state = false;
            stopService(new Intent(this, SmsService.class));
        }
    }

    /*
    * Метод позволяет реагировать на нажатие по ListView
    * */
    AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            //Обработка нажатия на меню
            Toast toast = Toast.makeText(getApplicationContext(), smsVector.get(position), 10000);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    };


    public void onClick1(View v) {
        String items[] = commonFunctions.scanMessage(EXAMPLE_TEST1);                                                    // вызвращает вектор стрингов с распознанными значениями
        Collections.addAll(smsVector, items);
//        Toast.makeText(this, smsVector.get(0), Toast.LENGTH_LONG).show();

        smsListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, smsVector));

    }

    private String readFile(String filename) {
        String text = "";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(filename)));
            String str;
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                text += str + "\n";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return text;
    }

    public void writeFile(String titleText, String filename) {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput(filename, MODE_PRIVATE)));
            // пишем данные
            bw.write(titleText);
            // закрываем поток
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}

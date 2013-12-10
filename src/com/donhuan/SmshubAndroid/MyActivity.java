package com.donhuan.SmshubAndroid;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.io.*;
import java.util.Vector;

public class MyActivity extends Activity implements OnCheckedChangeListener {
    String EXAMPLE_TEST1 = "ЗСКБ 9876 11 января 2013 13:02 оплата 500р, MAGAZIN остаток 5200.50р.";
    String EXAMPLE_TEST2 = "VISA 8339: 31.10 09:11 покупка на сумму 500 руб. PIZZA HUT PETROGRADSKAYA выполненна успешно. Доступно: 3417.83 руб.";
    String EXAMPLE_TEST3 = "AlphaBank 5454 23.11.2013 16:13 Произведена покупка на сумму 2500.00 руб. в Карусель успешно. Доступно: 10500.00 руб.";
    String EXAMPLE_TEST4 = "MasterCard 9999 24.11.2013 23:43 Совершена покупка на сумму 4500.00 руб. в Карусель успешно. Доступно: 1000.00 руб.";

    ListView smsListView;
    ToggleButton toogleButton;
    Vector<String> smsVector = new Vector<String>(0);
    boolean state;
    final Uri SMSBASE_URI = Uri.parse("content://com.donhuan.SmshubAndroid.SMSDataBaseProvider/smsdata");
    final String LOG_TAG = "MyLogs";
    CommonFunctions commonFunctions;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toogleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        smsListView = (ListView) findViewById(R.id.listView);
        toogleButton.setOnCheckedChangeListener(this);
        smsListView.setOnItemClickListener(itemListener);

        commonFunctions = new CommonFunctions(getContentResolver(), SMSBASE_URI);
    }

    //Send data button
    public void onClick3(View v) {
        CommonFunctions commonFunctions = new CommonFunctions(getContentResolver(), SMSBASE_URI);
        commonFunctions.putInfoToDB("bank", "banknum", "store", "date", "time", "smon", "rmon");
    }

    public void onClick4(View v) {
        smsVector = new Vector<String>(0);
        Cursor cursor = getContentResolver().query(SMSBASE_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(SMSDataBaseProvider.SMSDATA_ID));
            String bankname = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.BANKNAME));
            String banknum = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.BANKNUM));
            String storename = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.STORENAME));
            String date = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.DATE));
            String time = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.TIME));
            String spendmon = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.SPENDMON));
            String restmon = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.RESTMON));
            String isinfin = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.ISINFIN));                      //флаги
            Log.i(LOG_TAG, id + " " + bankname + " " + banknum + " " + storename + " " + date + " " + time + " " + spendmon + " " + restmon + " " + isinfin);


            String smsTitle = date + " / " + time + "; " + "(" + bankname + ") " + storename;
            smsVector.add(smsTitle);
        }
        cursor.close();
        smsListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsVector));
    }

    protected void onResume() {
        super.onResume();

        int i = readFile("is_active").length();
        if (i == 2) toogleButton.setChecked(true);
        else toogleButton.setChecked(false);

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
    */
    AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            //Обработка нажатия на меню
            Toast toast = Toast.makeText(getApplicationContext(), smsVector.get(position), 10000);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    };


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

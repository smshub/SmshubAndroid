package com.donhuan.SmshubAndroid;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.io.*;

public class MyActivity extends Activity implements OnCheckedChangeListener {
    private static final String exMessages[] = new String[10];
    String EXAMPLE_TEST1 = "ЗСКБ 9876 11 января 2013 13:02 оплата 500р, остаток 5200.50р.";
    String EXAMPLE_TEST2 = "VISA 8339: 31.10 09:11 покупка на сумму 500 руб. PIZZA HUT PETROGRADSKAYA выполненна успешно. Доступно: 3417.83 руб.";
    String EXAMPLE_TEST3 = "AlphaBank 5454 23.11.2013 16:13 Произведена покупка на сумму 2500.00 руб. в Карусель успешно. Доступно: 10500.00 руб.";
    String EXAMPLE_TEST4 = "MasterCard 9999 24.11.2013 23:43 Совершена покупка на сумму 4500.00 руб. в Карусель успешно. Доступно: 1000.00 руб.";

    ListView smsListView;
    ToggleButton toogleButton;
    boolean state;
    final Uri SMSBASE_URI = Uri.parse("content://com.donhuan.SmshubAndroid.SMSDataBaseProvider/smsdata");
    final String LOG_TAG = "MyLogs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toogleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        smsListView = (ListView) findViewById(R.id.listView);
        toogleButton.setOnCheckedChangeListener(this);

        exMessages[1] = EXAMPLE_TEST1;
        exMessages[2] = EXAMPLE_TEST2;
        exMessages[3] = EXAMPLE_TEST3;
        exMessages[4] = EXAMPLE_TEST4;

    }

    //Send data button
    public void onClick3 (View v) {

        CommonFunctions commonFunctions = new CommonFunctions(getContentResolver(),SMSBASE_URI);
        commonFunctions.putInfoToDB("bank","bank","store","date","time","smon","rmon");

        Toast.makeText(this, "Woohooo", Toast.LENGTH_LONG).show();
    }

    public void onClick4(View v)
    {


        Cursor cursor = getContentResolver().query(SMSBASE_URI, null, null, null, null);
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(SMSDataBaseProvider.SMSDATA_ID));
            String bankname = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.BANKNAME));
            String banknum = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.BANKNUM));
            String storename = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.STORENAME));
            String date = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.DATE));
            String time = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.TIME));
            String spendmon = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.SPENDMON));
            String restmon = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.RESTMON));
            String isinfin = cursor.getString(cursor.getColumnIndex(SMSDataBaseProvider.ISINFIN));
            Log.i(LOG_TAG, id + " " + bankname + " " + banknum + " " + storename + " " + date + " " + time + " " + spendmon + " " + restmon + " " + isinfin);
        }
        cursor.close();

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


// Button-3
    //public void onClick3(View v) {

    //}

    private void scanTestMessage(String message) {
        String smsTexts[];                                                                                              //Список для записи в него сообщений

        smsTexts = new String[2];                                                                                       //Очищаем список
        smsTexts[0] = message;

        String[] splitString = (message.split("\\s+"));
        boolean[] list = new boolean[7];                                                                                //Банк, номер, дата, время, оплата, магазин, остаток

        for (int i = 0; i < splitString.length; i++) {
            String word = splitString[i].toLowerCase();

            String regexpMonth = "(" +
                    "янв\\w*" +
                    "|фев\\w*" +
                    "|м\\w?рт\\w?" +
                    "|апр\\w*" +
                    "|ма\\w?" +
                    "|июн\\w?" +
                    "|июл\\w?" +
                    "|авг\\w*" +
                    "|сен\\w*" +
                    "|н\\w{2}?бр\\w?" +
                    "|дек\\w*)";

            if (word.matches("[a-z]+|[а-я]+") && !list[0]) {                                                                //Проверка на банк
                smsTexts[1] = word + "\tбанк\n";
                list[0] = true;
            } else if (word.matches("\\d+.?") && !list[1]) {                                                                //Проверка на номер
                smsTexts[1] += word + "\tномер\n";
                list[1] = true;
            } else if (word.matches("\\d{1,2}(" + regexpMonth + "|(\\.)\\d{2}((\\.)\\d{2,4})?)") && !list[2]) {             //Проверка на дату формата 1       (слитного)
                smsTexts[1] += word + "\tдата\n";
                list[2] = true;
            } else if (word.matches(regexpMonth) && splitString[i - 1].matches("\\d{1,2}") && !list[2]) {                   //Проверка на дату формата 2       (раздельного)
                String yy = "";
                if (splitString[i + 1].matches("\\d{2,4}?")) {
                    yy = splitString[i + 1];
                }
                smsTexts[1] += splitString[i - 1] + " " + word + " " + yy + "\tдата\n";
                list[2] = true;
            } else if (word.matches("\\d{1,2}(:|,|(\\.))\\d{2}") && !list[3]) {                                              //Проверка на время
                smsTexts[1] += word + "\tвремя\n";
                list[3] = true;
            } else if (word.matches("\\d+([._,]\\d*)?\\w*((\\.)|,)?") && list[0] && list[1] && list[2] && list[3]) {         //Проверка на суммы  (олько если до этого нашли номер, банк , дату и время)
                smsTexts[1] += word + "\tсумма\n";
            }
        }


        smsListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsTexts));
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

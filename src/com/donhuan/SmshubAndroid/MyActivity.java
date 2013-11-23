package com.donhuan.SmshubAndroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.io.*;

public class MyActivity extends Activity implements OnCheckedChangeListener {
    public static final String EXAMPLE_TEST1 = "ЗСКБ 9876 11 января 2013 13:02 оплата 500р, остаток 5200.50р.";
    public static final String EXAMPLE_TEST2 = "VISA 8339: 31.10 09:11 покупка на сумму 500 руб. PIZZA HUT PETROGRADSKAYA выполненна успешно. Доступно: 3417.83 руб.";
    CommonFunctions commonFunctions = new CommonFunctions();

    ListView smsListView;
    ToggleButton toogleButton;
    boolean state;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toogleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        smsListView = (ListView) findViewById(R.id.listView);
        toogleButton.setOnCheckedChangeListener(this);
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
        if (state) commonFunctions.writeFile("0", "is_active");
        else commonFunctions.writeFile("00", "is_active");
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
    * Поиск нужного формата с использованием регулярных выражений,
    * но с условием того, что порядок входных данных строго фиксирован.
    * Поэтому используются флаги.
    * */
    public void onClick1(View v) {
        scanTestMessage(EXAMPLE_TEST1);
    }

    public void onClick2(View v) {
        scanTestMessage(EXAMPLE_TEST2);
    }

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
                smsTexts[1] += word + "\tомер\n";
                list[1] = true;
            } else if (word.matches("\\d{1,2}(" + regexpMonth + "|(\\.)\\d{2}((\\.)\\d{2,4})?)") && !list[2]) {             //Проверка на дату формата 1       (слитного)
                smsTexts[1] += word + "\ttдата\n";
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

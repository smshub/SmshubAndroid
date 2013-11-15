package com.donhuan.SmshubAndroid;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.*;

public class MyActivity extends Activity implements OnCheckedChangeListener {
    public static final String EXAMPLE_TEST1 = "ЗСКБ 9876 11янв 13:02 оплата 500р, остаток 5200.50р.";
    public static final String EXAMPLE_TEST2 = "VISA 8339: 31.10.13 09:11 покупка на сумму 500 руб. PIZZA HUT PETROGRADSKAYA выполненна успешно. Доступно: 3417.83 руб.";
    TextView tvHello;
    ToggleButton toogleButton;
    boolean state;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toogleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        tvHello = (TextView) findViewById(R.id.textView1);
        toogleButton.setOnCheckedChangeListener(this);
    }

    protected void onResume() {
        super.onResume();
        //toogleButton.setChecked(readFile("is_active").equals("true"));
        if (state) {
            Toast.makeText(this, "true", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "false", Toast.LENGTH_LONG).show();
        }
        tvHello.setText(readFile("sms_file.txt"));
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
        super.onPause();
        if (state) {
            createInfoNotification("Smshub", "Smshub", "экран включен", 101, state);
            writeFile("true", "is_active");
        } else {
            createInfoNotification("Smshub", "Smshub", "экран отключен", 101, state);
            writeFile("false", "is_active");
        }

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

    private void writeFile(String titleText, String filename) {
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

    public void createInfoNotification(String ticker, String content, String message, int id, boolean bool) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // Создаем экземпляр менеджера уведомлений
        int icon;
        if (bool) {
            icon = R.drawable.image_on;
        } else {
            icon = R.drawable.image_off;
        }
        CharSequence tickerText = ticker;
        CharSequence contentTitle = content;
        CharSequence contentText = message;


        long when = System.currentTimeMillis();                                                     //Выясним системное время
        Notification notification = new Notification(icon, tickerText, when);                       //Создаем экземпляр уведомления, и передаем ему наши параметры
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;                  //Текущее уведомление
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(this, MyActivity.class);                             //Создаем экземпляр Intent
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);         //Передаем в наше уведомление параметры вида при развернутой строке состояния
        mNotificationManager.notify(id, notification);                                              //И наконец показываем наше уведомление через менеджер передав его ID
    }
}

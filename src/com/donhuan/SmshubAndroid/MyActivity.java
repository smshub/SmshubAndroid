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
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

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
        readSmsList();
    }

    private void readSmsList() {
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput("sms_file.txt")));
            String str;
            String text = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                text += str + "\n";
            }
            tvHello.setText(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected void onPause() {
        super.onPause();
        if (true) {
            createInfoNotification("Smshub", "Smshub", "экран включен", 101, state);
        } else {
            createInfoNotification("Smshub", "Smshub", "экран отключен", 101, state);
        }

    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            startService(new Intent(this, SmsService.class));
            state = true;
        } else {
            stopService(new Intent(this, SmsService.class));
            state = false;
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

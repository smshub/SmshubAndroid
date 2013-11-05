package com.donhuan.SmshubAndroid;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MyActivity extends Activity {
    public static final String EXAMPLE_TEST1 = "ЗСКБ 9876 11янв 13:02 оплата 500р, остаток 5200.50р.";
    public static final String EXAMPLE_TEST2 = "VISA 8339: 31.10.13 09:11 покупка на сумму 500 руб. PIZZA HUT PETROGRADSKAYA выполненна успешно. Доступно: 3417.83 руб.";
    TextView tvHello;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    }

    protected void onPause() {
        super.onPause();
        createInfoNotification("Smshub", "Smshub включен", "", 101);
    }

    public void startSer(View v) {
        startService(new Intent(this, SmsService.class));
    }

    public void stopSer(View v) {
        stopService(new Intent(this, SmsService.class));
    }

    public void createInfoNotification(String ticker, String content, String message, int id) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // Создаем экземпляр менеджера уведомлений
        int icon = android.R.drawable.sym_action_email;
        CharSequence tickerText = ticker;
        CharSequence contentTitle = content;
        CharSequence contentText = message;

        long when = System.currentTimeMillis(); // Выясним системное время
        Notification notification = new Notification(icon, tickerText, when); // Создаем экземпляр уведомления, и передаем ему наши параметры
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;//Текущее уведомление
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(this, MyActivity.class); // Создаем экземпляр Intent
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent); // Передаем в наше уведомление параметры вида при развернутой строке состояния
        mNotificationManager.notify(id, notification); // И наконец показываем наше уведомление через менеджер передав его ID
    }

}

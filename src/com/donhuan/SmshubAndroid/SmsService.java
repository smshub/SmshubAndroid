package com.donhuan.SmshubAndroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.gsm.SmsMessage;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: Dh
 * Date: 04.11.13
 * Time: 20:49
 * To change this template use File | Settings | File Templates.
 */
public class SmsService extends Service {
    CommonFunctions commonFunctions = new CommonFunctions();

    @Override
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            if (bundle != null) {
                //---извлечь полученное SMS ---
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                String str = "";
                String addr = "";
                for (int i = 0; i < msgs.length; i++) {

                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    addr = msgs[i].getOriginatingAddress();
                    str += msgs[i].getMessageBody();
                    str += "\n";
                }
                String items[] = commonFunctions.scanMessage(str);                          // вызвращает вектор стрингов с распознанными значениями

                if (items.length == 7) {
                    String smsText = "";
                    for (String item : items) smsText += item + "\n";
//                    writeFile(readFile("sms_texts.txt") + smsText, "sms_texts.txt");        //добавляем в файл!!!!!проверить!!!!!
//                    writeFile(readFile("sms_addrs.txt") + addr + "\n", "sms_addrs.txt");        //добавляем в файл!!!!!проверить!!!!!
                }
            }

        }
    };


    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mIntentReceiver, filter);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Smshub отключен", Toast.LENGTH_LONG).show();
        createInfoNotification("Smshub", "Smshub", "экран отключен", 101, false);
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Smshub включен", Toast.LENGTH_LONG).show();

        createInfoNotification("Smshub", "Smshub", "экран включен", 101, true);
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

        long when = System.currentTimeMillis();                                             // Выясним системное время
        Notification notification = new Notification(icon, tickerText, when);               // Создаем экземпляр уведомления, и передаем ему наши параметры
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;          //Текущее уведомление
        Context context = getApplicationContext();

        Intent intent = new Intent(this, MyActivity.class);                                 // Создаем экземпляр Intent

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent); // Передаем в наше уведомление параметры вида при развернутой строке состояния
        mNotificationManager.notify(id, notification);                                      // И наконец показываем наше уведомление через менеджер передав его ID
    }
}

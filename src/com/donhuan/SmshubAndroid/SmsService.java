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

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Dh
 * Date: 04.11.13
 * Time: 20:49
 * To change this template use File | Settings | File Templates.
 */
public class SmsService extends Service {
    String[] basePayOfWords = {"оплата", "сумму"};
    String[] baseBalanceOfWords = {"остаток", "Доступно:", "доступно:", "доступно"};

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
                    //str += " :";
                    str += msgs[i].getMessageBody();
                    str += "\n";
                }
                double[] amounts = scanMessage(str);
                if (amounts[0] != -1 && amounts[1] != -1) {
                    long when = System.currentTimeMillis();                                             //системное время!!!!!! Надо заменить на время из sms
                    String titleText = "Отправитель: " + addr + "\n";
                    titleText += "Дата:" + when + "\n";
                    titleText += "      -сумма оплаты: " + amounts[0] + "\n";
                    titleText += "      -сумма статка: " + amounts[1] + "\n\n";
                    titleText += readSmsList();
                    //выдаем оповещение
                    writeSmsList(titleText);
                    createInfoNotification("Smshub", "Smshub: " + addr, "Сообщение распознанно", 101, true);

                } else {
                    //выдаем оповещение
                    String titleText = "Формат сообщения не соответствует известному";
                    createInfoNotification("Smshub", "Smshub: ", titleText, 101, true);
                }
            }

        }
    };

    private String readSmsList() {
        String text = "";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput("sms_file.txt")));
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

    private void writeSmsList(String titleText) {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput("sms_file.txt", MODE_PRIVATE)));
            // пишем данные
            bw.write(titleText);
            // закрываем поток
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


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

    private int equalsWords(String word) {
        for (String str : basePayOfWords) {
            if (str.equals(word)) return 0;
        }
        for (String str : baseBalanceOfWords) {
            if (str.equals(word)) return 1;
        }
        return -1;
    }


    public double[] scanMessage(String message) {
        String[] splitString = (message.split("\\s+"));
        String oldWord = "";

        double payment = -1;
        double balance = -1;

        for (String word : splitString) {
            if (equalsWords(oldWord) == 0) {
                payment = readAmounts(word);
            }
            if (equalsWords(oldWord) == 1) {
                balance = readAmounts(word);
            }
            oldWord = word;
        }
        return new double[]{payment, balance};
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

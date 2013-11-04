package com.donhuan.SmshubAndroid;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.view.View;
import android.widget.TextView;

public class MyActivity extends Activity {
    public static final String EXAMPLE_TEST1 = "ЗСКБ 9876 11янв 13:02 оплата 500р, остаток 5200.50р.";
    public static final String EXAMPLE_TEST2 = "VISA 8339: 31.10.13 09:11 покупка на сумму 500 руб. PIZZA HUT PETROGRADSKAYA выполненна успешно. Доступно: 3417.83 руб.";
    TextView tvHello;

    String[] basePayOfWords = {"оплата", "сумму"};
    String[] baseBalanceOfWords = {"остаток", "Доступно:", "доступно:", "доступно"};

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
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    str += "SMS from " + msgs[i].getOriginatingAddress();
                    str += " :";
                    str += msgs[i].getMessageBody();
                    str += "\n";
                }
                double[] amounts = scanMessage(str);
                if (amounts[0] != -1 && amounts[1] != -1) {
                    String titleText = "Сумма оплаты: " + amounts[0] + "\n";
                    titleText = titleText + "Сумма статка: " + amounts[1] + "\n";
                    tvHello.setText(titleText);
                } else {
                    tvHello.setText("Формат сообщения не соответствует\nизвестному формату сообщений об оплате");
                }
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tvHello = (TextView) findViewById(R.id.textView1);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");

        registerReceiver(mIntentReceiver, filter);

    }


    public void onClick(View v) {
        double[] amounts = scanMessage(EXAMPLE_TEST2);
        if (amounts[0] != -1 && amounts[1] != -1) {
            String titleText = "Сумма оплаты: " + amounts[0] + "\n";
            titleText = titleText + "Сумма статка: " + amounts[1] + "\n";
            tvHello.setText(titleText);
        } else {
            tvHello.setText("Формат сообщения не соответствует\nизвестному формату сообщений об оплате");
        }
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


    private double[] scanMessage(String message) {
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

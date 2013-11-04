package com.donhuan.SmshubAndroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: Dh
 * Date: 04.11.13
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */

public class SmsReceiver extends BroadcastReceiver {
    String str = "null";

    @Override
    public void onReceive(Context context, Intent intent) {
        //---получить входящее SMS сообщение---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        if (bundle != null) {
            //---извлечь полученное SMS ---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += "SMS from " + msgs[i].getOriginatingAddress();
                str += " :";
                str += msgs[i].getMessageBody();
                str += "\n";
            }

//            double [] amounts = myActivity.scanMessage(str);
//
//            String titleText = "Сумма оплаты: " + amounts[0]  + "\n";
//            titleText = titleText + "Сумма статка: " + amounts[1]  + "\n";

            //---Показать новое SMS сообщение---

            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();

        }
    }
}

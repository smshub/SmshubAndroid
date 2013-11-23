package com.donhuan.SmshubAndroid;

import android.preference.PreferenceActivity;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: Dh
 * Date: 23.11.13
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
 */
public class CommonFunctions extends PreferenceActivity {
    public void writeFile(String titleText, String filename) {
//        try {
//            // отрываем поток для записи
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput(filename, MODE_PRIVATE)));
//            // пишем данные
//            bw.write(titleText);
//            // закрываем поток
//            bw.close();
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
        Toast.makeText(this, "woohoooo", Toast.LENGTH_LONG).show();
    }
}

package com.donhuan.SmshubAndroid;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

public class CommonFunctions {

    ContentResolver contentResolver;
    Uri SMSBASE_URI;
    String LOG_TAG = "CommonFunctions";


    CommonFunctions (ContentResolver contentResolver, Uri SMSBASE_URI)
    {
        this.contentResolver = contentResolver;
        this.SMSBASE_URI = SMSBASE_URI;
    }


    public void putInfoToDB (String BANKNAME, String BANKNUM, String STORENAME, String DATE, String TIME, String SPENDMON, String RESTMON)
    {
        SMSDataBaseProvider sqh = new SMSDataBaseProvider();
        //---- Работа с базой данных
        ContentValues cv = new ContentValues();
        cv.put(SMSDataBaseProvider.BANKNAME, BANKNAME);
        cv.put(SMSDataBaseProvider.BANKNUM, BANKNUM);
        cv.put(SMSDataBaseProvider.STORENAME, STORENAME);
        cv.put(SMSDataBaseProvider.DATE, DATE);
        cv.put(SMSDataBaseProvider.TIME, TIME);
        cv.put(SMSDataBaseProvider.SPENDMON, SPENDMON);
        cv.put(SMSDataBaseProvider.RESTMON, RESTMON);
        cv.put(SMSDataBaseProvider.ISINFIN, "0"); //было ли сообщение добавлено в финансисто

        Uri newUri = contentResolver.insert(SMSBASE_URI, cv);
        Log.d(LOG_TAG, "insert, result Uri : " + newUri.toString());
  }

}

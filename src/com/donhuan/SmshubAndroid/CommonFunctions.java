package com.donhuan.SmshubAndroid;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created with IntelliJ IDEA.
 * User: Роман
 * Date: 07.12.13
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
public class CommonFunctions {



    public static void putInfoToDB ( DataBaseClass sqh,  String fields[] ) {

        ContentValues cv = new ContentValues();
        sqh.BANKNAME = fields[0];
        sqh.BANKNUM = fields[1];
        sqh.STORENAME = fields[2];
        sqh.DATE = fields[3];
        sqh.TIME = fields[4];
        sqh.SPENDMON = fields[5];
        sqh.RESTMON = fields[6];

        SQLiteDatabase sqdb = sqh.getWritableDatabase();

        sqdb.insert(sqh.TABLE_NAME,sqh.BANKNAME, cv );
        sqdb.insert(sqh.TABLE_NAME, sqh.BANKNUM,  cv);
        sqdb.insert(sqh.TABLE_NAME, sqh.STORENAME, cv);
        sqdb.insert(sqh.TABLE_NAME, sqh.DATE,cv);
        sqdb.insert(sqh.TABLE_NAME, sqh.TIME, cv);
        sqdb.insert(sqh.TABLE_NAME, sqh.SPENDMON, cv);
        sqdb.insert(sqh.TABLE_NAME, sqh.RESTMON, cv);
    }

}

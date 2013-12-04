package com.donhuan.SmshubAndroid;

/**
 * Created with IntelliJ IDEA.
 * User: Роман
 * Date: 03.12.13
 * Time: 2:57
 * To change this template use File | Settings | File Templates.
 */


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DataBaseClass extends SQLiteOpenHelper  implements BaseColumns {

    // Константы для конструктора
    // Имя базы данных
    private static final String DATABASE_NAME = "SmshubDataBase.db";
    // Номер версии базы данных
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "contact_table";

    //  Поля базы данных
    //public static final String UID = "_id";
    public static final String BANKNAME = "bankname";
    public static final String BankNum = "banknum";
    public static final String StoreName = "storename";
    public static final String Date = "date";
    public static final String Time = "time";
    public static final String SpendMon = "spendmon";
    public static final String RestMon = "restmon";


    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + TABLE_NAME + " (" + DataBaseClass._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + BANKNAME + " VARCHAR(255));";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
            + TABLE_NAME;

    public DataBaseClass(Context context) {
        // TODO Auto-generated constructor stub
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Метод для создания базы данных
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    // Метод для обновления базы, при выпуске новых версий программы
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.w("LOG_TAG", "Обновление базы данных с версии " + oldVersion
                + " до версии " + newVersion + ", которое удалит все старые данные");
        // Удаляем предыдущую таблицу при апгрейде
        db.execSQL(SQL_DELETE_ENTRIES);
        // Создаём новый экземпляр таблицы
        onCreate(db);
    }
}

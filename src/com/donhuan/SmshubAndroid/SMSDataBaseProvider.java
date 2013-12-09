package com.donhuan.SmshubAndroid;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class SMSDataBaseProvider extends ContentProvider {
    final String LOG_TAG = "myLogs";

    // // Константы для БД
    // БД
    static final String DB_NAME = "SmshubDataBase";
    static final int DB_VERSION = 1;

    // Таблица
    static final String TABLE_NAME = "smsdata";

    // Поля
    static final String SMSDATA_ID = "_id";
    static final String BANKNAME = "bankname";
    static final String BANKNUM = "banknum";
    static final String STORENAME = "storename";
    static final String DATE = "date";
    static final String TIME = "time";
    static final String SPENDMON = "spendmon";
    static final String RESTMON = "restmon";

    // Скрипт создания таблицы
    static final String DB_CREATE = "create table " + TABLE_NAME + "("
            + SMSDATA_ID + " integer primary key autoincrement, "
            + BANKNAME + " text, " + BANKNUM + " text, " + STORENAME
            + " text, " + DATE + " text, " + TIME + " text, " + SPENDMON
            + " text, " + RESTMON + " text" + ");";

    // // Uri
    // authority
    static final String AUTHORITY = "com.donhuan.SmshubAndroid.SMSDataBaseProvider";

    // path
    static final String SMSDATA_PATH = "smsdata";

    // Общий Uri
    public static final Uri SMSDATA_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + SMSDATA_PATH);

    // Типы данных
    // набор строк
    static final String SMSDATA_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + SMSDATA_PATH;

    // одна строка
    static final String SMSDATA_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + SMSDATA_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_SMSDATA = 1;

    // Uri с указанным ID
    static final int URI_SMSDATA_ID = 2;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, SMSDATA_PATH, URI_SMSDATA);
        uriMatcher.addURI(AUTHORITY, SMSDATA_PATH + "/#", URI_SMSDATA_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate");
        dbHelper = new DBHelper(getContext());
        return true;
    }

    // чтение
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "query, " + uri.toString());
        // проверяем Uri
        switch (uriMatcher.match(uri)) {
            case URI_SMSDATA: // общий Uri
                Log.d(LOG_TAG, "URI_SMSDATA");
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = BANKNAME + " ASC";
                }
                break;
            case URI_SMSDATA_ID: // Uri с ID
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_SMSDATA_ID, " + id);
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = SMSDATA_ID + " = " + id;
                } else {
                    selection = selection + " AND " + SMSDATA_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);
        // просим ContentResolver уведомлять этот курсор
        // об изменениях данных в SMSDATA_CONTENT_URI
        cursor.setNotificationUri(getContext().getContentResolver(),
                SMSDATA_CONTENT_URI);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "insert, " + uri.toString());
        if (uriMatcher.match(uri) != URI_SMSDATA)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(TABLE_NAME, null, values);
        Uri resultUri = ContentUris.withAppendedId(SMSDATA_CONTENT_URI, rowID);
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "delete, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_SMSDATA:
                Log.d(LOG_TAG, "URI_SMSDATA");
                break;
            case URI_SMSDATA_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_SMSDATA_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = SMSDATA_ID + " = " + id;
                } else {
                    selection = selection + " AND " + SMSDATA_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(LOG_TAG, "update, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_SMSDATA:
                Log.d(LOG_TAG, "URI_SMSDATA");

                break;
            case URI_SMSDATA_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_SMSDATA_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = SMSDATA_ID + " = " + id;
                } else {
                    selection = selection + " AND " + SMSDATA_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public String getType(Uri uri) {
        Log.d(LOG_TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_SMSDATA:
                return SMSDATA_CONTENT_TYPE;
            case URI_SMSDATA_ID:
                return SMSDATA_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
            }


        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
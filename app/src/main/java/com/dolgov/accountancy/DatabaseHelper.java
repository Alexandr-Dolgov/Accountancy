package com.dolgov.accountancy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Alexandr on 26.04.2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "Accountancy";


    public DatabaseHelper(Context context) {
        super(context, "accountancy.db", null, 1);
        Log.d(TAG, "конструктор");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "open or create database");
        // создаем таблицу с полями
        db.execSQL("create table mytable ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "email text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Обновление базы данных. " +
                "НЕ ДОЛЖНО ВЫЗЫВАТЬСЯ, т.к. я нигде не обновляю БД");
    }
}

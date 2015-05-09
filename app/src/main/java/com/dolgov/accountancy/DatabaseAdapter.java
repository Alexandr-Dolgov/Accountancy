package com.dolgov.accountancy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatabaseAdapter {

    private final String TAG = this.getClass().getName();

    private static final String DATABASE_NAME = "accountancy.db";
    private static final int DATABASE_VERSION = 7;

    private static final String TABLE_NAME = "mytable";

    private static final String ID_COLUMN = "_id";
    private static final String DATE_COLUMN = "date";
    private static final String RECEIPT_COLUMN = "receipt";
    private static final String PREPARED_COLUMN = "prepared";
    private static final String REMAINDER_COLUMN = "remainder";
    private static final String SOLD_COLUMN = "sold";
    private static final String WRITE_OFF_COLUMN = "writeOff";
    private static final String PRODUCT_COLUMN = "product";
    private static final String MONEY_COLUMN = "money";

    private SQLiteDatabase database;

    public DatabaseAdapter (Context context) {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        database = dbOpenHelper.getWritableDatabase();
    }

    public void insert(Record record){
        Log.d(TAG, "insert(Record record)");
        ContentValues cv = new ContentValues();
        cv.put(DATE_COLUMN, record.getDate().getTime());
        cv.put(RECEIPT_COLUMN, record.getReceipt());
        cv.put(PREPARED_COLUMN, record.getPrepared());
        cv.put(REMAINDER_COLUMN, record.getRemainder());
        cv.put(SOLD_COLUMN, record.getSold());
        cv.put(WRITE_OFF_COLUMN, record.getWriteOff());
        cv.put(PRODUCT_COLUMN, record.getProduct());
        cv.put(MONEY_COLUMN, record.getMoney());
        database.insert(TABLE_NAME, null, cv);
    }

    public Record getLastRecord(){
        Log.d(TAG, "---getLastRecord()---");
        String sql = "SELECT * " +
                " FROM " + TABLE_NAME +
                " WHERE date=(SELECT MAX(date) FROM " + TABLE_NAME + " )";
        Cursor cursor = database.rawQuery(sql, null);
        print(cursor);

        Record record = getRecord(cursor);
        Log.d(TAG, "record = " + record);

        cursor.close();

        return record;
    }

    public Record getPrevRecord(Record currentRecord){
        Log.d(TAG, "---getPrevRecord(Record currentRecord)---");
        Date prevDate = currentRecord.getPrevDate();
        long unixTime = prevDate.getTime();
        String sql = "SELECT * " +
                " FROM " + TABLE_NAME +
                " WHERE date = " + unixTime;
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return getRecord(cursor);
        }

        return null;
    }

    public Record getNextRecord(Record currentRecord) {
        Log.d(TAG, "---getNextRecord(Record currentRecord)---");
        Date nextDate = currentRecord.getNextDate();
        long unixTime = nextDate.getTime();
        String sql = "SELECT * " +
                " FROM " + TABLE_NAME +
                " WHERE date = " + unixTime;
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return getRecord(cursor);
        }

        return null;
    }

    public void selectAll(){
        Log.d(TAG, "---selectAll()---");
        String sql = "SELECT * " +
                " FROM " + TABLE_NAME;
        Cursor cursor = database.rawQuery(sql, null);
        print(cursor);
        cursor.close();
    }

    private Record getRecord(Cursor cursor){
        Date date = new Date();
        date.setTime(cursor.getLong(cursor.getColumnIndex(DATE_COLUMN)));
        double receipt = cursor.getDouble(cursor.getColumnIndex(RECEIPT_COLUMN));
        double prepared = cursor.getDouble(cursor.getColumnIndex(PREPARED_COLUMN));
        double remainder = cursor.getDouble(cursor.getColumnIndex(REMAINDER_COLUMN));
        double sold = cursor.getDouble(cursor.getColumnIndex(SOLD_COLUMN));
        double writeOff = cursor.getDouble(cursor.getColumnIndex(WRITE_OFF_COLUMN));
        double product = cursor.getDouble(cursor.getColumnIndex(PRODUCT_COLUMN));
        double money = cursor.getDouble(cursor.getColumnIndex(MONEY_COLUMN));

        return new Record(date, receipt, prepared, remainder, sold, writeOff, product, money);
    }

    private void print(Cursor cursor){
        cursor.moveToFirst();
        do {
            int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN));
            long date = cursor.getLong(cursor.getColumnIndex(DATE_COLUMN));
            double receipt = cursor.getDouble(cursor.getColumnIndex(RECEIPT_COLUMN));
            double prepared = cursor.getDouble(cursor.getColumnIndex(PREPARED_COLUMN));
            double remainder = cursor.getDouble(cursor.getColumnIndex(REMAINDER_COLUMN));
            double sold = cursor.getDouble(cursor.getColumnIndex(SOLD_COLUMN));
            double writeOff = cursor.getDouble(cursor.getColumnIndex(WRITE_OFF_COLUMN));
            double product = cursor.getDouble(cursor.getColumnIndex(PRODUCT_COLUMN));
            double money = cursor.getDouble(cursor.getColumnIndex(MONEY_COLUMN));
            String res =
                    "id = " + id +
                            " date = " + date +
                            " receipt = " + receipt +
                            " prepared = " + prepared +
                            " remainder = " + remainder +
                            " sold = " + sold +
                            " writeOff = " + writeOff +
                            " product = " + product +
                            " money = " + money;
            Log.d(TAG, res);
        } while (cursor.moveToNext());
        cursor.moveToFirst();
    }

    private class DBOpenHelper extends SQLiteOpenHelper {

        public DBOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d(TAG, "конструктор");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "создание БД: создание таблицы");
            // создаем таблицу с полями
            String sql = "CREATE TABLE " + TABLE_NAME + " ( " +
                    ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DATE_COLUMN + " INTEGER NOT NULL, " +
                    RECEIPT_COLUMN + " REAL NOT NULL, " +
                    PREPARED_COLUMN + " REAL NOT NULL, " +
                    REMAINDER_COLUMN + " REAL NOT NULL, " +
                    SOLD_COLUMN + " REAL NOT NULL, " +
                    WRITE_OFF_COLUMN + " REAL NOT NULL, " +
                    PRODUCT_COLUMN +" REAL NOT NULL, " +
                    MONEY_COLUMN + " REAL NOT NULL);";
            db.execSQL(sql);
            long date = (new GregorianCalendar(2015, Calendar.APRIL, 22)).getTime().getTime();
            db.execSQL("INSERT INTO " + TABLE_NAME +
                    " (date, receipt, prepared, remainder, sold, writeOff, product, money) " +
                    " VALUES ( " + date + ", 0, 1689.2, 90, 2300, 0, 21002.92, 19075.52);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "обновление БД: удаление таблицы");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

}

package com.dolgov.accountancy;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatabaseAdapter {

    private final String TAG = this.getClass().getName();

    private static final String DATABASE_NAME = "accountancy.db";
    private static final int DATABASE_VERSION = 14;

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
    private Activity activity;

    public DatabaseAdapter (Context context, Activity activity) {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        database = dbOpenHelper.getWritableDatabase();
        this.activity = activity;
    }

    public void insert(Record record){
        Log.d(TAG, "---insert(Record record)---");
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
        Log.d(TAG, "---insert(Record record) end---");
    }

    public Record getLastRecord(){
        Log.d(TAG, "---getLastRecord()---");
        String sql = "SELECT * " +
                " FROM " + TABLE_NAME +
                " WHERE _id=(SELECT MAX(_id) FROM " + TABLE_NAME + " )";
        Cursor cursor = database.rawQuery(sql, null);
        print(cursor);
        printSelectAll();

        Record record = getRecord(cursor);
        Log.d(TAG, "record = " + record);

        cursor.close();

        Log.d(TAG, "---getLastRecord() end---");
        return record;
    }

    private int getRecordID(Record record){
        long unixTime = record.getDate().getTime();
        String sql = "SELECT * " +
                " FROM " + TABLE_NAME +
                " WHERE date = " + unixTime;
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0){
            return -1;
        } else {
            return cursor.getInt(cursor.getColumnIndex(ID_COLUMN));
        }
    }

    public Record getPrevRecord(Record currentRecord){
        Log.d(TAG, "---getPrevRecord(Record currentRecord)---");
        Log.d(TAG, "id = " + getRecordID(currentRecord));
        String sql = "SELECT * " +
                " FROM " + TABLE_NAME +
                " WHERE _id = " + (getRecordID(currentRecord) - 1);
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            Log.d(TAG, "---getPrevRecord(Record currentRecord) end---");
            return getRecord(cursor);
        }

        Log.d(TAG, "---getPrevRecord(Record currentRecord) end---");
        return null;
    }

    public Record getNextRecord(Record currentRecord) {
        Log.d(TAG, "---getNextRecord(Record currentRecord)---");
        printSelectAll();
        String sql = "SELECT * " +
                " FROM " + TABLE_NAME +
                " WHERE _id = " + (getRecordID(currentRecord) + 1);
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            Log.d(TAG, "---getNextRecord(Record currentRecord) end---");
            return getRecord(cursor);
        }

        Log.d(TAG, "---getNextRecord(Record currentRecord) end---");
        return null;
    }

    public void printSelectAll(){
        Log.d(TAG, "----printSelectAll()----");
        String sql = "SELECT * " +
                " FROM " + TABLE_NAME;
        Cursor cursor = database.rawQuery(sql, null);
        print(cursor);
        Log.d(TAG, "----printSelectAll() end----");
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
                            " date = " + date + "(" + Util.unixTimeToString(date) + ")" +
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

    private ArrayList<Record> getRecords(Cursor cursor){
        ArrayList<Record> arrayList = new ArrayList<>();
        cursor.moveToFirst();
        do {
            Date date =  new Date(cursor.getLong(cursor.getColumnIndex(DATE_COLUMN)));
            double receipt = cursor.getDouble(cursor.getColumnIndex(RECEIPT_COLUMN));
            double prepared = cursor.getDouble(cursor.getColumnIndex(PREPARED_COLUMN));
            double remainder = cursor.getDouble(cursor.getColumnIndex(REMAINDER_COLUMN));
            double sold = cursor.getDouble(cursor.getColumnIndex(SOLD_COLUMN));
            double writeOff = cursor.getDouble(cursor.getColumnIndex(WRITE_OFF_COLUMN));
            double product = cursor.getDouble(cursor.getColumnIndex(PRODUCT_COLUMN));
            double money = cursor.getDouble(cursor.getColumnIndex(MONEY_COLUMN));

            Record record = new Record(
                    date,
                    receipt,
                    prepared,
                    remainder,
                    sold,
                    writeOff,
                    product,
                    money
            );

            arrayList.add(record);

        } while (cursor.moveToNext());
        cursor.moveToFirst();

        return arrayList;
    }

    public File createXLS(int numMonth, int year) {
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet();

        int rowNum = 0;
        Row rowOfHeaders = sheet.createRow(rowNum);
        Cell[] cellsHeaders = new Cell[8];
        for (int i = 0; i < cellsHeaders.length; i++) {
            cellsHeaders[i] = rowOfHeaders.createCell(i);
        }
        cellsHeaders[0].setCellValue("дата");
        cellsHeaders[1].setCellValue("приход");
        cellsHeaders[2].setCellValue("приготовила");
        cellsHeaders[3].setCellValue("остаток");
        cellsHeaders[4].setCellValue("продала");
        cellsHeaders[5].setCellValue("хоз. нужды");
        cellsHeaders[6].setCellValue("продукты");
        cellsHeaders[7].setCellValue("деньги");
        //задаем границы для ечеек с загаловками
        for (Cell cellHeader : cellsHeaders) {
            CellStyle style = wb.createCellStyle();
            style.setBorderTop(CellStyle.BORDER_THIN);
            style.setBorderRight(CellStyle.BORDER_THIN);
            style.setBorderBottom(CellStyle.BORDER_THIN);
            style.setBorderLeft(CellStyle.BORDER_THIN);
            cellHeader.setCellStyle(style);
        }

        //находим все записи у которых год и месяц соответствую указанным
        long beginMonth = Util.unixTimeBeginMonth(year, numMonth);
        long endMonth = Util.unixTimeEndMonth(year, numMonth);
        String sql = "SELECT * " +
                " FROM " + TABLE_NAME +
                " WHERE (date >= " + beginMonth + ") AND (date <= " + endMonth + ");";
        Log.d(TAG, "sql = " + sql);
        Cursor cursor = database.rawQuery(sql, null);
        ArrayList<Record> records = getRecords(cursor);
        for (Record record : records) {
            rowNum++;
            Row row = sheet.createRow(rowNum);
            Cell[] cells = new Cell[8];
            for (int i = 0; i < cells.length; i++) {
                cells[i] = row.createCell(i);
            }
            cells[0].setCellValue(Util.unixTimeToString(record.getDate().getTime()));
            cells[1].setCellValue(record.getReceipt());
            cells[2].setCellValue(record.getPrepared());
            cells[3].setCellValue(record.getRemainder());
            cells[4].setCellValue(record.getSold());
            cells[5].setCellValue(record.getWriteOff());
            cells[6].setCellValue(record.getProduct());
            cells[7].setCellValue(record.getMoney());
            //задаем границы как прямые тонкие линии
            for (Cell cell : cells) {
                CellStyle style = wb.createCellStyle();
                style.setBorderTop(CellStyle.BORDER_THIN);
                style.setBorderRight(CellStyle.BORDER_THIN);
                style.setBorderBottom(CellStyle.BORDER_THIN);
                style.setBorderLeft(CellStyle.BORDER_THIN);
                cell.setCellStyle(style);
            }
            //устанавливаем формат ячеек как чило с двумя знаками после десятичного разделителя
            for (int i = 1; i < cells.length; i++) {
                CellStyle style = cells[i].getCellStyle();
                short format = wb.createDataFormat().getFormat("0.00");
                style.setDataFormat(format);
                cells[i].setCellStyle(style);
            }
        }

        //устанавливаем ширину для столбцов
        for (int i = 0; i < 8; i++) {
            sheet.setColumnWidth(i, 2634);
        }
        sheet.setColumnWidth(2, 2816);

        // Write the output to a file
        String dirPath = activity.getApplicationInfo().dataDir;
        String fileName = Util.monthToString(numMonth) + year + ".xls";
        File file = new File(dirPath, fileName);
        Log.d(TAG, "xlsFile AbsolutePath = " + file.getAbsolutePath());
        try {
            FileOutputStream out = new FileOutputStream(file);
            wb.write(out);
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        return file;
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
            long date = (new GregorianCalendar(2015, Calendar.APRIL, 29)).getTime().getTime();
            db.execSQL("INSERT INTO " + TABLE_NAME +
                    " (date, receipt, prepared, remainder, sold, writeOff, product, money) " +
                    " VALUES ( " + date + ", 11469.0, 2385.7, 474.4, 2000, 0, 31152.28, 9534.52);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "обновление БД: удаление таблицы");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

}

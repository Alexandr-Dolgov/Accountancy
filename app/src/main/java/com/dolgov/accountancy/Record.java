package com.dolgov.accountancy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Alexandr on 23.04.2015.
 */
public class Record {
    private Date date;
    private double receipt;     //приход
    private double prepared;    //приготовила
    private double remainder;   //остаток
    private double sold;        //продала
    private double writeOff;    //хоз. нужды
    private double product;
    private double money;

    public Record(
            Record prevRecord,
            double receipt,
            double prepared,
            double remainder,
            double sold,
            double writeOff){

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setGregorianChange(prevRecord.date);
        calendar.add(Calendar.DATE, 1);

        this.date = calendar.getTime();
        this.receipt = receipt;
        this.prepared = prepared;
        this.remainder = remainder;
        this.sold = sold;
        this.writeOff =writeOff;

        //calc: product, money
        this.product = prevRecord.product + receipt - (prepared / 1.1);
        this.money = prevRecord.money - receipt + sold - writeOff;
    }

    private Record(){}

    public static Record getFirst(){
        Record firstRecord = new Record();
        firstRecord.date = (new GregorianCalendar(2006, 5, 17)).getTime();
        firstRecord.receipt = 0;
        firstRecord.prepared = 682.8;
        firstRecord.remainder = 159;
        firstRecord.sold = 625;
        firstRecord.writeOff = 0;
        firstRecord.product = 2971.23;
        firstRecord.money = 1346;
        return firstRecord;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        sb.append("дата: ").append(sdf.format(date)).append("\n");
        sb.append("приход: ").append(receipt).append("\n");
        sb.append("приготовила: ").append(prepared).append("\n");
        sb.append("остаток: ").append(remainder).append("\n");
        sb.append("продала: ").append(sold).append("\n");
        sb.append("хоз. нужды: ").append(writeOff).append("\n");
        sb.append("продукты: ").append(prepared).append("\n");
        sb.append("деньги: ").append(money).append("\n");
        return sb.toString();
    }

    public double getProduct(){
        return product;
    }

    public double getMoney(){
        return money;
    }
}

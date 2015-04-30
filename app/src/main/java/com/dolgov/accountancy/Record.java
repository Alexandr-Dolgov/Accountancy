package com.dolgov.accountancy;

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
        this.date = prevRecord.getNextDate();
        this.receipt = receipt;
        this.prepared = prepared;
        this.remainder = remainder;
        this.sold = sold;
        this.writeOff =writeOff;

        //calc: product, money
        this.product = prevRecord.product + receipt - (prepared / 1.1);
        this.money = prevRecord.money - receipt + sold - writeOff;
    }

    public Record(
            Date date,
            double receipt,
            double prepared,
            double remainder,
            double sold,
            double writeOff,
            double product,
            double money){
        this.date = date;
        this.receipt = receipt;
        this.prepared = prepared;
        this.remainder = remainder;
        this.sold = sold;
        this.writeOff =writeOff;
        this.product = product;
        this.money = money;
    }

    private Record(){}

    public static Record getFirst(){
        Record firstRecord = new Record();
        firstRecord.date = (new GregorianCalendar(2006, Calendar.MAY, 17)).getTime();
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
        sb.append("дата: ").append(sdf.format(date)).append(" ");
        sb.append("приход: ").append(receipt).append(" ");
        sb.append("приготовила: ").append(prepared).append(" ");
        sb.append("остаток: ").append(remainder).append(" ");
        sb.append("продала: ").append(sold).append(" ");
        sb.append("хоз. нужды: ").append(writeOff).append(" ");
        sb.append("продукты: ").append(product).append(" ");
        sb.append("деньги: ").append(money).append(" ");
        return sb.toString();
    }

    public Date getNextDate(){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(this.date);
        calendar.add(Calendar.DATE, 1);
        return  calendar.getTime();
    }

    public Date getDate() {
        return date;
    }

    public double getReceipt() {
        return receipt;
    }

    public double getPrepared() {
        return prepared;
    }

    public double getRemainder() {
        return remainder;
    }

    public double getSold() {
        return sold;
    }

    public double getWriteOff() {
        return writeOff;
    }

    public double getProduct(){
        return product;
    }

    public double getMoney(){
        return money;
    }

    public Date getPrevDate() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(this.date);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }
}

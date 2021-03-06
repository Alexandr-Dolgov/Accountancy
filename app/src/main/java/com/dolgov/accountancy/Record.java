package com.dolgov.accountancy;

import java.text.SimpleDateFormat;
import java.util.Date;

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
            Date date,
            double receipt,
            double prepared,
            double remainder,
            double sold,
            double writeOff){
        this.date = date;
        this.receipt = receipt;
        this.prepared = prepared;
        this.remainder = remainder;
        this.sold = sold;
        this.writeOff =writeOff;

        //calc: product, money
        this.product = prevRecord.product + receipt - (prepared / 1.1);
        this.money = prevRecord.money - receipt + sold - writeOff;
    }

    public Record(Record prevRecord, Record currentRecord) {
        this.date = currentRecord.date;
        this.receipt = currentRecord.receipt;
        this.prepared = currentRecord.prepared;
        this.remainder = currentRecord.remainder;
        this.sold = currentRecord.sold;
        this.writeOff =currentRecord.writeOff;

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

    @Override
    public boolean equals(Object object){
        if (object == this) {
            return  true;
        }
        if (object instanceof Record) {
            return (date.equals(((Record) object).date)) &&
                    (((Record) object).receipt == receipt) &&
                    (((Record) object).prepared == prepared) &&
                    (((Record) object).remainder == remainder) &&
                    (((Record) object).sold == sold) &&
                    (((Record) object).writeOff == writeOff) &&
                    (((Record) object).product == product) &&
                    (((Record) object).money == money);
        }
        return false;
    }
}

package com.dolgov.accountancy;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Alexandr on 26.06.2015.
 */
public class Util {

    private static final String TAG = Util.class.getName();

    public static String unixTimeToString(long unixTime){
        Date date = new Date();
        date.setTime(unixTime);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(date);
    }

    public static String monthToString(int numMonth){
        String monthString;
        switch (numMonth) {
            case 0:  monthString = "Январь";
                break;
            case 1:  monthString = "Февраль";
                break;
            case 2:  monthString = "Март";
                break;
            case 3:  monthString = "Апрель";
                break;
            case 4:  monthString = "Май";
                break;
            case 5:  monthString = "Июнь";
                break;
            case 6:  monthString = "Июль";
                break;
            case 7:  monthString = "Август";
                break;
            case 8:  monthString = "Сентябрь";
                break;
            case 9:  monthString = "Октябрь";
                break;
            case 10: monthString = "Ноябрь";
                break;
            case 11: monthString = "Декабрь";
                break;
            default: monthString = "Invalid month";
                break;
        }
        return monthString;
    }

    public static int numPrevMonth(int numCurrentMonth){
        int numPrevMonth;
        if (numCurrentMonth == 0){
            numPrevMonth = 11;
        } else {
            numPrevMonth = numCurrentMonth - 1;
        }
        return numPrevMonth;
    }

    public static int yearOfPrevMonth(int numCurrentMonth, int yearOfCurrentMonth){
        if (numCurrentMonth == 0){
            return yearOfCurrentMonth - 1;
        } else {
            return yearOfCurrentMonth;
        }
    }

    //TODO проверить
    public static long unixTimeBeginMonth(int year, int month){
        Calendar calendar = new GregorianCalendar(year, month, 1);
        long unixTime = calendar.getTime().getTime();
        Log.d(TAG, "dateBeginMonth = " + unixTimeToString(unixTime));
        Log.d(TAG, "unixTimeBeginMonth = " + unixTime);
        return unixTime;
    }

    //TODO проверить
    public static long unixTimeEndMonth(int year, int month){
        Calendar calendar = new GregorianCalendar(year, month, 1);
        int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar = new GregorianCalendar(year, month, day);
        long unixTime = calendar.getTime().getTime();
        Log.d(TAG, "dateEndMonth = " + unixTimeToString(unixTime));
        Log.d(TAG, "unixTimeEndMonth = " + unixTime);
        return unixTime;
    }
}

package com.dolgov.accountancy;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alexandr on 26.06.2015.
 */
public class Util {
    public static String unixTimeToString(long unixTime){
        Date date = new Date();
        date.setTime(unixTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        return sdf.format(date);
    }
}

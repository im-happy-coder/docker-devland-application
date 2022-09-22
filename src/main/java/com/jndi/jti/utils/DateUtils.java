package com.jndi.jti.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String currentDate(String formater){
        SimpleDateFormat dateFormat = new SimpleDateFormat(formater);
        Date time = new Date();
        String currentDate = dateFormat.format(time);

        return  currentDate;
    }
}

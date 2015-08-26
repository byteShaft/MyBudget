package com.byteshaft.mybudget.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Helpers {

    public static String getTimeStamp(String dateFormat) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }
}

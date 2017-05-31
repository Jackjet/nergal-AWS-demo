package com.example.nergal.myaws.awss3v4.signaturev4;

/**
 * Created by nergal on 2017/5/22.
 */


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class DateUtils {
    public static final String COMPRESSED_DATE_PATTERN = "yyyyMMdd\'T\'HHmmss\'Z\'";
    private static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
    private static final Map<String, ThreadLocal<SimpleDateFormat>> SDF_MAP = new HashMap();

    public DateUtils() {
    }

    private static ThreadLocal<SimpleDateFormat> getSimpleDateFormat(final String pattern) {
        ThreadLocal sdf = (ThreadLocal)SDF_MAP.get(pattern);
        if(sdf == null) {
            Map var2 = SDF_MAP;
            synchronized(SDF_MAP) {
                sdf = (ThreadLocal)SDF_MAP.get(pattern);
                if(sdf == null) {
                    sdf = new ThreadLocal() {
                        protected SimpleDateFormat initialValue() {
                            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
                            sdf.setTimeZone(DateUtils.GMT_TIMEZONE);
                            sdf.setLenient(false);
                            return sdf;
                        }
                    };
                    SDF_MAP.put(pattern, sdf);
                }
            }
        }

        return sdf;
    }



    public static String format(String pattern, Date date) {
        return ((SimpleDateFormat)getSimpleDateFormat(pattern).get()).format(date);
    }


}

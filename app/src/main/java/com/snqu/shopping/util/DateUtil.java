package com.snqu.shopping.util;

import com.blankj.utilcode.constant.TimeConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author 张全
 */
public class DateUtil {
    private static SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    private static SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static SimpleDateFormat format3 = new SimpleDateFormat("dd天HH:mm:ss", Locale.getDefault());
    public static SimpleDateFormat format4 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static SimpleDateFormat format5 = new SimpleDateFormat("yyyy-MM-dd");

    public static String getFriendlyTimeStr2(long time) {
        Date date = new Date(time);
        return format5.format(date);
    }

    public static String getFriendlyTimeStr(long time) {
        Date date = new Date(time);
        long wee = getWeeOfToday();
        if (time >= wee) {
            if ((time - wee) >= 86400000) {
                return "次日" + format.format(date);
            } else {
                return "今天 " + format.format(date);
            }
        } else if (time >= wee - TimeConstants.DAY) {
            return "昨天 " + format.format(date);
        } else {
            return format2.format(date);
        }
    }

    private static long getWeeOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }


    public static long getTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }


    /**
     * 一周的时间戳
     *
     * @return
     */
    public static Long getWeekTimeStamp() {
        return 604800 * 1000L;
    }

}

package goldenbrother.gbmobile.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Exchanger;

/**
 * Created by asus on 2016/11/21.
 */

public class TimeHelper {
    private static final SimpleDateFormat sdf_standard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat YMD = new SimpleDateFormat("yyyy-MM-dd");

    private static final SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat sdf_content_date = new SimpleDateFormat("MM/dd");

    public static String getStandard() {
        return sdf_standard.format(new Date());
    }

    public static String getYMD() {
        return YMD.format(new Date());
    }

    public static String getYMD2YMDT(String ymd) {
        if (ymd == null) return "";
        try {
            return sdf_ymd.format(sdf_standard.parse(ymd));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Date getYMD2Date(String ymd) {
        try {
            return YMD.parse(ymd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDate2TMD(Date date) {
        return YMD.format(date);
    }

    public static String getYMDTime(String time) {
        if (time == null) return "";
        try {
            return sdf_ymd.format(sdf_standard.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getContentTime(String time) {
        try {
            Calendar now = Calendar.getInstance();
            Calendar date = Calendar.getInstance();
            date.setTime(sdf_standard.parse(time));
//            if (now.get(Calendar.YEAR) != date.get(Calendar.YEAR)) { // defferent
//                return time;
//            } else { // same year
            if (now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)) { // today
                int nowTotalMin = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
                int dateTotalMin = date.get(Calendar.HOUR_OF_DAY) * 60 + date.get(Calendar.MINUTE);
                if (nowTotalMin - dateTotalMin < 60) {
                    return (nowTotalMin - dateTotalMin <= 0 ? 1 : nowTotalMin - dateTotalMin) + " min ago";
                } else {
                    return ((nowTotalMin / 60) - (dateTotalMin / 60)) + " hour ago";
                }
            } else {
                return sdf_content_date.format(date.getTime());
            }
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getInitTime() {
        return "2016-01-01 00:00:00";
    }

    public static String addMinute(String str, int min) {
        try {
            SimpleDateFormat sdf_standard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = sdf_standard.parse(str);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.add(Calendar.MINUTE, min);
            return sdf_standard.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    // 01/01 02:55 AM
    public static String getTodayTime(String time) {
        try {
            Calendar now = Calendar.getInstance();
            Calendar date = Calendar.getInstance();
            date.setTime(sdf_standard.parse(time));

            if (now.get(Calendar.YEAR) != date.get(Calendar.YEAR)) { // defferent
                int y = date.get(Calendar.YEAR);
                int m = date.get(Calendar.MONTH) + 1;
                int d = date.get(Calendar.DAY_OF_MONTH);
                return (y < 10 ? "0" + y : y) + "/" + (m < 10 ? "0" + m : m) + "/" + (d < 10 ? "0" + d : d);
            } else { // same year
                if (now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)) { // today
                    int h = date.get(Calendar.HOUR_OF_DAY);
                    int M = date.get(Calendar.MINUTE);
                    return (h < 10 ? "0" + h : h) + ":" + (M < 10 ? "0" + M : M) + (h < 12 ? " AM" : " PM");
                } else {
                    int m = date.get(Calendar.MONTH) + 1;
                    int d = date.get(Calendar.DAY_OF_MONTH);
                    int h = date.get(Calendar.HOUR_OF_DAY);
                    int M = date.get(Calendar.MINUTE);
                    return (m < 10 ? "0" + m : m) + "/" + (d < 10 ? "0" + d : d) + " " + (h < 10 ? "0" + h : h) + ":" + (M < 10 ? "0" + M : M) + (h < 12 ? " AM" : " PM");
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int compare(String a, String b) {

        try {
            Date d1 = sdf_standard.parse(a);
            Date d2 = sdf_standard.parse(b);
            if (d1.after(d2) || d1.before(d2)) {
                return d1.after(d2) ? 1 : -1;
            } else { // same
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


}

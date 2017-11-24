package github.ivicel.zhihustory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ivicel on 12/10/2017.
 */

public class QueryPreferences {
    private static final String CURRENT_DAY = "current_day";
    private static final String OFFLINE_MODE = "offline_mode";
    // private static final String
    
    private QueryPreferences() {}
    
    public static String queryCurrentDay(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(CURRENT_DAY, null);
    }
    
    public static void saveCurrentDay(Context context, String date) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(CURRENT_DAY, date)
                .apply();
    }
    
    public static boolean isSetTodayOffline(Context context) {
        boolean offlineFlag = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(OFFLINE_MODE, false);
        return offlineFlag && isNewDay(context);
    }
    
    public static boolean isNewDay(Context context) {
        String saveDate = queryCurrentDay(context);
        String today = getToday();
        return today.equalsIgnoreCase(saveDate);
    }
    
    public static boolean isNewDay(Context context, String date) {
        String saveDate = queryCurrentDay(context);
        return !date.equalsIgnoreCase(saveDate);
    }
    
    private static String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        return dateFormat.format(new Date());
    }
    
}

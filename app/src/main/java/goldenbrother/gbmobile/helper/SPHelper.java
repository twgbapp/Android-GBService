package goldenbrother.gbmobile.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by asus on 2016/12/10.
 */

public class SPHelper {

    // fcm

    private static final String FILE_FCM = "fcm";
    private static final String KEY_FCM_TOKEN = "key_fcm_token";

    public static void setFcmToken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences(FILE_FCM, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_FCM_TOKEN, token).apply();
    }

    public static String getFcmToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_FCM, Context.MODE_PRIVATE);
        return sp.getString(KEY_FCM_TOKEN, "");
    }

    /*
    *   User
    */
    private static final String FILE_USER = "user";
    private static final String KEY_USER_INFO = "key_user_info";

    public static void setUser(Context context, JSONObject j) {
        SharedPreferences sp = context.getSharedPreferences(FILE_USER, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_USER_INFO, j.toString()).apply();
    }

    public static String getUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_USER, Context.MODE_PRIVATE);
        return sp.getString(KEY_USER_INFO, "");
    }

    public static void clearUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_USER, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    /*
    *   unread count
    */
    private static final String FILE_UNREAD = "unread";
    private static final String KEY_UNREAD_CHAT_COUNT = "key_unread_chat_count";
    private static final String KEY_UNREAD_EVENT_COUNT = "key_unread_event_count";

    public static void setChatUnReadCount(Context context, int count) {
        SharedPreferences sp = context.getSharedPreferences(FILE_UNREAD, Context.MODE_PRIVATE);
        sp.edit().putInt(KEY_UNREAD_CHAT_COUNT, count).apply();
    }

    public static void setEventUnReadCount(Context context, int count) {
        SharedPreferences sp = context.getSharedPreferences(FILE_UNREAD, Context.MODE_PRIVATE);
        sp.edit().putInt(KEY_UNREAD_EVENT_COUNT, count).apply();
    }

    public static int getChatUnReadCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_UNREAD, Context.MODE_PRIVATE);
        return sp.getInt(KEY_UNREAD_CHAT_COUNT, 0);
    }

    public static int getEventUnReadCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_UNREAD, Context.MODE_PRIVATE);
        return sp.getInt(KEY_UNREAD_EVENT_COUNT, 0);
    }

    public static int getUnReadCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_UNREAD, Context.MODE_PRIVATE);
        int chatCount = sp.getInt(KEY_UNREAD_CHAT_COUNT, 0);
        int eventCount = sp.getInt(KEY_UNREAD_EVENT_COUNT, 0);
        return chatCount + eventCount;
    }

    public static void clearUnReadCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_UNREAD, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
}

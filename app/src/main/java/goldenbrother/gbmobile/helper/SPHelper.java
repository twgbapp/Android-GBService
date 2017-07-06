package goldenbrother.gbmobile.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by asus on 2016/12/10.
 */

public class SPHelper {
    public static final String TAG = "$SPHelper$";
    private static SPHelper sp;
    private Context context;

    private SPHelper(Context context) {
        this.context = context;
    }

    public static SPHelper getInstance(Context context) {
        if (sp == null) {
            sp = new SPHelper(context);
        }
        return sp;
    }


    // fcm

    private static final String FILE_FCM = "fcm";
    private static final String KEY_FCM_TOKEN = "key_fcm_token";

    public void setGCMID(String token) {
        SharedPreferences sp = context.getSharedPreferences(FILE_FCM, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_FCM_TOKEN, token);
        editor.apply();
        Log.d(TAG, "Token Saved : " + token);
    }

    public String getGCMID() {
        SharedPreferences sp = context.getSharedPreferences(FILE_FCM, 0);
        return sp.getString(KEY_FCM_TOKEN, "");
    }

    /*
    *
    *   UserInfo
    *
    * */
    private static final String FILE_USER = "user";
    private static final String KEY_USER_INFO = "key_user_info";

    public void setUserInfo(JSONObject j) {
        SharedPreferences sp = context.getSharedPreferences(FILE_USER, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_USER_INFO, j.toString());
        editor.apply();
    }

    public String getUserInfo() {
        SharedPreferences sp = context.getSharedPreferences(FILE_USER, 0);
        return sp.getString(KEY_USER_INFO, "");
    }

    public void clearUserInfo() {
        SharedPreferences sp = context.getSharedPreferences(FILE_USER, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_USER_INFO, "");
        editor.apply();
    }

}

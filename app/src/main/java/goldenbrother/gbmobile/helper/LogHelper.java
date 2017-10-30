package goldenbrother.gbmobile.helper;


import android.util.Log;

public class LogHelper {
    private static final String TAG = "GBMLOG";
    private static boolean isDebuggable = true;

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (!isDebuggable) return;
        Log.d(tag, msg);
    }
}

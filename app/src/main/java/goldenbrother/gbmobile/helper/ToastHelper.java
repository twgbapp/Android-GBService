package goldenbrother.gbmobile.helper;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by asus on 2016/12/8.
 */

public class ToastHelper {
    //
    public static void t(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void t(Context context, int res) {
        Toast.makeText(context, context.getString(res), Toast.LENGTH_SHORT).show();
    }
}

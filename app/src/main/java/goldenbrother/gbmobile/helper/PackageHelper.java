package goldenbrother.gbmobile.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;


public class PackageHelper {
    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setBadge(Context context, int count) {
        LogHelper.d("Manufacturer : " + Build.MANUFACTURER);
        ShortcutBadger.applyCount(context, count);
    }
}

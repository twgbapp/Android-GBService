package goldenbrother.gbmobile.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 *
 */

public class PackageHelper {
    public static String getVersionName(Context context){
        PackageManager pm = context.getPackageManager();
        try{
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(),0);
            return packageInfo.versionName;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static int getVersionCode(Context context){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(),0);
            return packageInfo.versionCode;
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }
}

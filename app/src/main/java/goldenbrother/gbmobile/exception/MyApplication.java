package goldenbrother.gbmobile.exception;

import android.app.Application;
import android.content.Context;

/**
 * Created by rin84 on 2017/10/19.
 */

public class MyApplication extends Application {

    public static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
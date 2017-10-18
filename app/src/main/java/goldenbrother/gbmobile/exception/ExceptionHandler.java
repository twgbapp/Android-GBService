package goldenbrother.gbmobile.exception;

import android.app.Activity;

/**
 * Created by rin84 on 2017/10/19.
 */

public class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
    private Activity activity;

    public ExceptionHandler(Activity a) {
        activity = a;
    }
    // Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
    // add to every Activity
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
//        1
//        Intent intent = new Intent(activity, PermissionActivity.class);
//        intent.putExtra("crash", true);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getInstance().getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

//        AlarmManager mgr = (AlarmManager) MyApplication.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);

//        2
//        StringWriter stackTrace = new StringWriter();
//        exception.printStackTrace(new PrintWriter(stackTrace));
//        System.err.println(stackTrace);// You can use LogCat too
//        Intent intent = new Intent(myContext, myActivityClass);
//        String s = stackTrace.toString();
//        //you can use this String to know what caused the exception and in which Activity
//        intent.putExtra("uncaughtException",
//                "Exception is: " + stackTrace.toString());
//        intent.putExtra("stacktrace", s);
//        myContext.startActivity(intent);
        activity.finish();
        System.exit(0);
    }
}
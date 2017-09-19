package goldenbrother.gbmobile.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.EventChatActivity;
import goldenbrother.gbmobile.activity.MobileServiceActivity;
import goldenbrother.gbmobile.fragment.EventListFragment;
import goldenbrother.gbmobile.fragment.ServiceFragment;
import goldenbrother.gbmobile.fragment.ServiceListFragment;
import goldenbrother.gbmobile.helper.Constant;
import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.model.RoleInfo;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by asus on 2016/12/10.
 */

public class GBFirebaseMessagingService extends FirebaseMessagingService {

    public static final int NOTIFICATION_ID = 88;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        LogHelper.d("From: " + remoteMessage.getFrom());

        Map<String, String> map = remoteMessage.getData();
        if (map.size() > 0) {
            LogHelper.d("Message data payload: " + map);
        }

        if (remoteMessage.getNotification() != null) {
            LogHelper.d("Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        String userID = map.get("userID");
        String message = map.get("message");
        String type = map.get("type");
        FCMNotice.getInstance().notifyOnMessageReceived(message);
        String msg = message;
        if (message.equals(Constant.RATING)) {
            msg = getString(R.string.rating_request);
        } else if (message.contains(Constant.QR_MESSAGE)) {
            msg = getString(R.string.package_request);
        }
        sendNotification(getApplicationContext(), msg + "(" + type + ")");
    }


    public static void sendNotification(Context context, String message) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Intent to be launched on notification click
        Intent intent = new Intent(context, MobileServiceActivity.class);
//
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Intent intent = context.getPackageManager().getLaunchIntentForPackage("goldenbrother.gbmobile");

        int requestID = (int) System.currentTimeMillis();
        PendingIntent contentIntent = PendingIntent.getActivity(context, requestID,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context);

        String contentTitle = context.getString(R.string.app_name);
        String ticker = context.getString(R.string.app_name);
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        mBuilder.setContentTitle(contentTitle)          // title
                .setContentText(message)                // text
                .setTicker(ticker)                      // the thicker is the message that appears on the status bar when the notification first appears
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_notification_small)
                .setLargeIcon(bmp)
                .setDefaults(Notification.DEFAULT_ALL)  // use defaults for various notification settings
                //.setContentIntent(contentIntent)        // intent used on click
                .setAutoCancel(true)                    // if you want the notification to be dismissed when clicked
                .setOnlyAlertOnce(true); // don't play any sound or flash light if since we're updating


        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
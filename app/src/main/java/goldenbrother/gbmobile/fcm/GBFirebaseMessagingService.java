package goldenbrother.gbmobile.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.EventChatActivity;
import goldenbrother.gbmobile.activity.MainActivity;
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

    // type
    public static final String GROUP = "group";
    public static final String EVENT = "event";
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
        String type = map.get("type"); // group~event
        // notify
        FCMNotice.getInstance().notifyOnMessageReceived(message);
        // filter message
        String msg = message;
        if (message.equals(Constant.RATING)) {
            msg = getString(R.string.rating_request);
        } else if (message.contains(Constant.QR_MESSAGE)) {
            msg = getString(R.string.package_request);
        }
        if (type.equals(GROUP)) {
            msg += "(" + getString(R.string.main_drawer_chat) + ")";
        } else if (type.equals(EVENT)) {
            msg += "(" + getString(R.string.main_drawer_event_list) + ")";
        }
        sendNotification(getApplicationContext(), type, msg);
    }

    public static void sendNotification(Context context, String type, String message) {
        // open MainActivity params
        Bundle b = new Bundle();
        b.putBoolean("openMobileActivity", true);
        b.putString("type", type);

        // intent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(),
                new Intent(context, MainActivity.class)
                        .putExtras(b),
                PendingIntent.FLAG_ONE_SHOT
        );

        // notification builder
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle( context.getString(R.string.app_name))          // title
                .setContentText(message)                // text
                .setTicker(context.getString(R.string.app_name))                      // the thicker is the message that appears on the status bar when the notification first appears
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_notification_small)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))
                .setDefaults(Notification.DEFAULT_ALL)  // use defaults for various notification settings
                .setContentIntent(pendingIntent)        // intent used on click
                .setAutoCancel(true)                    // if you want the notification to be dismissed when clicked
                .setOnlyAlertOnce(true)
                .build(); // don't play any sound or flash light if since we're updating

        // send
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
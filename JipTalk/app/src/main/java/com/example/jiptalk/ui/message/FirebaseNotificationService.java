package com.example.jiptalk.ui.message;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.jiptalk.AppData;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseNotificationService extends FirebaseMessagingService {

    String TAG = "===";


    NotificationManager notificationManager;

    public FirebaseNotificationService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String msg = remoteMessage.getNotification().getBody();


        Log.d(TAG, "FCM message recieved : " + title + ", " + msg);
        String channelId = "channel";
        String channelName = "Channel_name";
        int importance = NotificationManager.IMPORTANCE_LOW;


//        notificationManager = NotificationManagerCompat.from(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setAutoCancel(true)
                .setVibrate(new long[]{1, 1000});

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(msg);

        mBuilder.setStyle(bigTextStyle);

        notificationManager.notify(0, mBuilder.build());
    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.d(TAG, "New Token : " + s);
        AppData.newToken = s;


    }
}

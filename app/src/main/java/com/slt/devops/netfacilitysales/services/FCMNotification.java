package com.slt.devops.netfacilitysales.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.slt.devops.netfacilitysales.R;

public class FCMNotification extends ContextWrapper {

    private static final String CHANNEL_ID = "Fcm Test";
    private static final String CHANNEL_NAME = "Fcm Test";
    private NotificationManager notificationManager;

    public FCMNotification(Context base) {
        super(base);

        /*if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }*/
        createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,  NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Fcm Test channel for app test FCM");
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        channel.setShowBadge(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager(){
        if(notificationManager == null){
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }


    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotification(String title, String body, PendingIntent pendingIntent, Uri soundUri, String icon){
        return  new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.nflogo2)
                .setTicker("Fcm Test")
                .setNumber(10)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setContentInfo("Info");
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotificationimg(String title, String body, Bitmap image, PendingIntent pendingIntent, Uri soundUri, String icon){
        return  new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.nflogo2)
                .setTicker("Fcm Test")
                .setNumber(10)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new Notification.BigPictureStyle()
                        .bigPicture(image))
                .setContentIntent(pendingIntent)
                .setContentInfo("Info");
    }
}
package com.example.stockmarketapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
    public static final String channel_1ID = "CHANNEL_1";
    public static final  String channel_2ID = "CHANNEL_2";
    public static final String channel1_Name = "Channel 1";
    public static final String channel2_Name = "Channel 2";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannels() {

        NotificationChannel channel1 = new NotificationChannel(channel_1ID, channel1_Name, NotificationManager.IMPORTANCE_HIGH);
        channel1.enableLights(true);
        channel1.enableVibration(true);
        channel1.setLightColor(R.color.colorPrimaryDark);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel1);


        NotificationChannel channel2 = new NotificationChannel(channel_2ID, channel2_Name, NotificationManager.IMPORTANCE_LOW);
        channel2.enableLights(true);
        channel2.enableVibration(false);
        channel2.setLightColor(R.color.colorAccent);
        channel2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel2);

    }

    public NotificationManager getManager(){

        if(mManager == null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return mManager;
    }

    public NotificationCompat.Builder getChannel1Notification (String Title, String message, String pre_open){
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent resultPendingIntent =  PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteView = new RemoteViews(getPackageName(),
                R.layout.notification_layout);
        remoteView.setTextViewText(R.id.not_stock_name, Title);
        remoteView.setTextViewText(R.id.not_value, message);
        remoteView.setTextViewText(R.id.prev_closed_value, pre_open);


        return new NotificationCompat.Builder(getApplicationContext(), channel_1ID)
                .setAutoCancel(true)
                .setCustomContentView(remoteView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setSmallIcon(R.drawable.ic_baseline_local_atm_24)
                .setContentIntent(resultPendingIntent);

    }
    public NotificationCompat.Builder getChannel2Notification (String Title, String message){

        RemoteViews remoteView = new RemoteViews(getPackageName(),
                R.layout.notification_layout);
        remoteView.setTextViewText(R.id.not_stock_name, Title);
        remoteView.setTextViewText(R.id.not_value, message);


        return new NotificationCompat.Builder(getApplicationContext(), channel_2ID)
                .setCustomContentView(remoteView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setSmallIcon(R.drawable.ic_baseline_local_atm_24);

    }
}

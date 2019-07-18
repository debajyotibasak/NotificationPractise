package com.debo.notificationtest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnNotifyMe;
    private Button btnCancel;
    private Button btnUpdate;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final String PRIMARY_CHANNEL_NAME = "mascot_notification";
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.debo.notificationtest.MainActivity.ACTION_UPDATE_NOTIFICATION";
    private static final String ACTION_DELETE_NOTIFICATION =
            "com.debo.notificationtest.MainActivity.ACTION_DELETE_NOTIFICATION";
    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotificationManager;
    private NotificationReceiver mReceiver = new NotificationReceiver();
    private ClearNotificationReceiver mClearNotificationsReceiver = new ClearNotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
        registerReceiver(mClearNotificationsReceiver, new IntentFilter(ACTION_DELETE_NOTIFICATION));

        btnNotifyMe = findViewById(R.id.notify);
        btnNotifyMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotifications();
            }
        });

        btnUpdate = findViewById(R.id.update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNotifications();
            }
        });

        btnCancel = findViewById(R.id.cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelNotifications();
            }
        });

        createNotificationChannel();
        setNotificationButtonState(true, false, false);
    }

    private void sendNotifications() {
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent);
        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, true, true);
    }

    private void updateNotifications() {
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated!"));
        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, false, true);
    }

    private void cancelNotifications() {
        mNotificationManager.cancel(NOTIFICATION_ID);
        setNotificationButtonState(true, false, false);
    }

    private void createNotificationChannel() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    PRIMARY_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text.")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setDeleteIntent(createPendingIntent())
                .setAutoCancel(true);
    }

    private void setNotificationButtonState(Boolean isNotifyEnabled,
                                            Boolean isUpdateEnabled,
                                            Boolean isCancelEnabled) {

        btnNotifyMe.setEnabled(isNotifyEnabled);
        btnUpdate.setEnabled(isUpdateEnabled);
        btnCancel.setEnabled(isCancelEnabled);
    }

    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotifications();
        }
    }

    public class ClearNotificationReceiver extends BroadcastReceiver {

        public ClearNotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            cancelNotifications();
        }
    }

    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(ACTION_DELETE_NOTIFICATION);
        return PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterReceiver(mClearNotificationsReceiver);
        super.onDestroy();
    }
}

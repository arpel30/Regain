package com.example.regain.Services_Listeners;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.regain.Activities.MainActivity;
import com.example.regain.R;

public class MyService extends Service {

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Log.d("aaa", "Service Started onStartCommand");
        startForeground();

        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        Log.d("aaa", "Service Started startForeground1");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        Log.d("aaa", "Service Started startForeground2");

        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
                NOTIF_CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background startForeground")
                .setContentIntent(pendingIntent)
                .build());
        Log.d("aaa", "Service Started startForeground3");

    }
}

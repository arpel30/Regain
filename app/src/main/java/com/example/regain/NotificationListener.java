package com.example.regain;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;

public class NotificationListener extends NotificationListenerService {

    static MyListener listener;
    int i = 0;
    String content = "";
    private Bundle extras;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
//        super.onNotificationPosted(sbn);
//        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)){
//            Parcelable b[] = (Parcelable[]) extras.get(Notification.EXTRA_MESSAGES);
//
//            if(b != null){
//                content="";
//                for (Parcelable tmp : b){
//
//                    Bundle msgBundle = (Bundle) tmp;
//                    content = content + msgBundle.getString("text") + "\n";
//                    Log.d("aaa", "Not :" + content);
//                    /*Set<String> io = msgBundle.keySet(); // To get the keys available for this bundle*/
//
//                }
//            }
//        }
        i++;
        try {
            String pack = sbn.getPackageName();
            String ticker = "";
            if (sbn.getNotification().tickerText != null) {
                ticker = sbn.getNotification().tickerText.toString();
            }
            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString("android.title");
            String text = extras.getCharSequence("android.text").toString();
//            int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);
//            Bitmap id = sbn.getNotification().largeIcon;


//            Log.i("Package", pack);
//            Log.i("Ticker", ticker);
//            Log.i("Title", title);
//            Log.i("Text", text);
            Log.d("aaa", pack + " : " + title + " : " + text + ", num : " + i);
            if (listener == null)
                Log.d("aaa", "Listener is null"+", num : " + i);
            else
                listener.setValue(pack + ":" + title + " : " + text);

//            Intent msgrcv = new Intent("Msg");
//            msgrcv.putExtra("package", pack);
//            msgrcv.putExtra("ticker", ticker);
//            msgrcv.putExtra("title", title);
//            msgrcv.putExtra("text", text);
//            if (id != null) {
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                id.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
//                msgrcv.putExtra("icon", byteArray);
//            }
//        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
        } catch (Exception e) {
            Log.d("aaa", "Exception: " + sbn);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }

    public void setListener(MyListener list) {
        NotificationListener.listener = list;
        Log.d("aaab", "listener : " + listener);
    }
}

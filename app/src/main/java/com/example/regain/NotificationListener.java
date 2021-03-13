package com.example.regain;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.CaseMap;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//long yourmilliseconds = System.currentTimeMillis();
//        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
//        Date resultdate = new Date(yourmilliseconds);
//        System.out.println(sdf.format(resultdate));
public class NotificationListener extends NotificationListenerService {

//    private static int NextCustomerId = 1;
    static MyListener listener;
    int i = 0;
    String content = "";
    private Bundle extras;
    private NotificationManager notificationManager;


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
            long time = sbn.getPostTime();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
            Date resultdate = new Date(time);
            String date = sdf.format(resultdate);

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
//            Log.d("aaa", pack + " : " + title + " : " + text + ", num : " + i);
//            if (listener == null)
//                Log.d("aaa", "Listener is null"+", num : " + i);
//            else {
//                listener.setValue(pack + ":" + title + " : " + text);
            String userName = getUserName();
//            String serial = getUserSerial();
            MyNotification notification = new MyNotification(pack, title, text, time);
//                Log.d("aaab", userName);
            FirebaseApp.initializeApp(getApplicationContext());
            if (text.contains(Constants.DELETED))
                sendNotification(title);
            else if (saveMessage(notification)) {
                FirebaseDatabase.getInstance().getReference(userName).child(getDomain(pack)).child(getDomain(title)).child(sha256(pack + ":" + title + " : " + text + date)).setValue(new Message(time, text));
            }
//                 FirebaseDatabase.getInstance().getReference("Arad").child("WhatsApp").child(getDomain("Ammie")).child(sha256(pack + ":" + title + " : " + text)).setValue(text);
//                 Log.d("aaab", userName);
//            }

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

    private void sendNotification(String title) {
        // send
        int reqCode = 1;
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        showNotification(getApplicationContext(), "A message has been removed", title, intent, reqCode);
//        Log.d("aaa", title + " Has removed a message");
    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
//        SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(context);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id

        Log.d("showNotification", "showNotification: " + reqCode);
    }

    private boolean saveMessage(MyNotification notification) {
        for (int i = 0; i < Constants.dontSave.length; i++) {
            if (notification.getText().contains(Constants.dontSave[i]))
                return false;
        }
        if (notification.getApp().contains(Constants.SYSTEM_UI))
            return false;
        if (notification.getContact().contains(Constants.WHATSAPP))
            return false;
        return true;
    }

    private String getUserName() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
        if (accounts.length > 0) {
            String domain = getDomain(accounts[0].name);
            return domain;
        }
        return "Unknown";
    }

    private String getDomain(String email) {
        String domain = "";
        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@')
                break;
            if (email.charAt(i) != '.')
                domain += email.charAt(i);
        }
        String tmp = domain.replace("com", "");
//        Log.d("aaa", tmp);
        return tmp;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }

    public void setListener(MyListener list) {
        NotificationListener.listener = list;
//        Log.d("aaab", "listener : " + listener);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    protected String sha256(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }
}

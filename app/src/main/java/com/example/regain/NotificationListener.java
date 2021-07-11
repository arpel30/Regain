package com.example.regain;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.regain.Activities.MainActivity;
import com.example.regain.Classes.Constants;
import com.example.regain.Classes.Message;
import com.example.regain.Classes.MyNotification;
import com.example.regain.Classes.MyUtils;
import com.example.regain.Services_Listeners.MyListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class NotificationListener extends NotificationListenerService {

    static MyListener listener;
    int i = 0;
    String content = "";
    private Bundle extras;
    private NotificationManager notificationManager;


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        i++;
        try {
            Bitmap bmp = null;
            long time = sbn.getPostTime();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
            Date resultdate = new Date(time);
            String date = sdf.format(resultdate);
            Notification not = sbn.getNotification();

            String pack = sbn.getPackageName();
            String ticker = "";
            if (sbn.getNotification().tickerText != null) {
                ticker = sbn.getNotification().tickerText.toString();
            }
            Bundle extras = not.extras;
            if (extras.containsKey(Notification.EXTRA_PICTURE)) {
                bmp = (Bitmap) extras.get(Notification.EXTRA_PICTURE);
            }

            String text = "";
            String title = "";
            Boolean isGroup = extras.getBoolean("android.isGroupConversation");
            String userName = MyUtils.getUserName(getApplicationContext());
            if(isGroup != null && isGroup){
                // maybe in a later version - saves group chat messages
//                title = extras.getString("android.hiddenConversationTitle");
//                text = extras.getCharSequence("android.text").toString() + ": " + MyUtils.getName_inGroupChat(title, extras.getString("android.title"));
                return;
            }else{
                title = extras.getString("android.title");
                text = extras.getCharSequence("android.text").toString();
            }
            String selfTitle = "";
            if(sbn.getPackageName().equals("com.whatsapp"))
                selfTitle = extras.getCharSequence("android.selfDisplayName").toString();

            if(title.equals(selfTitle)){
                FirebaseDatabase.getInstance().getReference(userName).child(Constants.SELF_KEY).child(sha256(pack + ":" + title + " : " + text + date)).setValue(new Message(time, text));
                Log.d("aaa", "it's just a self message bro");
                return;
            }

            if(sbn.getPackageName().equals("com.whatsapp")) {

                Icon icon = not.getLargeIcon();
                MyUtils.saveProfilePicture(icon, userName, getDomain(title), getApplicationContext());
                FirebaseDatabase.getInstance().getReference(userName).child(getDomain(Constants.WHATSAPP_PATH)).child(getDomain(title)).child(Constants.TIME).setValue(time);
            }

            MyNotification notification = new MyNotification(pack, title, text, time);
            FirebaseApp.initializeApp(getApplicationContext());
            if (text.contains(Constants.DELETED))
                sendNotification(title);
            else if (saveMessage(notification)) {
                FirebaseDatabase.getInstance().getReference(userName).child(getDomain(pack)).child(getDomain(title)).child(sha256(pack + ":" + title + " : " + text + date)).setValue(new Message(time, text));
                if(bmp != null){
                    String encoded_image = Constants.IMAGE_KEY + MyUtils.encodeTobase64(bmp);
                    FirebaseDatabase.getInstance().getReference(userName).child(getDomain(pack)).child(getDomain(title)).child(sha256(pack + ":" + title + " : " + encoded_image + date)).setValue(new Message(time, encoded_image));
                }
            }
        } catch (Exception e) {
            Log.d("aaaaaa", "Exception s: " + sbn);
            Log.d("aaaaaa", "Exception e: " + e.getMessage());
        }
    }





    private void sendNotification(String title) {
        int reqCode = 1;
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        showNotification(getApplicationContext(), "A message has been removed", title, intent, reqCode);
    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.reagin_logo_simple)
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

    private String getDomain(String email) {
        String domain = "";
        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@')
                break;
            if (email.charAt(i) != '.')
                domain += email.charAt(i);
        }
        String tmp = domain.replace("com", "");
        return tmp;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }

    public void setListener(MyListener list) {
        NotificationListener.listener = list;
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

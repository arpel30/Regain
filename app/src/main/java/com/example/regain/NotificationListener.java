package com.example.regain;

import android.accounts.Account;
import android.accounts.AccountManager;
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
import android.util.Patterns;

import androidx.core.app.NotificationCompat;

import com.example.regain.Activities.MainActivity;
import com.example.regain.Classes.Constants;
import com.example.regain.Classes.Message;
import com.example.regain.Classes.MyNotification;
import com.example.regain.Classes.MyUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        Log.d("aaaa", "sbn : " + sbn.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("aaaa", "sbn channel : " + sbn.getNotification().getChannelId());
        }

// group chat -> group_chat_defaults_2
        Log.d("aaaa", "sbn : " + sbn.getNotification().toString());
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
//            extras.getParcelable()
            Log.d("aaaaa", "extras "+extras);

            if (extras.containsKey(Notification.EXTRA_PICTURE)) {
                // this bitmap contain the picture attachment
                Log.d("aaaa", "contains image");
                bmp = (Bitmap) extras.get(Notification.EXTRA_PICTURE);
                Log.d("aaaa", "image " + bmp);
            }else
                Log.d("aaaa", "not contains image");

//            Drawable drawable = icon.loadDrawable(getApplicationContext());
//            bmp = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
//            Canvas c = new Canvas(bmp);
//            drawable.setBounds(new Rect(0, 0, 150, 150));
//            drawable.draw(c);
            Log.d("aaaa", "image " + bmp);

            String text = "";
            String title = "";
            Boolean isGroup = extras.getBoolean("android.isGroupConversation");
            String userName = getUserName();
            if(isGroup != null && isGroup){
                // maybe in a later version - saves group chat messages
//                title = extras.getString("android.hiddenConversationTitle");
//                text = extras.getCharSequence("android.text").toString() + ": " + MyUtils.getName_inGroupChat(title, extras.getString("android.title"));
                return;
            }else{
                title = extras.getString("android.title");
                text = extras.getCharSequence("android.text").toString();
            }

            String selfTitle = extras.getCharSequence("android.selfDisplayName").toString();
            Log.d("aaa", "self message = " + selfTitle);

            if(title.equals(selfTitle)){
                FirebaseDatabase.getInstance().getReference(userName).child(Constants.SELF_KEY).child(sha256(pack + ":" + title + " : " + text + date)).setValue(new Message(time, text));
                Log.d("aaa", "it's just a self message bro");
                return;
            }
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
            if(sbn.getPackageName().equals("com.whatsapp")) {

                //            sbn : Notification(channel=silent_notifications_3 shortcut=972542145856@s.whatsapp.net contentView=null vibrate=null sound=null defaults=0x0 flags=0x8 color=0xff075e54 groupKey=group_key_messages sortKey=1 actions=2 vis=PRIVATE publicVersion=Notification(channel=null shortcut=null contentView=null vibrate=null sound=null defaults=0x0 flags=0x0 color=0xff075e54 category=msg vis=PRIVATE semFlags=0x0 semPriority=0 semMissedCount=0) semFlags=0x0 semPriority=0 semMissedCount=0)
//            extras Bundle[{android.title=עדי אחותי, android.hiddenConversationTitle=null, android.reduced.images=true, android.subText=null, android.template=android.app.Notification$MessagingStyle, android.showChronometer=false, android.people.list=[android.app.Person@840e830e], android.text=📷 ‏תמונה, android.progress=0, android.progressMax=0, android.selfDisplayName=‏את/ה, android.conversationUnreadMessageCount=0, android.appInfo=ApplicationInfo{d97789d com.whatsapp}, android.messages=[Bundle[mParcelledData.dataSize=880]],                                      android.showWhen=true, android.largeIcon=Icon(typ=BITMAP size=95x95), android.messagingStyleUser=Bundle[mParcelledData.dataSize=432], android.messagingUser=android.app.Person@97f82256, android.infoText=null, android.wearable.EXTENSIONS=Bundle[mParcelledData.dataSize=1012], android.progressIndeterminate=false, android.remoteInputHistory=null, last_row_id=2939909, android.isGroupConversation=false}]
//            extras Bundle[{android.title=שקד❤,     android.hiddenConversationTitle=null, android.reduced.images=true, android.subText=null, android.template=android.app.Notification$MessagingStyle, android.showChronometer=false, android.people.list=[android.app.Person@2e16c7fe], android.text=נ,           android.progress=0, android.progressMax=0, android.selfDisplayName=‏את/ה, android.conversationUnreadMessageCount=0, android.appInfo=ApplicationInfo{4f8a286 com.whatsapp}, android.messages=[Bundle[mParcelledData.dataSize=644], Bundle[mParcelledData.dataSize=600]], android.showWhen=true, android.largeIcon=Icon(typ=BITMAP size=95x95), android.messagingStyleUser=Bundle[mParcelledData.dataSize=432], android.messagingUser=android.app.Person@4b8cc67, android.infoText=null, android.wearable.EXTENSIONS=Bundle[mParcelledData.dataSize=996], android.progressIndeterminate=false, android.remoteInputHistory=null, last_row_id=2939908, android.isGroupConversation=false}]

                // try to get images
//                Bundle[] bundles = (Bundle[]) extras.getParcelableArray("android.messages");
//                Log.d("aaa", "self message = " + bundles.toString());

//content://com.android.contacts/contacts/lookup/0r388-27061E06.3551i3611c0190b55505a.3789r899-27061E06/933

                Icon icon = not.getLargeIcon();
                MyUtils.saveProfilePicture(icon, userName, getDomain(title), getApplicationContext());
                Log.d("aaaa", ",name " + userName + ",path " + Constants.WHATSAPP_PATH + ",title " + title + ",TIME " + Constants.TIME + ",time " + time);
                FirebaseDatabase.getInstance().getReference(userName).child(getDomain(Constants.WHATSAPP_PATH)).child(getDomain(title)).child(Constants.TIME).setValue(time);
            }
//            String serial = getUserSerial();
            MyNotification notification = new MyNotification(pack, title, text, time);
            FirebaseApp.initializeApp(getApplicationContext());
            if (text.contains(Constants.DELETED))
                sendNotification(title);
            else if (saveMessage(notification)) {
                FirebaseDatabase.getInstance().getReference(userName).child(getDomain(pack)).child(getDomain(title)).child(sha256(pack + ":" + title + " : " + text + date)).setValue(new Message(time, text));
                if(bmp != null){
                    String encoded_image = Constants.IMAGE_KEY + MyUtils.encodeTobase64(bmp);
                    Log.d("aaaa", "saving image");
                    FirebaseDatabase.getInstance().getReference(userName).child(getDomain(pack)).child(getDomain(title)).child(sha256(pack + ":" + title + " : " + encoded_image + date)).setValue(new Message(time, encoded_image));
                }
            }
        } catch (Exception e) {
            Log.d("aaaaaa", "Exception s: " + sbn);
            Log.d("aaaaaa", "Exception e: " + e.getMessage());
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

//2021-06-27 15:26:59.059 29898-29898/com.example.regain D/aaaa: sbn : Notification(channel=group_chat_defaults_2 shortcut=972528036969-1586716853@g.us contentView=null vibrate=null sound=null defaults=0x0 flags=0x8 color=0xff075e54 groupKey=group_key_messages sortKey=1 actions=2 vis=PRIVATE publicVersion=Notification(channel=null shortcut=null contentView=null vibrate=null sound=null defaults=0x0 flags=0x0 color=0xff075e54 category=msg vis=PRIVATE semFlags=0x0 semPriority=0 semMissedCount=0) semFlags=0x0 semPriority=0 semMissedCount=0)
//2021-06-27 15:26:59.062 29898-29898/com.example.regain D/aaaaa: extras Bundle[{android.title=חיים ריקס 🎅🏻 (ערד 🤴🏼🤴🏻): אבי אפקה, android.hiddenConversationTitle=חיים ריקס 🎅🏻 (ערד 🤴🏼🤴🏻), android.reduced.images=true, android.conversationTitle=חיים ריקס 🎅🏻 (ערד 🤴🏼🤴🏻), android.subText=null, android.template=android.app.Notification$MessagingStyle, android.showChronometer=false, android.people.list=[android.app.Person@95640872], android.text=לא בסדר בכלל, android.progress=0, android.progressMax=0, android.selfDisplayName=‏את/ה, android.conversationUnreadMessageCount=0, android.appInfo=ApplicationInfo{1e40e26 com.whatsapp}, android.messages=[Bundle[mParcelledData.dataSize=624]], android.showWhen=true, android.largeIcon=Icon(typ=BITMAP size=95x95), android.messagingStyleUser=Bundle[mParcelledData.dataSize=432], android.messagingUser=android.app.Person@86c2e107, android.infoText=null, android.wearable.EXTENSIONS=Bundle[mParcelledData.dataSize=1076], android.progressIndeterminate=false, android.remoteInputHistory=null, last_row_id=2930913, android.isGroupConversation=true}]

// extras Bundle[{android.title=שקד❤, android.hiddenConversationTitle=null, android.reduced.images=true, android.subText=null, android.template=android.app.Notification$MessagingStyle, android.showChronometer=false, android.people.list=[android.app.Person@2e16c7fe], android.text=סבבה, android.progress=0, android.progressMax=0, android.selfDisplayName=‏את/ה, android.conversationUnreadMessageCount=0, android.appInfo=ApplicationInfo{4e647f1 com.whatsapp}, android.messages=[Bundle[mParcelledData.dataSize=608]], android.showWhen=true, android.largeIcon=Icon(typ=BITMAP size=95x95), android.messagingStyleUser=Bundle[mParcelledData.dataSize=432], android.messagingUser=android.app.Person@cb5dd9ca, android.infoText=null, android.wearable.EXTENSIONS=Bundle[mParcelledData.dataSize=996], android.progressIndeterminate=false, android.remoteInputHistory=null, last_row_id=2886223, android.isGroupConversation=false}]
// extras Bundle[{android.title=תמונת המסך נשמרה, android.reduced.images=true, android.template=android.app.Notification$BigPictureStyle, android.text=הקש כאן כדי לפתוח את הפריט ב'גלריה'., android.largeIcon.big=null, android.appInfo=ApplicationInfo{af32f1c com.samsung.android.app.smartcapture}, android.picture=android.graphics.Bitmap@d8dc725, android.showWhen=true, android.substName=צילום Samsung, android.largeIcon=Icon(typ=BITMAP size=95x95)}]

// /  [{android.hiddenConversationTitle=null,android.subText=null, android.template=android.app.Notification$MessagingStyle, android.showChronometer=false, android.people.list=[android.app.Person@2e16c7fe], android.progress=0, android.progressMax=0, android.selfDisplayName=‏את/ה, android.conversationUnreadMessageCount=0, android.appInfo=ApplicationInfo{4e647f1 com.whatsapp}, android.messages=[Bundle[mParcelledData.dataSize=608]], android.showWhen=true, android.largeIcon=Icon(typ=BITMAP size=95x95), android.messagingStyleUser=Bundle[mParcelledData.dataSize=432], android.messagingUser=android.app.Person@cb5dd9ca, android.infoText=null, android.wearable.EXTENSIONS=Bundle[mParcelledData.dataSize=996], android.progressIndeterminate=false, android.remoteInputHistory=null, last_row_id=2886223, android.isGroupConversation=false}]
//  [{android.template=android.app.Notification$BigPictureStyle, android.largeIcon.big=null, android.appInfo=ApplicationInfo{af32f1c com.samsung.android.app.smartcapture}, android.picture=android.graphics.Bitmap@d8dc725, android.showWhen=true, android.substName=צילום Samsung, android.largeIcon=Icon(typ=BITMAP size=95x95)}]

// extras Bundle[{android.title=שקד❤, android.hiddenConversationTitle=null, android.reduced.images=true, android.subText=null, android.template=android.app.Notification$MessagingStyle, android.showChronometer=false, android.people.list=[android.app.Person@2e16c7fe], android.text=סבבה, android.progress=0, android.progressMax=0, android.selfDisplayName=‏את/ה, android.conversationUnreadMessageCount=0, android.appInfo=ApplicationInfo{4e647f1 com.whatsapp}, android.messages=[Bundle[mParcelledData.dataSize=608]], android.showWhen=true, android.largeIcon=Icon(typ=BITMAP size=95x95), android.messagingStyleUser=Bundle[mParcelledData.dataSize=432], android.messagingUser=android.app.Person@cb5dd9ca, android.infoText=null, android.wearable.EXTENSIONS=Bundle[mParcelledData.dataSize=996], android.progressIndeterminate=false, android.remoteInputHistory=null, last_row_id=2886223, android.isGroupConversation=false}]
// extras Bundle[{android.title=אפקה, android.hiddenConversationTitle=null, android.reduced.images=true, android.subText=null, android.template=android.app.Notification$MessagingStyle, android.showChronometer=false, android.people.list=[android.app.Person@74c2305d], android.text=📷 קח, android.progress=0, android.progressMax=0, android.selfDisplayName=‏את/ה, android.conversationUnreadMessageCount=0, android.appInfo=ApplicationInfo{920b14f com.whatsapp}, android.messages=[Bundle[mParcelledData.dataSize=864]], android.showWhen=true, android.largeIcon=Icon(typ=BITMAP size=95x95), android.messagingStyleUser=Bundle[mParcelledData.dataSize=432], android.messagingUser=android.app.Person@f6692658, android.infoText=null, android.wearable.EXTENSIONS=Bundle[mParcelledData.dataSize=3816], android.progressIndeterminate=false, android.remoteInputHistory=null, last_row_id=2886238, android.isGroupConversation=false}]
// 2021-05-27 18:07:25.322 11117-11117/com.example.regain D/aaaaa: extras Bundle[{android.title=נועה אפקה, android.hiddenConversationTitle=null, android.reduced.images=true, android.subText=null, android.template=android.app.Notification$MessagingStyle, android.showChronometer=false, android.people.list=[android.app.Person@74c2305d], android.text=📷 קח, android.progress=0, android.progressMax=0, android.selfDisplayName=‏את/ה, android.conversationUnreadMessageCount=0, android.appInfo=ApplicationInfo{bc5c90a com.whatsapp}, android.messages=[Bundle[mParcelledData.dataSize=612]], android.showWhen=true, android.largeIcon=Icon(typ=BITMAP size=95x95), android.messagingStyleUser=Bundle[mParcelledData.dataSize=432], android.messagingUser=android.app.Person@c2524e8b, android.infoText=null, android.wearable.EXTENSIONS=Bundle[mParcelledData.dataSize=3816], android.progressIndeterminate=false, android.remoteInputHistory=null, last_row_id=2886238, android.isGroupConversation=false}]

//2021-05-27 18:07:29.190 11117-11117/com.example.regain D/aaaa: sbn : StatusBarNotification(pkg=com.whatsapp user=UserHandle{0} id=1 tag=so0wU9GptAxVk3pxSoqvrKXNOYvnbXHY4jjBc+Fj6rs=
//     key=0|com.whatsapp|1|so0wU9GptAxVk3pxSoqvrKXNOYvnbXHY4jjBc+Fj6rs=
//    |10342: Notification(channel=silent_notifications_3 shortcut=972543188799@s.whatsapp.net contentView=null vibrate=null sound=null defaults=0x0 flags=0x8 color=0xff075e54 groupKey=group_key_messages sortKey=1 actions=2 vis=PRIVATE publicVersion=Notification(channel=null shortcut=null contentView=null vibrate=null sound=null defaults=0x0 flags=0x0 color=0xff075e54 category=msg vis=PRIVATE semFlags=0x0 semPriority=0 semMissedCount=0) semFlags=0x0 semPriority=0 semMissedCount=0))

package com.example.regain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.widget.TextView;

import java.net.CookieHandler;
import java.net.ResponseCache;

public class MainActivity extends AppCompatActivity implements MyListener{

//    static MyListener listener;
    private TextView main_LBL_notifications;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getNot();
        findViews();
        initViews();
        NotificationListener nl = new NotificationListener();
        nl.setListener(this);
//        new NotificationListener();
//        txtView = findViewById(R.id.main_LBL_notifications) ;
//        Button btnCreateNotification = findViewById(R.id. btnCreateNotification ) ;
//        btnCreateNotification.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick (View v) {
//                NotificationManager mNotificationManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE ) ;
//                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity. this, default_notification_channel_id ) ;
//                mBuilder.setContentTitle( "My Notification" ) ;
//                mBuilder.setContentText( "Notification Listener Service Example" ) ;
//                mBuilder.setTicker( "Notification Listener Service Example" ) ;
//                mBuilder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
//                mBuilder.setAutoCancel( true ) ;
//                if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
//                    int importance = NotificationManager. IMPORTANCE_HIGH ;
//                    NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
//                    mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
//                    assert mNotificationManager != null;
//                    mNotificationManager.createNotificationChannel(notificationChannel) ;
//                }
//                assert mNotificationManager != null;
//                mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;
//            }
//        }) ;
    }


    private void findViews() {
        this.main_LBL_notifications = findViewById(R.id.main_LBL_notifications);
//        main_LBL_notifications.setText("");
    }

    private void initViews() {
//        Log.d("aaa", this.main_LBL_notifications+"");
                main_LBL_notifications.setText("");
    }

    //    @Override
//    public boolean onCreateOptionsMenu (Menu menu) {
//        getMenuInflater().inflate(R.menu. menu_main , menu) ; //Menu Resource, Menu
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected (MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id. action_settings :
//                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS" ) ;
//                startActivity(intent) ;
//                return true;
//            default :
//                return super .onOptionsItemSelected(item) ;
//        }
//    }
//    @Override
//    public void setValue (String packageName) {
//        txtView .append( " \n " + packageName) ;
//    }
//}
    public void getNot() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE ) ;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity. this, default_notification_channel_id ) ;
        mBuilder.setContentTitle( "My Notification" ) ;
        mBuilder.setContentText( "Notification Listener Service Example" ) ;
        mBuilder.setTicker( "Notification Listener Service Example" ) ;
        mBuilder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        mBuilder.setAutoCancel( true ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
//        mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS" ) ;
        startActivity(intent) ;
    }

    @Override
    public void setValue(String message) {
        main_LBL_notifications.setText(main_LBL_notifications.getText()+"\n"+message);
    }
}
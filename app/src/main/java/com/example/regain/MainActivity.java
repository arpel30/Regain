package com.example.regain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements MyListener {

    //    static MyListener listener;
    private RecyclerView main_LST_contacts;
    private ArrayList<String> contacts;
    private Adapter_Contacts adapter_contacts;

    private TextView main_LBL_name;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";

    private DatabaseReference divRef;
    private ValueEventListener newContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getNot(); // need to write a function to check permissions
        findViews();
        getContacts();

        NotificationListener nl = new NotificationListener();
        nl.setListener(this);
        new NotificationListener();
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


    public void getContacts() {
        newContact = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getAllContacts(snapshot.getChildren());
                initViews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        String userName = getUserName();
        if (!userName.equals("Unknown")) {
            divRef = FirebaseDatabase.getInstance().getReference(userName).child(Constants.WHATSAPP_PATH);
            divRef.addValueEventListener(newContact);
        }
//        return contacts;
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

    private void getAllContacts(Iterable<DataSnapshot> children) {
        contacts = new ArrayList<>();
        for (DataSnapshot child : children) {
            String con = child.getKey();
            contacts.add(con);
//                DatabaseReference divRef = MyFirebase.getInstance().getFdb().getReference(Constants.WORKER_PATH);
//                divRef = divRef.child(req.getUid());
//                divRef.addValueEventListener(workerChangedListener);
        }
    }

    private void findViews() {
        this.main_LBL_name = findViewById(R.id.main_LBL_name);
        this.main_LST_contacts = findViewById(R.id.main_LST_contacts);
//        main_LBL_notifications.setText("");
    }

    private void initViews() {
//        Log.d("aaa", this.main_LBL_notifications+"");
        adapter_contacts = new Adapter_Contacts(this, contacts);
        adapter_contacts.setClickListener(new Adapter_Contacts.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(MainActivity.this, contacts.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Messages_Activity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.NAME_KEY, contacts.get(position));
                startActivity(intent);

//                finish();
            }
        });

        main_LST_contacts.setLayoutManager(new LinearLayoutManager(this));
        main_LST_contacts.setAdapter(adapter_contacts);
        //main_LBL_name.setText("");
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
        if(!Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this, default_notification_channel_id);
            mBuilder.setContentTitle("My Notification");
            mBuilder.setContentText("Notification Listener Service Example");
            mBuilder.setTicker("Notification Listener Service Example");
            mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
            mBuilder.setAutoCancel(true);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                if (mNotificationManager != null)
                    mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
//        mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
    }

    @Override
    public void setValue(String message) {
        main_LBL_name.setText(main_LBL_name.getText() + "\n" + message);
    }
}
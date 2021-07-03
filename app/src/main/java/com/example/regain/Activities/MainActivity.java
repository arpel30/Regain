package com.example.regain.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.regain.Adapters.Adapter_Contacts;
import com.example.regain.Classes.Constants;
import com.example.regain.Classes.Contact;
import com.example.regain.Classes.MyUtils;
import com.example.regain.Comperators.CompareByDate_contact;
import com.example.regain.MyListener;
import com.example.regain.NotificationListener;
import com.example.regain.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements MyListener {

    //    static MyListener listener;
    private RecyclerView main_LST_contacts;
    private ArrayList<Contact> contacts;
    private Adapter_Contacts adapter_contacts;

    private TextView main_LBL_name;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";

    private String userName = "Unknown";

    private DatabaseReference divRef;
    private ValueEventListener newContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getNot();
        findViews();
        userName = MyUtils.getUserName(this);
        getContacts();
        Log.d("aaa", "Build : " + MyUtils.getBuildNumber());
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
        if (!userName.equals("Unknown")) {
            divRef = FirebaseDatabase.getInstance().getReference(userName).child(Constants.WHATSAPP_PATH);
            divRef.addValueEventListener(newContact);
        }
//        return contacts;
    }


    private void getAllContacts(Iterable<DataSnapshot> children) {
        contacts = new ArrayList<>();
        long time = 0;
        for (DataSnapshot child : children) {
            String con = child.getKey();

            FirebaseDatabase.getInstance().getReference(userName).child(Constants.WHATSAPP_PATH).child(con).child(Constants.TIME).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Long tmp = snapshot.getValue(Long.class);
                    if(tmp == null)
                        tmp = 0l;
                    Log.d("aaa", con + ":" + tmp);
                    Contact tmp_con = new Contact(tmp, con);
                    if(contacts.contains(tmp_con)){
                        contacts.remove(tmp_con);
                    }
                    contacts.add(tmp_con);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        contacts.sort(new CompareByDate_contact());
                    }
                    adapter_contacts.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });

            time-=200;
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
        adapter_contacts = new Adapter_Contacts(this, contacts);
        adapter_contacts.setClickListener(new Adapter_Contacts.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(MainActivity.this, contacts.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Messages_Activity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.NAME_KEY, contacts.get(position).getName());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseDatabase.getInstance().getReference(userName).child(Constants.WHATSAPP_PATH).removeEventListener(newContact);
    }
}
package com.example.regain.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.regain.Adapters.Adapter_Contacts;
import com.example.regain.Classes.Constants;
import com.example.regain.Classes.Contact;
import com.example.regain.Classes.MyUtils;
import com.example.regain.Comperators.CompareByDate_contact;
import com.example.regain.Services_Listeners.MyListener;
import com.example.regain.NotificationListener;
import com.example.regain.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyListener {

    private RecyclerView main_LST_contacts;
    private ArrayList<Contact> contacts;
    private Adapter_Contacts adapter_contacts;

    private TextView main_LBL_name;
    private TextView main_LBL_blocked;
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
        NotificationListener nl = new NotificationListener();
        nl.setListener(this);
        new NotificationListener();
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
    }


    private void getAllContacts(Iterable<DataSnapshot> children) {
        contacts = new ArrayList<>();
        for (DataSnapshot child : children) {
            String con = child.getKey();

            FirebaseDatabase.getInstance().getReference(userName).child(Constants.WHATSAPP_PATH).child(con).child(Constants.TIME).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Long tmp = snapshot.getValue(Long.class);
                    if(tmp == null)
                        tmp = 0l;
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
        }
    }

    private void findViews() {
        this.main_LBL_name = findViewById(R.id.main_LBL_name);
        this.main_LBL_blocked = findViewById(R.id.main_LBL_blocked);
        this.main_LST_contacts = findViewById(R.id.main_LST_contacts);
    }

    private void initViews() {
        adapter_contacts = new Adapter_Contacts(this, contacts);
        adapter_contacts.setClickListener(new Adapter_Contacts.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, Messages_Activity.class);
                intent.putExtra(Constants.NAME_KEY, contacts.get(position).getName());
                startActivity(intent);
            }
        });

        main_LST_contacts.setLayoutManager(new LinearLayoutManager(this));
        main_LST_contacts.setAdapter(adapter_contacts);
    }

    public void getNot() {
        if(!checkPermission()) {
            Toast.makeText(this, "Notifications permission is required!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
    }

    public boolean checkPermission(){
        return Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners").contains(getApplicationContext().getPackageName());
    }

    public void setViews(boolean permissionGranted){
        if(permissionGranted){
            main_LBL_name.setVisibility(View.VISIBLE);
            main_LST_contacts.setVisibility(View.VISIBLE);
            main_LBL_blocked.setVisibility(View.INVISIBLE);
        }else{
            main_LBL_name.setVisibility(View.INVISIBLE);
            main_LST_contacts.setVisibility(View.INVISIBLE);
            main_LBL_blocked.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setViews(checkPermission());
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
package com.example.regain.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.regain.Adapters.Adapter_Messages;
import com.example.regain.Classes.Constants;
import com.example.regain.Classes.Message;
import com.example.regain.Classes.MyUtils;
import com.example.regain.Comperators.CompareByDate_message;
import com.example.regain.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Messages_Activity extends AppCompatActivity {

    private TextView messages_LBL_name;
    private ImageView messages_IMG_profile;
    private RecyclerView messages_LST_messages;
    private Adapter_Messages adapter_messages;
    private String contactName;
    private ArrayList<Message> messages;

    private DatabaseReference divRef;
    private ValueEventListener newMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Intent intent = getIntent();
        contactName = intent.getStringExtra(Constants.NAME_KEY);
        findViews();
        getMessages();
    }


    public void getMessages(){
        newMessage = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getAllMessages(snapshot.getChildren());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    messages.sort(new CompareByDate_message());
                }
                initViews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        String userName = getUserName();
        if (!userName.equals("Unknown")) {
            divRef = FirebaseDatabase.getInstance().getReference(userName).child(Constants.WHATSAPP_PATH).child(contactName);
            divRef.addValueEventListener(newMessage);
            FirebaseDatabase.getInstance().getReference(userName).child(Constants.WHATSAPP_PATH).child(contactName).child("profile_pic").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    messages_IMG_profile.setImageBitmap(MyUtils.decodeBase64(snapshot.getValue(String.class)));
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
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

    private void getAllMessages(Iterable<DataSnapshot> children) {
        messages = new ArrayList<>();
        for (DataSnapshot child : children) {
            if(child.getKey().equals(Constants.PROFILE_PIC) || child.getKey().equals(Constants.TIME))
                continue;
            Message message = child.getValue(Message.class);
//            String con = child.getKey();
            messages.add(message);
//                DatabaseReference divRef = MyFirebase.getInstance().getFdb().getReference(Constants.WORKER_PATH);
//                divRef = divRef.child(req.getUid());
//                divRef.addValueEventListener(workerChangedListener);
        }
    }

    private void findViews() {
        messages_LBL_name = findViewById(R.id.messages_LBL_name);
        messages_LST_messages = findViewById(R.id.messages_LST_messages);
        messages_IMG_profile = findViewById(R.id.messages_IMG_profile);
//        main_LBL_notifications.setText("");
    }

    private void initViews() {
//        Log.d("aaa", this.main_LBL_notifications+"");
        messages_LBL_name.setText(contactName);
        adapter_messages = new Adapter_Messages(this, messages);
        messages_LST_messages.setLayoutManager(new LinearLayoutManager(this));
        messages_LST_messages.setAdapter(adapter_messages);

        scrollToBottom(messages_LST_messages);
        //main_LBL_name.setText("");
    }

    private void scrollToBottom(final RecyclerView recyclerView) {
        // scroll to last item to get the view of last item
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
        final int lastItemPosition = adapter.getItemCount() - 1;

        layoutManager.scrollToPositionWithOffset(lastItemPosition, 0);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                // then scroll to specific offset
                View target = layoutManager.findViewByPosition(lastItemPosition);
                if (target != null) {
                    int offset = recyclerView.getMeasuredHeight() - target.getMeasuredHeight() - 50;
                    layoutManager.scrollToPositionWithOffset(lastItemPosition, offset);
                }
            }
        });
    }
}
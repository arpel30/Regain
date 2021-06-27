package com.example.regain;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class MyUtils {
    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

//        Log.d("aaaa", "image encoded : "+imageEncoded);
        return imageEncoded;
    }

    public static void saveProfilePicture(Icon icon, String userName, String title, Context context){
        Log.d("aaaa", "saving profile pic");
        Drawable drawable = icon.loadDrawable(context);
        Bitmap bmp = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        drawable.setBounds(new Rect(0, 0, 150, 150));
        drawable.draw(c);
        String pic = encodeTobase64(bmp);
        FirebaseDatabase.getInstance().getReference(userName).child(getDomain(Constants.WHATSAPP_PATH)).child(getDomain(title)).child("profile_pic").setValue(pic);

    }
    public static void setProfilePicture(String userName, ImageView image, String contactName) {
        if (!userName.equals("Unknown")) {
//            divRef = FirebaseDatabase.getInstance().getReference(userName).child(Constants.WHATSAPP_PATH).child(contactName);
            FirebaseDatabase.getInstance().getReference(userName).child(Constants.WHATSAPP_PATH).child(contactName).child("profile_pic").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    image.setImageBitmap(MyUtils.decodeBase64(snapshot.getValue(String.class)));
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
    }
    public static String getDomain(String email) {
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

    public static String getUserName(Context context) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(context.getApplicationContext()).getAccounts();
        if (accounts.length > 0) {
            String domain = getDomain(accounts[0].name);
            return domain;
        }
        return "Unknown";
    }

    public static String getBuildNumber() {
        // problem with SDK >= Q
        String serialNumber;
        Log.d("aaa","before");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && 3>4) {
            Log.d("aaa","before - in if");
            serialNumber = Build.getSerial();
            Log.d("aaa","after - in if");
        }else {
            Log.d("aaa", "before - in else");
            serialNumber = android.os.Build.SERIAL;
            Log.d("aaa", "after - in else");
        }
        return serialNumber;
    }

    public static String getName_inGroupChat(String groupName, String title){
        return title.substring(groupName.length()-1, title.length()-1);
    }
}

package com.example.regain.Classes;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.UUID;
import java.util.regex.Pattern;

public class MyUtils {
    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        if (input == null || input.isEmpty())
            return null;
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
        return imageEncoded;
    }

    public static void saveProfilePicture(Icon icon, String userName, String title, Context context) {
        Drawable drawable = icon.loadDrawable(context);
        Bitmap bmp = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        drawable.setBounds(new Rect(0, 0, 150, 150));
        drawable.draw(c);
        String pic = encodeTobase64(bmp);
        FirebaseDatabase.getInstance().getReference(userName).child(getDomain(Constants.WHATSAPP_PATH)).child(getDomain(title)).child("profile_pic").setValue(pic);

    }

    public static void setProfilePicture(String userName, ImageView image, String contactName, Context context) {
        if (!userName.equals("Unknown")) {
            FirebaseDatabase.getInstance().getReference(userName).child(Constants.WHATSAPP_PATH).child(contactName).child("profile_pic").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Bitmap tmp = MyUtils.decodeBase64(snapshot.getValue(String.class));
                    if (tmp != null)
                        loadBitmapGlide(context, image, tmp);
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
    }

    public static void loadBitmapGlide(Context context, ImageView image, Bitmap tmp) {
        Glide
                .with(context.getApplicationContext())
                .load(tmp)
                .fitCenter()
                .into(image);
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
        return tmp;
    }

    public static String getUserName(Context context) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(context.getApplicationContext()).getAccounts();
        if (accounts.length > 0) {
            String domain = getDomain(accounts[0].name);
            return domain;
        }
        // there is no account - create random value and save it
        String val = MySPV.getInstance().getString(Constants.MY_ID, null);
        Log.d("aaaaabb", "1 val = " + val);
        // if value is not exist, create & save a new value
        if(val == null){
            val = saveLastID(context, Constants.MY_ID);
            MySPV.getInstance().putString(Constants.MY_ID, val);
        }
        return val;
    }

    public static String saveLastID(Context context, String key){
        FirebaseDatabase.getInstance().getReference(Constants.LAST_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int id = snapshot.getValue(Integer.class);
                Log.d("aaaaabb", "id = " + id);
                FirebaseDatabase.getInstance().getReference(Constants.LAST_ID).setValue(id+1);
                MySPV.getInstance().putString(key, id + UUID.randomUUID().toString());
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        return MySPV.getInstance().getString(key, "Unknown"); // return the value we saved earlier
    }

    public static String getBuildNumber() {
        // problem with SDK >= Q - only system apps can get the build number
        String serialNumber;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && 3>4) {
            serialNumber = Build.getSerial();
        }else {
            serialNumber = android.os.Build.SERIAL;
        }
        return serialNumber;
    }

    public static String getName_inGroupChat(String groupName, String title){
        return title.substring(groupName.length()-1, title.length()-1);
    }
}

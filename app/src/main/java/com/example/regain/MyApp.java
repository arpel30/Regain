package com.example.regain;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.judemanutd.autostarter.AutoStartPermissionHelper;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if(AutoStartPermissionHelper.getInstance().getAutoStartPermission(this))
            startService(new Intent(this, MyService.class));
        Log.d("aaa", "Start service");
    }
}

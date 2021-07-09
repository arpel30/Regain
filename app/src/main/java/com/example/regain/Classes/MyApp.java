package com.example.regain.Classes;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.example.regain.MyService;
import com.judemanutd.autostarter.AutoStartPermissionHelper;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MySPV.init(this);
        if(AutoStartPermissionHelper.getInstance().getAutoStartPermission(this))
            startService(new Intent(this, MyService.class));
        Log.d("aaa", "Start service");
    }
}

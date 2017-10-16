package com.emilyzebra.eyesores;

import android.app.IntentService;
import android.content.Intent;

import com.google.firebase.appindexing.FirebaseAppIndex;

public class AppIndexingService extends IntentService {

    static String CREATE_FLAG = "create";

    public AppIndexingService() {
        super("AppIndexingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FirebaseAppIndex instance = FirebaseAppIndex.getInstance();
        boolean createFlag = intent.getBooleanExtra(CREATE_FLAG, false);
        if (createFlag) {
            AppIndexingUtil.setStickers(getApplicationContext(), instance);
        } else {
            AppIndexingUtil.clearStickers(getApplicationContext(), instance);
        }
    }
}

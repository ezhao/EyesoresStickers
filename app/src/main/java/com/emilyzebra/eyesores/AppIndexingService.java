package com.emilyzebra.eyesores;

import android.app.IntentService;
import android.content.Intent;

import android.os.Binder;
import android.os.IBinder;
import com.google.firebase.appindexing.FirebaseAppIndex;

public class AppIndexingService extends IntentService {

    static String CREATE_FLAG = "create";

    private final IBinder binder = new ServiceBinder();
    private ServiceCallbacks serviceCallbacks;

    public AppIndexingService() {
        super("AppIndexingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FirebaseAppIndex instance = FirebaseAppIndex.getInstance();
        boolean createFlag = intent.getBooleanExtra(CREATE_FLAG, false);
        if (createFlag) {
            AppIndexingUtil.setStickers(getApplicationContext(), instance, serviceCallbacks);
        } else {
            AppIndexingUtil.clearStickers(instance, serviceCallbacks);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    class ServiceBinder extends Binder {
        AppIndexingService getService() {
            return AppIndexingService.this;
        }
    }

    void setServiceCallbacks(ServiceCallbacks serviceCallbacks) {
        this.serviceCallbacks = serviceCallbacks;
    }

    public interface ServiceCallbacks {
        void onStickerAddSuccess();
        void onStickerAddFail();
        void onStickerRemoveSuccess();
        void onStickerRemoveFail();
    }
}

package com.emilyzebra.eyesores;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {

    private AppIndexingService appIndexingService;
    private ProgressBar loadingSpinner;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingSpinner = findViewById(R.id.loading_spinner);
        intent = new Intent(this, AppIndexingService.class);

        // TODO: 10/17/17 emily handle rotation potentially with the loading spinner
        startAndBindService(true);

        Button addStickersButton = findViewById(R.id.add_stickers_button);
        Button clearStickersButton = findViewById(R.id.clear_stickers_button);
        addStickersButton.setOnClickListener(v -> startAndBindService(true));
        clearStickersButton.setOnClickListener(v -> startAndBindService(false));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAppIndexingService();
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            appIndexingService = ((AppIndexingService.ServiceBinder) service).getService();
            appIndexingService.setServiceCallbacks(serviceCallbacks);
            startService(intent);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            removeAppIndexingService();
        }
    };

    private void startAndBindService(boolean createFlag) {
        intent.putExtra(AppIndexingService.CREATE_FLAG, createFlag);
        if (appIndexingService != null) {
            startService(intent);
        } else {
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void removeAppIndexingService() {
        if (appIndexingService != null) {
            appIndexingService.setServiceCallbacks(null);
            appIndexingService = null;
        }
    }

    private final AppIndexingService.ServiceCallbacks serviceCallbacks = new AppIndexingService.ServiceCallbacks() {
        @Override
        public void onStickerAddSuccess() {
            if (loadingSpinner != null && loadingSpinner.getVisibility() == View.VISIBLE) {
                loadingSpinner.setVisibility(View.GONE);
            }
        }

        @Override
        public void onStickerAddFail() {
            // TODO: 10/17/17 emily error state
        }

        @Override
        public void onStickerRemoveSuccess() {
            Toast.makeText(MainActivity.this, getString(R.string.clear_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStickerRemoveFail() {
            // TODO: 10/17/17 emily error state
        }
    };
}

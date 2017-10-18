package com.emilyzebra.eyesores;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    private AppIndexingService appIndexingService;
    private ProgressBar loadingSpinner;
    private TextView successText;
    private View helpButton;
    private View gboardGuide;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingSpinner = findViewById(R.id.loading_spinner);
        successText = findViewById(R.id.success_text);
        helpButton = findViewById(R.id.help_button);
        gboardGuide = findViewById(R.id.gboard_guide);

        intent = new Intent(this, AppIndexingService.class);

        // TODO: 10/17/17 emily handle rotation potentially with the loading spinner
        startAndBindService(true);

        Button addStickersButton = findViewById(R.id.add_stickers_button);
        Button clearStickersButton = findViewById(R.id.clear_stickers_button);
        addStickersButton.setOnClickListener(v -> {
            startLoading();
            startAndBindService(true);
        });
        clearStickersButton.setOnClickListener(v -> {
            startLoading();
            startAndBindService(false);
        });

        gboardGuide.setVisibility(View.GONE);
        helpButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    gboardGuide.setVisibility(View.GONE);
                    return true;
                default:
                    gboardGuide.setVisibility(View.VISIBLE);
                    return true;

            }
        });
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

    private void startLoading() {
        if (loadingSpinner == null) {
            return;
        }
        loadingSpinner.setVisibility(View.VISIBLE);
        successText.setVisibility(View.GONE);
    }

    private void completeLoading(boolean createFlag) {
        if (loadingSpinner == null) {
            return;
        }
        loadingSpinner.setVisibility(View.GONE);
        successText.setText(createFlag ? R.string.add_success : R.string.clear_success);
        successText.setVisibility(View.VISIBLE);
    }

    private final AppIndexingService.ServiceCallbacks serviceCallbacks = new AppIndexingService.ServiceCallbacks() {
        @Override
        public void onStickerAddSuccess() {
            completeLoading(true);
        }

        @Override
        public void onStickerAddFail() {
            // TODO: 10/17/17 emily error state
        }

        @Override
        public void onStickerRemoveSuccess() {
            completeLoading(false);
        }

        @Override
        public void onStickerRemoveFail() {
            // TODO: 10/17/17 emily error state
        }
    };
}

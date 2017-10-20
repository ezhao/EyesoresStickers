package com.emilyzebra.eyesores;

import android.app.Activity;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String FIRST_LAUNCH_PREF = "first_launch_pref";

    private AppIndexingService appIndexingService;
    private ProgressBar loadingSpinner;
    private TextView successText;
    private View gboardGuide;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingSpinner = findViewById(R.id.loading_spinner);
        successText = findViewById(R.id.success_text);
        gboardGuide = findViewById(R.id.gboard_guide);
        intent = new Intent(this, AppIndexingService.class);

        Button addStickersButton = findViewById(R.id.add_stickers_button);
        addStickersButton.setOnClickListener(v -> startAndBindService(true));

        Button clearStickersButton = findViewById(R.id.clear_stickers_button);
        clearStickersButton.setOnClickListener(v -> startAndBindService(false));

        Button helpButton = findViewById(R.id.help_button);
        helpButton.setOnClickListener(v -> {
            boolean helpVisible = gboardGuide.getVisibility() == View.VISIBLE;
            int newVisibility = helpVisible ? View.GONE : View.VISIBLE;
            gboardGuide.setVisibility(newVisibility);
            helpButton.setText(helpVisible ? R.string.help : R.string.hide);
        });

        View footer = findViewById(R.id.footer);
        footer.setOnClickListener(v -> {
            Uri uriApp = Uri.parse("http://instagram.com/_u/eyesores");
            Uri uriWeb = Uri.parse("http://instagram.com/eyesores");
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, uriApp).setPackage("com.instagram.android"));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, uriWeb));
            }
        });

        // TODO: 10/17/17 emily handle rotation potentially with the loading spinner

        if (isFirstLaunch()) {
            startAndBindService(true);
            writeLaunchedPref();
        }
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
        startLoading();
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

    private void writeLaunchedPref() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(FIRST_LAUNCH_PREF, true);
        editor.apply();
    }

    private boolean isFirstLaunch() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return !sharedPref.getBoolean(FIRST_LAUNCH_PREF, false);
    }
}

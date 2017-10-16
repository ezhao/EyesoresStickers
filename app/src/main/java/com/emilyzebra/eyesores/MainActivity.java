package com.emilyzebra.eyesores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = new Intent(MainActivity.this, AppIndexingService.class);

        Button addStickersButton = findViewById(R.id.add_stickers_button);
        Button clearStickersButton = findViewById(R.id.clear_stickers_button);

        addStickersButton.setOnClickListener(v -> startService(intent.putExtra(AppIndexingService.CREATE_FLAG, true)));
        clearStickersButton.setOnClickListener(v -> startService(intent.putExtra(AppIndexingService.CREATE_FLAG, false)));
    }
}

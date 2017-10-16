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

        Button addStickersBtn = findViewById(R.id.addStickersBtn);
        Button clearStickersBtn = findViewById(R.id.clearStickersBtn);

        addStickersBtn.setOnClickListener(v -> startService(intent.putExtra(AppIndexingService.CREATE_FLAG, true)));
        clearStickersBtn.setOnClickListener(v -> startService(intent.putExtra(AppIndexingService.CREATE_FLAG, false)));
    }
}

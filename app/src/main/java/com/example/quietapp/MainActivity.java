package com.example.quietapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private static final String WORK_NAME = "quiet_task";
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int pad = (int) (24 * getResources().getDisplayMetrics().density);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad, pad, pad);

        status = new TextView(this);
        status.setTextSize(16);

        Button start = new Button(this);
        start.setText("Start");
        start.setOnClickListener(v -> {
            Constraints constraints = new Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build();
            PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                    QuietWorker.class, 15, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build();
            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
            refresh("Started");
        });

        Button stop = new Button(this);
        stop.setText("Stop");
        stop.setOnClickListener(v -> {
            WorkManager.getInstance(this).cancelUniqueWork(WORK_NAME);
            refresh("Stopped");
        });

        root.addView(status);
        root.addView(start);
        root.addView(stop);
        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh(null);
    }

    private void refresh(String msg) {
        long last = getSharedPreferences("quiet", Context.MODE_PRIVATE).getLong("last_run", 0);
        String lastText = last == 0 ? "never"
                : DateFormat.getDateTimeInstance().format(new Date(last));
        status.setText((msg == null ? "" : msg + "\n") + "Last run: " + lastText);
    }
}

package com.example.quietapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class QuietWorker extends Worker {

    public QuietWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // ===== ඔයාගේ වැඩේ මෙතන දාන්න (ඉක්මනින් ඉවර වෙන්න ඕන) =====

        getApplicationContext()
                .getSharedPreferences("quiet", Context.MODE_PRIVATE)
                .edit()
                .putLong("last_run", System.currentTimeMillis())
                .apply();

        return Result.success();
    }
}

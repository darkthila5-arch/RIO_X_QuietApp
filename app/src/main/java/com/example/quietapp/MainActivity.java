package com.example.quietapp;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        float d = getResources().getDisplayMetrics().density;
        int pad = (int) (24 * d);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_VERTICAL);
        root.setPadding(pad, pad, pad, pad);

        TextView title = new TextView(this);
        title.setText("Snowfall");
        title.setTextSize(28);
        title.setPadding(0, 0, 0, pad / 2);

        TextView info = new TextView(this);
        info.setText("Overlay: snow falls over every app.\n"
                + "Live wallpaper: snow on your home/lock screen (lightest on battery).");
        info.setPadding(0, 0, 0, pad);

        Button start = new Button(this);
        start.setText("Start snow overlay");
        start.setOnClickListener(v -> startSnow());

        Button stop = new Button(this);
        stop.setText("Stop snow overlay");
        stop.setOnClickListener(v -> stopService(new Intent(this, SnowOverlayService.class)));

        Button wallpaper = new Button(this);
        wallpaper.setText("Set as live wallpaper");
        wallpaper.setOnClickListener(v -> openWallpaperPicker());

        root.addView(title);
        root.addView(info);
        root.addView(start);
        root.addView(stop);
        root.addView(wallpaper);
        setContentView(root);
    }

    private void startSnow() {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this,
                    "Allow \"Display over other apps\", then press Start again",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())));
            return;
        }
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission("android.permission.POST_NOTIFICATIONS")
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
        }
        Intent i = new Intent(this, SnowOverlayService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(i);
        } else {
            startService(i);
        }
    }

    private void openWallpaperPicker() {
        try {
            Intent i = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(this, SnowWallpaperService.class));
            startActivity(i);
        } catch (Exception e) {
            try {
                startActivity(new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));
            } catch (Exception e2) {
                Toast.makeText(this, "Live wallpaper not supported on this phone",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}

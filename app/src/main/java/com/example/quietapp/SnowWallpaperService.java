package com.example.quietapp;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class SnowWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new SnowWallpaperEngine();
    }

    private class SnowWallpaperEngine extends Engine {

        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Paint bg = new Paint();
        private SnowEngine snow;
        private boolean visible = false;
        private long last = 0;

        private final Runnable frame = new Runnable() {
            @Override
            public void run() {
                drawFrame();
            }
        };

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            snow = new SnowEngine(70, getResources().getDisplayMetrics().density);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            bg.setShader(new LinearGradient(0, 0, 0, height,
                    0xFF0B1D3A, 0xFF1F4E79, Shader.TileMode.CLAMP));
            snow.resize(width, height);
            drawFrame();
        }

        @Override
        public void onVisibilityChanged(boolean isVisible) {
            visible = isVisible;
            handler.removeCallbacks(frame);
            if (isVisible) {
                last = 0;
                handler.post(frame);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            visible = false;
            handler.removeCallbacks(frame);
            super.onSurfaceDestroyed(holder);
        }

        private void drawFrame() {
            handler.removeCallbacks(frame);
            SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    long now = SystemClock.uptimeMillis();
                    float dt = last == 0 ? 0.033f : Math.min((now - last) / 1000f, 0.1f);
                    last = now;
                    snow.update(dt);
                    c.drawRect(0, 0, c.getWidth(), c.getHeight(), bg);
                    snow.draw(c);
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }
            if (visible) handler.postDelayed(frame, 33);
        }
    }
}

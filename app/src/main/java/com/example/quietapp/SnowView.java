package com.example.quietapp;

import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.view.View;

/** Overlay eke thiyena transparent snow view eka (~30 fps). */
class SnowView extends View {

    private final SnowEngine engine;
    private long last = 0;
    private boolean active = true;

    SnowView(Context context) {
        super(context);
        engine = new SnowEngine(60, context.getResources().getDisplayMetrics().density);
    }

    void setActive(boolean value) {
        active = value;
        if (value) {
            last = 0;
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        engine.resize(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long now = SystemClock.uptimeMillis();
        float dt = last == 0 ? 0.033f : Math.min((now - last) / 1000f, 0.1f);
        last = now;
        engine.update(dt);
        engine.draw(canvas);
        if (active) postInvalidateDelayed(33);
    }
}

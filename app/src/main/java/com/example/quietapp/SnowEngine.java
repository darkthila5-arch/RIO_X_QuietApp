package com.example.quietapp;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

/** Snow flakes wala position update + draw karana shared code eka. */
class SnowEngine {

    private final int count;
    private final float density;
    private final Random rnd = new Random();
    private final float[] x, y, radius, speed, sway, phase;
    private final int[] alpha;
    private final Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint flake = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float w, h;

    SnowEngine(int count, float density) {
        this.count = count;
        this.density = density;
        x = new float[count];
        y = new float[count];
        radius = new float[count];
        speed = new float[count];
        sway = new float[count];
        phase = new float[count];
        alpha = new int[count];
        glow.setColor(0xFFA8CCFF);
        flake.setColor(0xFFFFFFFF);
    }

    void resize(int width, int height) {
        w = width;
        h = height;
        for (int i = 0; i < count; i++) reset(i, true);
    }

    private void reset(int i, boolean randomY) {
        radius[i] = (1.5f + rnd.nextFloat() * 3.5f) * density;
        x[i] = rnd.nextFloat() * w;
        y[i] = randomY ? rnd.nextFloat() * h : -radius[i] * 2;
        speed[i] = (35f + rnd.nextFloat() * 55f) * density;
        sway[i] = (8f + rnd.nextFloat() * 18f) * density;
        phase[i] = rnd.nextFloat() * 6.28f;
        alpha[i] = 150 + rnd.nextInt(100);
    }

    void update(float dt) {
        if (w <= 0) return;
        for (int i = 0; i < count; i++) {
            y[i] += speed[i] * dt;
            phase[i] += dt;
            x[i] += (float) Math.sin(phase[i]) * sway[i] * dt;
            if (y[i] > h + radius[i] * 2) reset(i, false);
            if (x[i] < -radius[i]) x[i] = w + radius[i];
            if (x[i] > w + radius[i]) x[i] = -radius[i];
        }
    }

    void draw(Canvas c) {
        for (int i = 0; i < count; i++) {
            glow.setAlpha(alpha[i] / 3);
            c.drawCircle(x[i], y[i], radius[i] * 1.6f, glow);
            flake.setAlpha(alpha[i]);
            c.drawCircle(x[i], y[i], radius[i], flake);
        }
    }
}

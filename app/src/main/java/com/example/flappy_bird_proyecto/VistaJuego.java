package com.example.flappy_bird_proyecto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Color;

import androidx.annotation.NonNull;

import java.util.Random;

public class VistaJuego extends View {

    Handler handler;
    Runnable runnable;
    final int UPDATE_MILLIS=30;
    Bitmap backgraund, topTubo, bottonTubo;
    Display display;
    Point point;
    int dWidth, dHeight;
    Rect rect;
    Bitmap[] pajaros;
    int pajaroFrame = 0;
    int velocidad = 0, gravedad = 3;
    int pajaroX, pajaroY;
    boolean juego_State = false;
    int gap = 400;
    int minTubo, maxTubo;
    int numTubos = 4;
    int distanciaTubos;
    int[] tuboX = new int[numTubos];
    int[] topTuboY = new int[numTubos];
    Random rd;
    int tuboVelocidad = 10;
    int score = 0;
    boolean[] scored = new boolean[numTubos];
    private Paint textPaint = new Paint();

    public VistaJuego(Context context) {
        super(context);
        handler = new Handler();
        runnable = this::invalidate;

        backgraund = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        topTubo = BitmapFactory.decodeResource(getResources(), R.drawable.tuberia2);
        bottonTubo = BitmapFactory.decodeResource(getResources(), R.drawable.tuberia1);
        display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getSize(point);
        dWidth = point.x;
        dHeight = point.y;
        rect = new Rect(0, 0, dWidth, dHeight);
        pajaros = new Bitmap[]{BitmapFactory.decodeResource(getResources(), R.drawable.frame_4),
                BitmapFactory.decodeResource(getResources(), R.drawable.frame_5)};

        pajaroX = dWidth / 4; // Colocar el pájaro más a la izquierda
        pajaroY = dHeight / 2 - pajaros[0].getHeight() / 2;

        distanciaTubos = dWidth * 3 / 4;
        minTubo = gap / 2;
        maxTubo = dHeight - minTubo - gap;
        rd = new Random();

        for (int i = 0; i < numTubos; i++) {
            tuboX[i] = dWidth + i * distanciaTubos;
            topTuboY[i] = minTubo + rd.nextInt(maxTubo - minTubo + 1);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(backgraund, null, rect, null);
        pajaroFrame = pajaroFrame == 0 ? 1 : 0;

        for (int i = 0; i < numTubos; i++) {
            if (juego_State) tuboX[i] -= tuboVelocidad;

            Rect dstTop = new Rect(tuboX[i], 0, tuboX[i] + topTubo.getWidth(), topTuboY[i]);
            Rect dstBottom = new Rect(tuboX[i], topTuboY[i] + gap, tuboX[i] + bottonTubo.getWidth(), dHeight);
            canvas.drawBitmap(topTubo, null, dstTop, null);
            canvas.drawBitmap(bottonTubo, null, dstBottom, null);

            if (juego_State && !scored[i] && pajaroX > tuboX[i] + topTubo.getWidth()) {
                score++;
                scored[i] = true;
            }

            if (tuboX[i] < -topTubo.getWidth()) {
                tuboX[i] += numTubos * distanciaTubos;
                topTuboY[i] = minTubo + rd.nextInt(maxTubo - minTubo + 1);
                scored[i] = false;
            }
        }

        if (juego_State) {
            velocidad += gravedad;
            pajaroY += velocidad;
        }

        pajaroY = Math.max(0, Math.min(pajaroY, dHeight - pajaros[0].getHeight()));
        canvas.drawBitmap(pajaros[pajaroFrame], pajaroX, pajaroY, null);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(100);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(score), dWidth / 2, 150, textPaint);

        if (juego_State && checkCollision()) {
            juego_State = false;
            handler.removeCallbacks(runnable);
            Intent intent = new Intent(getContext(), GameOver.class);
            intent.putExtra("score", score);
            getContext().startActivity(intent);
            ((Activity) getContext()).finish();
            return;
        }

        handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            velocidad = -30;
            juego_State = true;
        }
        return true;
    }

    private boolean checkCollision() {
        if (pajaroY <= 0 || pajaroY >= dHeight - pajaros[0].getHeight()) return true;

        for (int i = 0; i < numTubos; i++) {
            int horizontalMargin = pajaros[0].getWidth() / 4;

            if (pajaroX + pajaros[0].getWidth() - horizontalMargin > tuboX[i] &&
                    pajaroX + horizontalMargin < tuboX[i] + topTubo.getWidth()) {

                if (pajaroY + pajaros[0].getHeight() / 4 < topTuboY[i] ||
                        pajaroY + pajaros[0].getHeight() * 3 / 4 > topTuboY[i] + gap) {
                    return true;
                }
            }
        }
        return false;
    }
}

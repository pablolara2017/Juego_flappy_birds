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
    final int UPDATE_MILLIS = 30;
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
    int tubosPasados = 0;
    int velocidadInicial = 10;
    int incrementoVelocidad = 4;

    // Variables para el sistema de niveles
    private boolean mostrarNivel = false;
    private long tiempoMostrarNivel = 0;
    private final long DURACION_NIVEL = 1500; // 1.5 segundos
    private int nivelActual = 1;
    private Paint nivelPaint = new Paint();
    private float escalaTexto = 1f;
    private final float ESCALA_MAXIMA = 1.5f;

    public VistaJuego(Context context) {
        super(context);
        handler = new Handler();
        runnable = this::invalidate;

        backgraund = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        topTubo = BitmapFactory.decodeResource(getResources(), R.drawable.tuberia1_2);
        bottonTubo = BitmapFactory.decodeResource(getResources(), R.drawable.tuberia1_2);
        display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getSize(point);
        dWidth = point.x;
        dHeight = point.y;
        rect = new Rect(0, 0, dWidth, dHeight);
        pajaros = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.frame_4),
                BitmapFactory.decodeResource(getResources(), R.drawable.frame_5)
        };

        pajaroX = dWidth / 4;
        pajaroY = dHeight / 2 - pajaros[0].getHeight() / 2;

        distanciaTubos = dWidth * 3 / 4;
        minTubo = gap / 2;
        maxTubo = dHeight - minTubo - gap;
        rd = new Random();

        for (int i = 0; i < numTubos; i++) {
            tuboX[i] = dWidth + i * distanciaTubos;
            topTuboY[i] = minTubo + rd.nextInt(maxTubo - minTubo + 1);
        }

        // Configuración del texto de nivel
        nivelPaint.setColor(Color.YELLOW);
        nivelPaint.setTextSize(120);
        nivelPaint.setTextAlign(Paint.Align.CENTER);
        nivelPaint.setStyle(Paint.Style.STROKE);
        nivelPaint.setStrokeWidth(5);
        nivelPaint.setAntiAlias(true);
        nivelPaint.setShadowLayer(10, 0, 0, Color.BLACK);
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
                tubosPasados++;

                if (tubosPasados % 10 == 0) {
                    aumentarVelocidad();
                    mostrarCambioNivel();
                }
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
        textPaint.setAntiAlias(true);
        canvas.drawText(String.valueOf(score), dWidth / 2, 150, textPaint);

        // Mostrar indicador de nivel
        if (mostrarNivel) {
            long tiempoActual = System.currentTimeMillis();
            if (tiempoActual - tiempoMostrarNivel > DURACION_NIVEL) {
                mostrarNivel = false;
            } else {
                float progreso = (float)(tiempoActual - tiempoMostrarNivel) / DURACION_NIVEL;
                escalaTexto = 1 + (ESCALA_MAXIMA - 1) * (1 - Math.abs(progreso - 0.5f) * 2);

                canvas.save();
                canvas.scale(escalaTexto, escalaTexto, dWidth / 2, dHeight / 3);
                nivelPaint.setAlpha((int)(255 * (1 - progreso * 0.5f)));
                canvas.drawText("NIVEL " + nivelActual, dWidth / 2, dHeight / 3, nivelPaint);
                canvas.restore();
            }
        }

        if (juego_State && checkCollision()) {
            juego_State = false;
            handler.removeCallbacks(runnable);
            Intent intent = new Intent(getContext(), GameOver.class);
            intent.putExtra("score", score);
            intent.putExtra("nivel", nivelActual);
            getContext().startActivity(intent);
            ((Activity) getContext()).finish();
            return;
        }

        handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    private void aumentarVelocidad() {
        tuboVelocidad += incrementoVelocidad;
        nivelActual++;
    }

    private void mostrarCambioNivel() {
        mostrarNivel = true;
        tiempoMostrarNivel = System.currentTimeMillis();
        escalaTexto = 1f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            velocidad = -30;
            if (!juego_State) {
                // Reiniciar juego
                juego_State = true;
                score = 0;
                tubosPasados = 0;
                nivelActual = 1;
                tuboVelocidad = velocidadInicial;

                // Reiniciar posición del pájaro
                pajaroY = dHeight / 2 - pajaros[0].getHeight() / 2;
                velocidad = 0;

                // Reiniciar tubos
                for (int i = 0; i < numTubos; i++) {
                    tuboX[i] = dWidth + i * distanciaTubos;
                    topTuboY[i] = minTubo + rd.nextInt(maxTubo - minTubo + 1);
                    scored[i] = false;
                }
            }
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
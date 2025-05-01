package com.example.flappy_bird_proyecto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class GameOver extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // Referencias a las vistas
        TextView puntuacion = findViewById(R.id.puntuacion);
        TextView nivel = findViewById(R.id.nivel);
        Button restartButton = findViewById(R.id.btnReiniciar);

        // Obtener puntuación y nivel
        int score = getIntent().getIntExtra("score", 0);
        int level = getIntent().getIntExtra("nivel", 1);

        puntuacion.setText("Puntuación: " + score);
        nivel.setText("Nivel alcanzado: " + level);

        // Configurar el botón
        restartButton.setOnClickListener(v -> restartGame());
    }

    private void restartGame() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
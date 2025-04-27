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
        TextView scoreText = findViewById(R.id.scoreText);
        Button restartButton = findViewById(R.id.btnReiniciar);

        // Verifica que las vistas no sean nulas
        if (scoreText == null || restartButton == null) {
            throw new RuntimeException("No se encontraron las vistas en el layout");
        }

        // Obtener puntuación
        int score = getIntent().getIntExtra("score", 0);
        scoreText.setText("Puntuación: " + score);

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
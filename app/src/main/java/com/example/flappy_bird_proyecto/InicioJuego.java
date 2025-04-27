package com.example.flappy_bird_proyecto;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class InicioJuego extends Activity {

    VistaJuego vJuego;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vJuego=new VistaJuego(this);
        setContentView(vJuego);

    }
}

package br.com.projetoIntegrador.presentation.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import br.com.projetoIntegrador.R;

@SuppressLint("CustomSplashScreen") // Necessário se o target SDK for 31+ para suprimir aviso do novo SplashScreen API
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 3000; // 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Após o timeout, iniciar a MainActivity (tela de seleção de perfil)
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finaliza a SplashActivity para que o usuário não volte para ela
            }
        }, SPLASH_TIMEOUT);
    }
}
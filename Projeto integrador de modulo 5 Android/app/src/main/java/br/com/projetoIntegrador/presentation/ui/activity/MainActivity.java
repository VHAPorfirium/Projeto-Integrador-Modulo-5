package br.com.projetoIntegrador.presentation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import br.com.projetoIntegrador.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPaciente = findViewById(R.id.btnPaciente);
        Button btnFuncionario = findViewById(R.id.btnFuncionario);

        btnPaciente.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginPacienteActivity.class);
            startActivity(intent);
        });

        btnFuncionario.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginFuncionarioActivity.class);
            startActivity(intent);
        });
    }
}
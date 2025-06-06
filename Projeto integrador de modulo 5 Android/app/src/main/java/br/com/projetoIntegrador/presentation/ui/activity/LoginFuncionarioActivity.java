package br.com.projetoIntegrador.presentation.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.fcm.MyFirebaseService;
import br.com.projetoIntegrador.presentation.viewmodel.AuthViewModel;

public class LoginFuncionarioActivity extends AppCompatActivity {

    private static final String TAG = "LoginFuncActivity";
    private AuthViewModel authViewModel;
    private TextInputEditText editTextMatricula;
    private TextInputEditText editTextPassword;
    private TextInputLayout tilMatricula, tilPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_funcionario);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        editTextMatricula = findViewById(R.id.editTextMatricula);
        editTextPassword = findViewById(R.id.editTextSenhaFuncionario);
        tilMatricula = findViewById(R.id.tilMatricula);
        tilPassword = findViewById(R.id.tilSenhaFuncionario);
        Button buttonLogin = findViewById(R.id.buttonEntrarFuncionario);
        TextView textViewForgotPassword = findViewById(R.id.textViewEsqueciSenhaFuncionario);

        buttonLogin.setOnClickListener(view -> {
            String matricula = editTextMatricula.getText().toString();
            String password = editTextPassword.getText().toString();

            boolean valid = true;
            if (TextUtils.isEmpty(matricula)) {
                tilMatricula.setError("Matrícula não pode ser vazia");
                valid = false;
            } else {
                tilMatricula.setError(null);
            }

            if (TextUtils.isEmpty(password)) {
                tilPassword.setError("Senha não pode ser vazia");
                valid = false;
            } else {
                tilPassword.setError(null);
            }

            if (valid) {
                performLogin(matricula, password);
            }
        });

        textViewForgotPassword.setOnClickListener(view -> {
            Toast.makeText(LoginFuncionarioActivity.this, "Funcionalidade 'Esqueci a senha' a ser implementada.", Toast.LENGTH_SHORT).show();
        });

        // CORREÇÃO: O observer que ficava aqui no onCreate foi removido.
    }

    private void performLogin(String matricula, String password) {
        // CORREÇÃO: A observação da resposta da API é feita aqui.
        authViewModel.loginFuncionario(matricula, password).observe(this, loginResponse -> {
            if (loginResponse == null) {
                Toast.makeText(this, "Erro desconhecido no login.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "LoginResponseDto is null");
                return;
            }

            if (loginResponse.isSuccess()) {
                Toast.makeText(LoginFuncionarioActivity.this, "Login bem-sucedido! Bem-vindo, " + loginResponse.getUserName(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "Login Funcionário Sucesso: " + loginResponse.getUserId() + " - " + loginResponse.getUserName() + " - Tipo: " + loginResponse.getUserType());

                SharedPreferences prefs = getSharedPreferences(MyFirebaseService.SHARED_PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("FUNCIONARIO_ID_KEY", loginResponse.getUserId());
                editor.putString("USER_TYPE", loginResponse.getUserType()); // Pode ser RECEPCIONISTA, PROFISSIONAL_SAUDE, etc.
                editor.putString("USER_NAME", loginResponse.getUserName()); // Salva o nome para usar depois
                editor.apply();

                String userRole = loginResponse.getUserType();

                // Backend retorna o papel específico (EmployeeRole).
                // Frontend direciona para a tela correta.
                if ("RECEPCIONISTA".equalsIgnoreCase(userRole)) {
                    startActivity(new Intent(this, RecepcionistaActivity.class));
                } else if ("PROFISSIONAL_SAUDE".equalsIgnoreCase(userRole)) {
                    startActivity(new Intent(this, ProfissionalSaudeActivity.class));
                } else if ("ADMINISTRADOR".equalsIgnoreCase(userRole) || "DIRETOR".equalsIgnoreCase(userRole)) {
                    startActivity(new Intent(this, AdministradorActivity.class));
                } else {
                    Toast.makeText(this, "Tipo de funcionário desconhecido: " + userRole, Toast.LENGTH_LONG).show();
                    return;
                }
                finishAffinity(); // Fecha todas as activities anteriores da pilha

            } else {
                Toast.makeText(LoginFuncionarioActivity.this, "Falha no login: " + loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                Log.w(TAG, "Login Funcionário Falhou: " + loginResponse.getMessage());
            }
        });
    }
}
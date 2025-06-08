package br.com.projetoIntegrador.presentation.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import br.com.projetoIntegrador.databinding.ActivityLoginFuncionarioBinding;
import br.com.projetoIntegrador.fcm.MyFirebaseService;
import br.com.projetoIntegrador.network.LoginResponseDto; // DTO de resposta
import br.com.projetoIntegrador.presentation.viewmodel.AuthViewModel;

import br.com.projetoIntegrador.presentation.ui.activity.RecepcionistaActivity;
import br.com.projetoIntegrador.presentation.ui.activity.ProfissionalSaudeActivity;
import br.com.projetoIntegrador.presentation.ui.activity.AdministradorActivity;

public class LoginFuncionarioActivity extends AppCompatActivity {

    private static final String TAG = "LoginFuncActivity";

    private ActivityLoginFuncionarioBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginFuncionarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.buttonEntrarFuncionario.setOnClickListener(view -> {
            String matricula = binding.editTextMatricula.getText().toString().trim();

            if (TextUtils.isEmpty(matricula)) {
                binding.tilMatricula.setError("Matrícula não pode ser vazia");
            } else {
                binding.tilMatricula.setError(null);
                performLogin(matricula, ""); // senha vazia para funcionário
            }
        });
    }

    private void performLogin(String matricula, String password) {
        setLoadingState(true);

        authViewModel.loginFuncionario(matricula, password)
                .observe(this, loginResponse -> {
                    setLoadingState(false);

                    if (loginResponse == null) {
                        Toast.makeText(this, "Erro de comunicação com o servidor.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Resposta de login veio nula");
                        return;
                    }

                    if (loginResponse.isSuccess()) {
                        Log.i(TAG, "Login Funcionário Sucesso: "
                                + loginResponse.getUserId() + " - "
                                + loginResponse.getUserName() + " - Tipo: "
                                + loginResponse.getUserType());

                        salvarCredenciais(loginResponse);
                        redirecionarParaTelaCorreta(loginResponse.getUserType());

                    } else {
                        Toast.makeText(this,
                                "Falha no login: " + loginResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                        Log.w(TAG, "Login Funcionário Falhou: " + loginResponse.getMessage());
                    }
                });
    }

    private void setLoadingState(boolean isLoading) {
        binding.progressBarLogin.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.buttonEntrarFuncionario.setEnabled(!isLoading);
    }

    private void salvarCredenciais(LoginResponseDto loginResponse) {
        SharedPreferences prefs = getSharedPreferences(
                MyFirebaseService.SHARED_PREFS_NAME,
                MODE_PRIVATE
        );
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("FUNCIONARIO_ID_KEY", loginResponse.getUserId());
        editor.putString("USER_TYPE", loginResponse.getUserType());
        editor.putString("USER_NAME", loginResponse.getUserName());
        editor.apply();
    }

    private void redirecionarParaTelaCorreta(String userRole) {
        if (userRole == null) {
            Toast.makeText(this, "Tipo de usuário desconhecido.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent;
        switch (userRole.toUpperCase()) {
            case "RECEPCIONISTA":
                intent = new Intent(this, RecepcionistaActivity.class);
                break;

            case "PROFISSIONAL_SAUDE":
            case "FUNCIONARIO":
                intent = new Intent(this, ProfissionalSaudeActivity.class);
                break;

            case "ADMINISTRADOR":
            case "DIRETOR":
                intent = new Intent(this, AdministradorActivity.class);
                break;

            default:
                Toast.makeText(this,
                        "Tipo de funcionário não reconhecido: " + userRole,
                        Toast.LENGTH_LONG).show();
                return;
        }

        startActivity(intent);
        finishAffinity(); // fecha todas as Activities anteriores
    }
}

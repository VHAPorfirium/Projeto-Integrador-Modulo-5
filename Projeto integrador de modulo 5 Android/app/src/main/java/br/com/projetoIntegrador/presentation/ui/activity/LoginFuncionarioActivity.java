package br.com.projetoIntegrador.presentation.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import br.com.projetoIntegrador.databinding.ActivityLoginFuncionarioBinding;
import br.com.projetoIntegrador.fcm.MyFirebaseService;
import br.com.projetoIntegrador.network.LoginResponseDto;
import br.com.projetoIntegrador.presentation.viewmodel.AuthViewModel;

public class LoginFuncionarioActivity extends AppCompatActivity {

    private ActivityLoginFuncionarioBinding binding;
    private AuthViewModel authViewModel;
    private static final String TAG = "LoginFuncActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginFuncionarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.buttonEntrarFuncionario.setOnClickListener(v -> {
            String perfilSelecionado = binding.spinnerPerfil.getSelectedItem().toString();
            String matricula = binding.editTextMatricula.getText().toString().trim();

            if (TextUtils.isEmpty(matricula)) {
                binding.tilMatricula.setError("Matrícula não pode ser vazia");
            } else {
                binding.tilMatricula.setError(null);
                performLogin(matricula, "", perfilSelecionado);
            }
        });
    }

    private void performLogin(String matricula, String password, String perfilSelecionado) {
        setLoadingState(true);

        authViewModel.loginFuncionario(matricula, password)
                .observe(this, loginResponse -> {
                    setLoadingState(false);

                    if (loginResponse == null) {
                        Toast.makeText(this,
                                "Erro de comunicação com o servidor.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (loginResponse.isSuccess()) {
                        salvarCredenciais(loginResponse);
                        redirecionarPorPerfil(perfilSelecionado);
                    } else {
                        Toast.makeText(this,
                                "Falha no login: " + loginResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
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
        prefs.edit()
                .putLong("FUNCIONARIO_ID_KEY", loginResponse.getUserId())
                .putString("USER_TYPE", loginResponse.getUserType())
                .putString("USER_NAME", loginResponse.getUserName())
                .apply();
    }

    private void redirecionarPorPerfil(String perfil) {
        Intent intent;
        switch (perfil) {
            case "Médico":
                intent = new Intent(this, ProfissionalSaudeActivity.class);
                break;
            case "Recepcionista":
                intent = new Intent(this, RecepcionistaActivity.class);
                break;
            case "Administrador":
                intent = new Intent(this, AdministradorActivity.class);
                break;
            default:
                Toast.makeText(this,
                        "Perfil não reconhecido: " + perfil,
                        Toast.LENGTH_LONG).show();
                return;
        }
        startActivity(intent);
        finishAffinity();
    }
}

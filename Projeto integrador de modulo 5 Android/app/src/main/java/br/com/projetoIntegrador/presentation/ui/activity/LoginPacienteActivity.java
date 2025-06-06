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
import br.com.projetoIntegrador.util.CpfMaskTextWatcher;

public class LoginPacienteActivity extends AppCompatActivity {

    private static final String TAG = "LoginPacienteActivity";
    private AuthViewModel authViewModel;
    private TextInputEditText editTextCpf;
    private TextInputEditText editTextPassword;
    private TextInputLayout tilCpf, tilPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_paciente);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        editTextCpf = findViewById(R.id.editTextCpf);
        editTextPassword = findViewById(R.id.editTextPasswordPaciente);
        tilCpf = findViewById(R.id.tilCpf);
        tilPassword = findViewById(R.id.tilSenhaPaciente);
        Button buttonLogin = findViewById(R.id.buttonLoginPaciente);
        TextView textViewForgotPassword = findViewById(R.id.textViewEsqueciSenhaPaciente);

        editTextCpf.addTextChangedListener(new CpfMaskTextWatcher(editTextCpf));

        buttonLogin.setOnClickListener(view -> {
            String cpf = editTextCpf.getText().toString().replaceAll("[^0-9]", "");
            String password = editTextPassword.getText().toString();

            boolean valid = true;
            if (TextUtils.isEmpty(cpf) || cpf.length() != 11) {
                tilCpf.setError("CPF inválido");
                valid = false;
            } else {
                tilCpf.setError(null);
            }

            if (TextUtils.isEmpty(password)) {
                tilPassword.setError("Senha não pode ser vazia");
                valid = false;
            } else {
                tilPassword.setError(null);
            }

            if (valid) {
                performLogin(cpf, password);
            }
        });

        textViewForgotPassword.setOnClickListener(view -> {
            Toast.makeText(LoginPacienteActivity.this, "Funcionalidade 'Esqueci a senha' a ser implementada.", Toast.LENGTH_SHORT).show();
        });

        // CORREÇÃO: O observer que ficava aqui no onCreate foi removido.
        // A observação agora acontece dentro do método performLogin, que é o local correto.
    }

    private void performLogin(String cpf, String password) {
        // CORREÇÃO: A chamada de login agora retorna um LiveData, que observamos aqui.
        // O observador só será ativado quando o resultado do login chegar e será
        // automaticamente destruído com a Activity, evitando memory leaks.
        authViewModel.loginPaciente(cpf, password).observe(this, loginResponse -> {
            if (loginResponse == null) {
                Toast.makeText(this, "Erro desconhecido no login.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "LoginResponseDto is null");
                return;
            }

            if (loginResponse.isSuccess()) {
                Toast.makeText(LoginPacienteActivity.this, "Login bem-sucedido! Bem-vindo, " + loginResponse.getUserName(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "Login Paciente Sucesso: " + loginResponse.getUserId() + " - " + loginResponse.getUserName());

                SharedPreferences prefs = getSharedPreferences(MyFirebaseService.SHARED_PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(MyFirebaseService.PACIENTE_ID_KEY, loginResponse.getUserId());
                editor.putString("USER_TYPE", "paciente");
                editor.putString("USER_NAME", loginResponse.getUserName()); // É bom salvar o nome também
                editor.apply();

                Intent intent = new Intent(LoginPacienteActivity.this, PacienteDashboardActivity.class);
                startActivity(intent);
                finishAffinity(); // Fecha todas as activities anteriores da pilha
            } else {
                Toast.makeText(LoginPacienteActivity.this, "Falha no login: " + loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                Log.w(TAG, "Login Paciente Falhou: " + loginResponse.getMessage());
            }
        });
    }
}
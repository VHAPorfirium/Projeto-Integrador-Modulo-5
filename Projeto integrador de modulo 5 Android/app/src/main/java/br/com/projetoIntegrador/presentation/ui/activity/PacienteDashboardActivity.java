package br.com.projetoIntegrador.presentation.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.fcm.MyFirebaseService;
import br.com.projetoIntegrador.presentation.ui.fragment.AgendamentoFragment;
import br.com.projetoIntegrador.presentation.ui.fragment.ConfirmacaoFragment;
import br.com.projetoIntegrador.presentation.ui.fragment.FilaFragment;
import br.com.projetoIntegrador.presentation.ui.fragment.PerfilFragment;


public class PacienteDashboardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private Long pacienteId; // Para passar aos fragments se necessário

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_dashboard);

        // Recuperar ID do paciente das SharedPreferences
        SharedPreferences prefs = getSharedPreferences(MyFirebaseService.SHARED_PREFS_NAME, MODE_PRIVATE);
        pacienteId = prefs.getLong(MyFirebaseService.PACIENTE_ID_KEY, -1L);

        if (pacienteId == -1L) {
            // Tratar erro: pacienteId não encontrado, talvez redirecionar para login
            startActivity(new Intent(this, LoginPacienteActivity.class));
            finish();
            return;
        }

        toolbar = findViewById(R.id.toolbarPacienteDashboard);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.pacienteBottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                String title = getString(R.string.app_name);

                int itemId = item.getItemId();
                if (itemId == R.id.nav_inicio_paciente) {
                    selectedFragment = FilaFragment.newInstance(pacienteId);
                    title = "Início";
                } else if (itemId == R.id.nav_confirmacao_paciente) {
                    // ConfirmacaoFragment pode precisar de um ID de entrada específico,
                    // geralmente não é aberto diretamente pela navegação, mas por uma notificação.
                    // Por enquanto, pode ser um placeholder ou carregar se houver uma chamada ativa.
                    selectedFragment = new ConfirmacaoFragment(); // Ou ConfirmacaoFragment.newInstance(algumIdDeChamadaAtiva);
                    title = "Confirmação";
                } else if (itemId == R.id.nav_retornos_paciente) {
                    selectedFragment = AgendamentoFragment.newInstance(pacienteId);
                    title = "Agendar Retorno";
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(title);
                    }
                }
                return true;
            }
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_inicio_paciente); // Carga inicial
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.paciente_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_perfil_paciente) {
            loadFragment(PerfilFragment.newInstance(pacienteId));
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Meu Perfil");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.pacienteFragmentContainer, fragment);
        transaction.commit();
    }

    // Método para ser chamado por MyFirebaseService para mostrar ConfirmacaoFragment
    public void mostrarConfirmacaoChamada(long attendanceEntryId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_confirmacao_paciente); // Muda para a aba
        }
        loadFragment(ConfirmacaoFragment.newInstance(attendanceEntryId));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Confirmação de Chamada");
        }
    }
}
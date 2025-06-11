package br.com.projetoIntegrador.presentation.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.fcm.MyFirebaseService;
import br.com.projetoIntegrador.presentation.ui.fragment.AgendamentoFragment;
import br.com.projetoIntegrador.presentation.ui.fragment.ConfirmacaoFragment;
import br.com.projetoIntegrador.presentation.ui.fragment.FilaFragment;
import br.com.projetoIntegrador.presentation.ui.fragment.PerfilFragment;
import br.com.projetoIntegrador.presentation.ui.activity.LoginPacienteActivity;
import br.com.projetoIntegrador.presentation.ui.activity.MainActivity;

public class PacienteDashboardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private long pacienteId;

    // Receiver para atualizações vindas do FCM
    private final BroadcastReceiver patientCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long attendanceId = intent.getLongExtra(
                    MyFirebaseService.EXTRA_ATTENDANCE_ID, -1L
            );
            mostrarConfirmacaoChamada(attendanceId);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_dashboard);

        // 1) Valida login do paciente
        SharedPreferences prefs = getSharedPreferences(
                MyFirebaseService.SHARED_PREFS_NAME, MODE_PRIVATE
        );
        pacienteId = prefs.getLong(MyFirebaseService.PACIENTE_ID_KEY, -1L);
        if (pacienteId == -1L) {
            startActivity(new Intent(this, LoginPacienteActivity.class));
            finish();
            return;
        }

        // 2) Toolbar
        toolbar = findViewById(R.id.toolbarPacienteDashboard);
        setSupportActionBar(toolbar);

        // 3) BottomNavigation
        bottomNavigationView = findViewById(R.id.pacienteBottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selected = null;
                        String title = getString(R.string.app_name);

                        int id = item.getItemId();
                        if (id == R.id.nav_inicio_paciente) {
                            selected = FilaFragment.newInstance(pacienteId);
                            title = "Sua Posição";
                        }
                        else if (id == R.id.nav_retornos_paciente) {
                            selected = AgendamentoFragment.newInstance(pacienteId);
                            title = "Agendamentos";
                        }
                        else if (id == R.id.nav_perfil_paciente) {
                            selected = PerfilFragment.newInstance(pacienteId);
                            title = "Meu Perfil";
                        }

                        if (selected != null) {
                            loadFragment(selected);
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setTitle(title);
                            }
                        }
                        return true;
                    }
                }
        );

        // Carga inicial
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_inicio_paciente);
        }

        // 4) Trata deep-link vindo da notificação
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null
                && "ConfirmacaoFragment".equals(intent.getStringExtra("open_fragment"))) {
            long attendanceId = intent.getLongExtra(
                    MyFirebaseService.EXTRA_ATTENDANCE_ID, -1L
            );
            mostrarConfirmacaoChamada(attendanceId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registra o Receiver para atualizações em tempo real
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                        patientCallReceiver,
                        new IntentFilter(MyFirebaseService.ACTION_PATIENT_CALLED)
                );
    }

    @Override
    protected void onPause() {
        // Desregistra ao sair da Activity
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(patientCallReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.paciente_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout_paciente) {
            getSharedPreferences(
                    MyFirebaseService.SHARED_PREFS_NAME, MODE_PRIVATE
            ).edit().clear().apply();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Troca o fragment no container
    private void loadFragment(Fragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.pacienteFragmentContainer, fragment);
        tx.commit();
    }

    // Exibe o ConfirmacaoFragment com o ID do atendimento
    public void mostrarConfirmacaoChamada(long attendanceEntryId) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(
                R.id.pacienteFragmentContainer,
                ConfirmacaoFragment.newInstance(attendanceEntryId)
        );
        tx.commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Confirmação de Chamada");
        }
    }
}

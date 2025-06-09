package br.com.projetoIntegrador.presentation.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.databinding.ActivityProfissionalSaudeBinding;
import br.com.projetoIntegrador.fcm.MyFirebaseService;
import br.com.projetoIntegrador.network.AttendanceEntryDto;
import br.com.projetoIntegrador.network.PacienteDto;
import br.com.projetoIntegrador.network.SpecialtyDto;
import br.com.projetoIntegrador.presentation.adapter.FilaProfissionalAdapter;
import br.com.projetoIntegrador.presentation.viewmodel.AttendanceEntryViewModel;
import br.com.projetoIntegrador.presentation.viewmodel.PacienteViewModel;
import br.com.projetoIntegrador.presentation.viewmodel.SpecialtyViewModel;

public class ProfissionalSaudeActivity extends AppCompatActivity
        implements FilaProfissionalAdapter.OnItemFilaClickListener {

    private static final String TAG = "ProfSaudeActivity";
    private static final long REFRESH_FILA_INTERVAL = 20_000L;

    private ActivityProfissionalSaudeBinding binding;
    private AttendanceEntryViewModel attendanceEntryViewModel;
    private PacienteViewModel pacienteViewModel;
    private SpecialtyViewModel specialtyViewModel;
    private FilaProfissionalAdapter filaAdapter;

    private Handler handler;
    private Runnable refreshFilaRunnable;

    private String nomeFuncionarioLogado;
    private long idFuncionarioLogado;

    private Map<Long, String> mapNomesPacientes         = new HashMap<>();
    private Map<Long, String> mapNomesEspecialidades   = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfissionalSaudeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1) Lê SharedPreferences
        SharedPreferences prefs = getSharedPreferences(
                MyFirebaseService.SHARED_PREFS_NAME,
                MODE_PRIVATE
        );
        nomeFuncionarioLogado = prefs.getString("USER_NAME", "Profissional");
        idFuncionarioLogado   = prefs.getLong("FUNCIONARIO_ID_KEY", -1L);

        // 2) Toolbar com saudação
        setSupportActionBar(binding.toolbarProfissionalSaude);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Olá, " + nomeFuncionarioLogado);
        }

        // 3) ViewModels
        attendanceEntryViewModel = new ViewModelProvider(this)
                .get(AttendanceEntryViewModel.class);
        pacienteViewModel       = new ViewModelProvider(this)
                .get(PacienteViewModel.class);
        specialtyViewModel      = new ViewModelProvider(this)
                .get(SpecialtyViewModel.class);

        // 4) RecyclerView + Adapter
        filaAdapter = new FilaProfissionalAdapter(
                this,
                new java.util.ArrayList<>(),
                mapNomesPacientes,
                mapNomesEspecialidades,
                this
        );
        binding.recyclerViewFilaProfissional.setLayoutManager(
                new LinearLayoutManager(this)
        );
        binding.recyclerViewFilaProfissional.setAdapter(filaAdapter);

        // 5) FAB “Chamar próximo”
        binding.fabChamarProximo.setOnClickListener(v -> chamarProximoPaciente());

        handler = new Handler(Looper.getMainLooper());

        // 6) Observadores e carregamento inicial
        observarViewModels();
        carregarDadosIniciais();
    }

    private void observarViewModels() {
        // Carrega mapeamento de nomes de pacientes
        pacienteViewModel.listAllPacientes().observe(this, pacientes -> {
            if (pacientes != null) {
                mapNomesPacientes.clear();
                for (PacienteDto p : pacientes) {
                    mapNomesPacientes.put(p.getId(), p.getFullName());
                }
                carregarFila();
            }
        });

        // Carrega mapeamento de nomes de especialidades
        specialtyViewModel.listAllSpecialties().observe(this, specs -> {
            if (specs != null) {
                mapNomesEspecialidades.clear();
                for (SpecialtyDto s : specs) {
                    mapNomesEspecialidades.put(s.getId(), s.getName());
                }
                carregarFila();
            }
        });

        // Carrega a lista de atendimentos (fila)
        attendanceEntryViewModel.listAllAttendanceEntries()
                .observe(this, entries -> {
                    if (entries != null
                            && !mapNomesPacientes.isEmpty()
                            && !mapNomesEspecialidades.isEmpty()) {

                        List<AttendanceEntryDto> filaFiltrada = entries.stream()
                                .filter(e ->
                                        "AGUARDANDO".equalsIgnoreCase(e.getStatus()) ||
                                                "CONFIRMADO".equalsIgnoreCase(e.getStatus())
                                )
                                .sorted(Comparator.comparing(
                                        AttendanceEntryDto::getCheckInTime,
                                        Comparator.nullsLast(Comparator.naturalOrder())
                                ))
                                .collect(Collectors.toList());

                        filaAdapter.updateData(
                                filaFiltrada,
                                mapNomesPacientes,
                                mapNomesEspecialidades
                        );

                        boolean vazia = filaFiltrada.isEmpty();
                        binding.tvFilaVaziaProfissional
                                .setVisibility(vazia ? View.VISIBLE : View.GONE);
                        binding.recyclerViewFilaProfissional
                                .setVisibility(vazia ? View.GONE : View.VISIBLE);

                    } else if (entries == null) {
                        Toast.makeText(
                                this,
                                "Erro ao carregar a fila.",
                                Toast.LENGTH_SHORT
                        ).show();
                        binding.tvFilaVaziaProfissional
                                .setText("Erro ao carregar a fila.");
                        binding.tvFilaVaziaProfissional
                                .setVisibility(View.VISIBLE);
                        binding.recyclerViewFilaProfissional
                                .setVisibility(View.GONE);
                    }
                });
    }

    private void carregarDadosIniciais() {
        // Dispara carregamento de pacientes e especialidades
        pacienteViewModel.listAllPacientes();
        specialtyViewModel.listAllSpecialties();
    }

    private void carregarFila() {
        // Só recarrega quando já temos os mapas de nomes
        if (!mapNomesPacientes.isEmpty()
                && !mapNomesEspecialidades.isEmpty()) {
            attendanceEntryViewModel.listAllAttendanceEntries();
        } else {
            Log.d(TAG, "Aguardando mapeamentos de nomes...");
        }
    }

    private void chamarProximoPaciente() {
        List<AttendanceEntryDto> atual = filaAdapter.getCurrentList();
        AttendanceEntryDto proximo = null;

        if (atual != null && !atual.isEmpty()) {
            proximo = atual.stream()
                    .filter(e -> "CONFIRMADO".equalsIgnoreCase(e.getStatus()))
                    .findFirst()
                    .orElse(atual.get(0));
        }

        if (proximo != null) {
            Intent intent = new Intent(this, ChamarProximoActivity.class);
            intent.putExtra("ATTENDANCE_ENTRY_ID", proximo.getId());
            intent.putExtra("PACIENTE_ID", proximo.getPacienteId());
            startActivity(intent);
        } else {
            Toast.makeText(
                    this,
                    "Não há pacientes aguardando ou confirmados na fila.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onItemClick(AttendanceEntryDto entrada) {
        // Clicou em um item da lista: chama esse paciente
        Intent intent = new Intent(this, ChamarProximoActivity.class);
        intent.putExtra("ATTENDANCE_ENTRY_ID", entrada.getId());
        intent.putExtra("PACIENTE_ID", entrada.getPacienteId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profissional_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh_fila_profissional) {
            carregarDadosIniciais();
            Toast.makeText(this, "Fila atualizada", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (item.getItemId() == R.id.action_logout_profissional) {
            getSharedPreferences(
                    MyFirebaseService.SHARED_PREFS_NAME,
                    MODE_PRIVATE
            ).edit().clear().apply();
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Agenda atualização periódica da fila
        refreshFilaRunnable = () -> {
            carregarFila();
            handler.postDelayed(refreshFilaRunnable, REFRESH_FILA_INTERVAL);
        };
        handler.postDelayed(refreshFilaRunnable, REFRESH_FILA_INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshFilaRunnable);
    }
}

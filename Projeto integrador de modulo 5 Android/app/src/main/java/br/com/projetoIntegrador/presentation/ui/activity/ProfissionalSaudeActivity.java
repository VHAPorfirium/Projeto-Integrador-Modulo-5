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
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.fcm.MyFirebaseService;
import br.com.projetoIntegrador.network.AttendanceEntryDto;
import br.com.projetoIntegrador.network.PacienteDto;
import br.com.projetoIntegrador.network.SpecialtyDto;
import br.com.projetoIntegrador.presentation.adapter.FilaProfissionalAdapter;
import br.com.projetoIntegrador.presentation.viewmodel.AttendanceEntryViewModel;
import br.com.projetoIntegrador.presentation.viewmodel.PacienteViewModel;
import br.com.projetoIntegrador.presentation.viewmodel.SpecialtyViewModel;

public class ProfissionalSaudeActivity extends AppCompatActivity implements FilaProfissionalAdapter.OnItemFilaClickListener {

    private static final String TAG = "ProfSaudeActivity";
    private AttendanceEntryViewModel attendanceEntryViewModel;
    private PacienteViewModel pacienteViewModel; // Para buscar nomes de pacientes
    private SpecialtyViewModel specialtyViewModel; // Para buscar nomes de especialidades

    private RecyclerView recyclerViewFilaProfissional;
    private FilaProfissionalAdapter filaAdapter;
    private FloatingActionButton fabChamarProximo;
    private TextView tvFilaVaziaProfissional;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshFilaRunnable;
    private final long REFRESH_FILA_INTERVAL = 20000; // 20 segundos

    private String nomeFuncionarioLogado = "Profissional";
    private Long idFuncionarioLogado; // Poderia ser usado para filtrar fila por especialidade do profissional

    private Map<Long, String> mapNomesPacientes = new HashMap<>();
    private Map<Long, String> mapNomesEspecialidades = new HashMap<>();
    private List<PacienteDto> listaPacientesCache = new ArrayList<>();
    private List<SpecialtyDto> listaEspecialidadesCache = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profissional_saude);

        Toolbar toolbar = findViewById(R.id.toolbarProfissionalSaude);
        SharedPreferences prefs = getSharedPreferences(MyFirebaseService.SHARED_PREFS_NAME, MODE_PRIVATE);
        nomeFuncionarioLogado = prefs.getString("USER_NAME", "Profissional");
        idFuncionarioLogado = prefs.getLong("FUNCIONARIO_ID_KEY", -1L); // Supondo que salvou o ID
        toolbar.setTitle("Olá, " + nomeFuncionarioLogado);
        setSupportActionBar(toolbar);

        attendanceEntryViewModel = new ViewModelProvider(this).get(AttendanceEntryViewModel.class);
        pacienteViewModel = new ViewModelProvider(this).get(PacienteViewModel.class);
        specialtyViewModel = new ViewModelProvider(this).get(SpecialtyViewModel.class);


        recyclerViewFilaProfissional = findViewById(R.id.recyclerViewFilaProfissional);
        fabChamarProximo = findViewById(R.id.fabChamarProximo);
        tvFilaVaziaProfissional = findViewById(R.id.tvFilaVaziaProfissional);

        setupRecyclerView();

        fabChamarProximo.setOnClickListener(v -> chamarProximoPaciente());

        observarViewModels();
        carregarDadosIniciais(); // Carrega pacientes e especialidades primeiro
    }

    private void setupRecyclerView() {
        recyclerViewFilaProfissional.setLayoutManager(new LinearLayoutManager(this));
        filaAdapter = new FilaProfissionalAdapter(this, new ArrayList<>(), mapNomesPacientes, mapNomesEspecialidades, this);
        recyclerViewFilaProfissional.setAdapter(filaAdapter);
    }

    private void observarViewModels() {
        pacienteViewModel.listAllPacientes().observe(this, pacientes -> {
            if (pacientes != null) {
                listaPacientesCache.clear();
                listaPacientesCache.addAll(pacientes);
                mapNomesPacientes.clear();
                for (PacienteDto p : pacientes) {
                    mapNomesPacientes.put(p.getId(), p.getFullName());
                }
                // Após carregar pacientes, recarrega a fila para que o adapter tenha os nomes
                carregarFila();
            }
        });

        specialtyViewModel.listAllSpecialties().observe(this, specialties -> {
            if (specialties != null) {
                listaEspecialidadesCache.clear();
                listaEspecialidadesCache.addAll(specialties);
                mapNomesEspecialidades.clear();
                for (SpecialtyDto s : specialties) {
                    mapNomesEspecialidades.put(s.getId(), s.getName());
                }
                // Após carregar especialidades, recarrega a fila
                carregarFila();
            }
        });


        attendanceEntryViewModel.listAllAttendanceEntries().observe(this, attendanceEntries -> {
            if (attendanceEntries != null && !mapNomesPacientes.isEmpty() && !mapNomesEspecialidades.isEmpty()) {
                // Filtrar e ordenar a fila para o profissional
                // TODO: Filtrar pela especialidade do profissional logado, se aplicável.
                // Por enquanto, mostra todos aguardando ou confirmados.
                List<AttendanceEntryDto> filaFiltrada = attendanceEntries.stream()
                        .filter(entry -> "AGUARDANDO".equalsIgnoreCase(entry.getStatus()) || "CONFIRMADO".equalsIgnoreCase(entry.getStatus()))
                        .sorted(Comparator.comparing(AttendanceEntryDto::getCheckInTime, Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.toList());

                filaAdapter.updateData(filaFiltrada, mapNomesPacientes, mapNomesEspecialidades);

                if (filaFiltrada.isEmpty()) {
                    tvFilaVaziaProfissional.setVisibility(View.VISIBLE);
                    recyclerViewFilaProfissional.setVisibility(View.GONE);
                } else {
                    tvFilaVaziaProfissional.setVisibility(View.GONE);
                    recyclerViewFilaProfissional.setVisibility(View.VISIBLE);
                }

            } else if (attendanceEntries == null) { // Erro ao carregar
                Toast.makeText(this, "Erro ao carregar a fila.", Toast.LENGTH_SHORT).show();
                tvFilaVaziaProfissional.setText("Erro ao carregar a fila.");
                tvFilaVaziaProfissional.setVisibility(View.VISIBLE);
                recyclerViewFilaProfissional.setVisibility(View.GONE);
            }
            // Se attendanceEntries != null mas maps estão vazios, espera os maps carregarem.
        });
    }
    private void carregarDadosIniciais() {
        pacienteViewModel.listAllPacientes(); // Dispara o carregamento de pacientes
        specialtyViewModel.listAllSpecialties(); // Dispara o carregamento de especialidades
        // carregarFila() será chamado pelos observers de pacientes e especialidades
    }


    private void carregarFila() {
        Log.d(TAG, "Carregando fila para profissional...");
        // Só carrega a fila se os nomes já estiverem disponíveis para evitar mostrar IDs
        if (!mapNomesPacientes.isEmpty() && !mapNomesEspecialidades.isEmpty()) {
            attendanceEntryViewModel.listAllAttendanceEntries();
        } else {
            Log.d(TAG, "Aguardando carregamento de nomes de pacientes/especialidades.");
        }
    }

    private void chamarProximoPaciente() {
        List<AttendanceEntryDto> filaAtual = filaAdapter.getCurrentList();
        AttendanceEntryDto proximoPaciente = null;

        if (filaAtual != null && !filaAtual.isEmpty()) {
            // Prioriza CONFIRMADO, depois AGUARDANDO
            proximoPaciente = filaAtual.stream()
                    .filter(e -> "CONFIRMADO".equalsIgnoreCase(e.getStatus()))
                    .findFirst()
                    .orElse(filaAtual.stream()
                            .filter(e -> "AGUARDANDO".equalsIgnoreCase(e.getStatus()))
                            .findFirst()
                            .orElse(null));
        }

        if (proximoPaciente != null) {
            Intent intent = new Intent(ProfissionalSaudeActivity.this, ChamarProximoActivity.class);
            intent.putExtra("ATTENDANCE_ENTRY_ID", proximoPaciente.getId());
            intent.putExtra("PACIENTE_ID", proximoPaciente.getPacienteId());
            // O nome já estará disponível no mapNomesPacientes para a próxima tela buscar se necessário
            startActivity(intent);
        } else {
            Toast.makeText(this, "Não há pacientes aguardando ou confirmados na fila.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AttendanceEntryDto entrada) {
        // Se clicar em um item da lista, pode abrir a tela de chamar para aquele paciente específico
        Intent intent = new Intent(ProfissionalSaudeActivity.this, ChamarProximoActivity.class);
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
        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh_fila_profissional) {
            Toast.makeText(this, "Atualizando fila...", Toast.LENGTH_SHORT).show();
            carregarDadosIniciais(); // Recarrega tudo para garantir consistência
            return true;
        } else if (itemId == R.id.action_logout_profissional) {
            SharedPreferences prefs = getSharedPreferences(MyFirebaseService.SHARED_PREFS_NAME, MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDadosIniciais(); // Carrega ao resumir
        refreshFilaRunnable = new Runnable() {
            @Override
            public void run() {
                carregarFila(); // Apenas a fila, pois nomes/especialidades são mais estáticos
                handler.postDelayed(this, REFRESH_FILA_INTERVAL);
            }
        };
        handler.postDelayed(refreshFilaRunnable, REFRESH_FILA_INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshFilaRunnable);
    }
}
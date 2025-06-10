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

import java.util.ArrayList; // Importar ArrayList
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
        carregarDadosIniciais(); // Inicia o carregamento
    }

    private void observarViewModels() {
        // Carrega mapeamento de nomes de pacientes
        pacienteViewModel.listAllPacientes().observe(this, pacientes -> {
            if (pacientes != null) { // Agora 'pacientes' nunca será null se o backend retornar uma lista vazia
                Log.d(TAG, "Observador Pacientes: Recebida lista de tamanho: " + pacientes.size());
                mapNomesPacientes.clear();
                for (PacienteDto p : pacientes) {
                    mapNomesPacientes.put(p.getId(), p.getFullName());
                }
                Log.d(TAG, "Observador Pacientes: Mapeamento de nomes de pacientes preenchido. Tamanho: " + mapNomesPacientes.size());
                carregarFila(); // Tenta carregar a fila novamente após pacientes serem processados
            } else {
                // Este else agora só será atingido se houver um problema muito grave
                // que faça o LiveData retornar um null explícito ou não ser observado.
                Log.e(TAG, "Observador Pacientes: Lista de pacientes é NULA (problema inesperado)!");
                Toast.makeText(this, "Erro: Não foi possível carregar dados dos pacientes.", Toast.LENGTH_LONG).show();
                // Ainda assim, podemos tentar carregar a fila, ela só ficará vazia
                carregarFila();
            }
        });

        // Carrega mapeamento de nomes de especialidades
        specialtyViewModel.listAllSpecialties().observe(this, specs -> {
            if (specs != null) { // Agora 'specs' nunca será null se o backend retornar uma lista vazia
                Log.d(TAG, "Observador Especialidades: Recebida lista de tamanho: " + specs.size());
                mapNomesEspecialidades.clear();
                for (SpecialtyDto s : specs) {
                    mapNomesEspecialidades.put(s.getId(), s.getName());
                }
                Log.d(TAG, "Observador Especialidades: Mapeamento de nomes de especialidades preenchido. Tamanho: " + mapNomesEspecialidades.size());
                carregarFila(); // Tenta carregar a fila novamente após especialidades serem processadas
            } else {
                Log.e(TAG, "Observador Especialidades: Lista de especialidades é NULA (problema inesperado)!");
                Toast.makeText(this, "Erro: Não foi possível carregar dados das especialidades.", Toast.LENGTH_LONG).show();
                carregarFila();
            }
        });

        // Carrega a lista de atendimentos (fila)
        attendanceEntryViewModel.listAllAttendanceEntries()
                .observe(this, entries -> {
                    if (entries != null) { // Agora 'entries' nunca será null
                        Log.d(TAG, "Observador Atendimentos: Entradas brutas recebidas: " + entries.size());
                        Log.d(TAG, "Observador Atendimentos: mapNomesPacientes is empty? " + mapNomesPacientes.isEmpty());
                        Log.d(TAG, "Observador Atendimentos: mapNomesEspecialidades is empty? " + mapNomesEspecialidades.isEmpty());

                        if (!mapNomesPacientes.isEmpty() && !mapNomesEspecialidades.isEmpty()) {
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

                            Log.d(TAG, "Observador Atendimentos: Fila filtrada e ordenada. Tamanho final: " + filaFiltrada.size());

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
                            Log.d(TAG, "Observador Atendimentos: Fila visível? " + (!vazia));

                            if (vazia) {
                                binding.tvFilaVaziaProfissional.setText("Nenhum paciente aguardando no momento.");
                            }

                        } else {
                            Log.w(TAG, "Observador Atendimentos: Mapas de nomes ainda não preenchidos ou vazios. Tentando novamente na próxima...");
                            // Se os mapas estão vazios, a fila não pode ser populada corretamente.
                            // Mostrar mensagem de "carregando" ou "aguardando dados" seria ideal.
                            binding.tvFilaVaziaProfissional.setText("Carregando dados da fila...");
                            binding.tvFilaVaziaProfissional.setVisibility(View.VISIBLE);
                            binding.recyclerViewFilaProfissional.setVisibility(View.GONE);
                        }
                    } else {
                        // Este bloco só deve ser atingido se o LiveData de attendanceEntries retornar null,
                        // o que agora é evitado pelo Repositório ao retornar lista vazia em caso de falha.
                        Log.e(TAG, "Observador Atendimentos: Lista de entradas é NULA (problema inesperado)!");
                        Toast.makeText(
                                this,
                                "Erro ao carregar a fila de atendimentos.",
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
        Log.d(TAG, "Disparando carregamento inicial de pacientes e especialidades.");
        pacienteViewModel.listAllPacientes();
        specialtyViewModel.listAllSpecialties();
    }

    private void carregarFila() {
        // Só recarrega a fila de atendimentos quando já temos os mapas de nomes
        // (ou pelo menos um mapa não nulo, mesmo que vazio).
        if (mapNomesPacientes != null && mapNomesEspecialidades != null &&
                !mapNomesPacientes.isEmpty() && !mapNomesEspecialidades.isEmpty()) {
            Log.d(TAG, "Carregando fila: Mapas de nomes estão prontos. Solicitando entradas de atendimento.");
            attendanceEntryViewModel.listAllAttendanceEntries();
        } else {
            Log.d(TAG, "Carregando fila: Aguardando mapeamentos de nomes. Pacientes size: " + mapNomesPacientes.size() + ", Especialidades size: " + mapNomesEspecialidades.size());
            binding.tvFilaVaziaProfissional.setText("Carregando dados da fila...");
            binding.tvFilaVaziaProfissional.setVisibility(View.VISIBLE);
            binding.recyclerViewFilaProfissional.setVisibility(View.GONE);
        }
    }


    private void chamarProximoPaciente() {
        List<AttendanceEntryDto> atual = filaAdapter.getCurrentList();
        AttendanceEntryDto proximo = null;

        if (atual != null && !atual.isEmpty()) {
            // Prioriza pacientes CONFIRMADO, senão pega o primeiro AGUARDANDO
            proximo = atual.stream()
                    .filter(e -> "CONFIRMADO".equalsIgnoreCase(e.getStatus()))
                    .findFirst()
                    .orElse(
                            atual.stream()
                                    .filter(e -> "AGUARDANDO".equalsIgnoreCase(e.getStatus()))
                                    .findFirst()
                                    .orElse(null)
                    );
        }

        if (proximo != null) {
            Log.d(TAG, "ChamarProximoPaciente: Próximo paciente a ser chamado: ID " + proximo.getId() + ", Status: " + proximo.getStatus());
            Intent intent = new Intent(this, ChamarProximoActivity.class);
            intent.putExtra("ATTENDANCE_ENTRY_ID", proximo.getId());
            intent.putExtra("PACIENTE_ID", proximo.getPacienteId());
            startActivity(intent);
        } else {
            Log.d(TAG, "chamarProximoPaciente: Nao ha pacientes aguardando ou confirmados na fila.");
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
        Log.d(TAG, "onItemClick: Item da fila clicado. ID da entrada: " + entrada.getId());
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
            Log.d(TAG, "Menu: Atualizando fila manualmente.");
            carregarDadosIniciais(); // Recarrega tudo
            Toast.makeText(this, "Fila atualizada", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (item.getItemId() == R.id.action_logout_profissional) {
            Log.d(TAG, "Menu: Realizando logout do profissional.");
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
        Log.d(TAG, "onResume: Agendando atualização periódica da fila.");
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
        Log.d(TAG, "onPause: Removendo callbacks de atualização da fila.");
        handler.removeCallbacks(refreshFilaRunnable);
    }
}
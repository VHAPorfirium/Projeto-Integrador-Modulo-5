package br.com.projetoIntegrador.presentation.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import br.com.projetoIntegrador.presentation.adapter.FilaRecepcaoAdapter;
import br.com.projetoIntegrador.presentation.viewmodel.AttendanceEntryViewModel;
import br.com.projetoIntegrador.presentation.viewmodel.PacienteViewModel;
import br.com.projetoIntegrador.presentation.viewmodel.SpecialtyViewModel;
import br.com.projetoIntegrador.util.CpfMaskTextWatcher;

public class RecepcionistaActivity extends AppCompatActivity {

    private static final String TAG = "RecepcionistaActivity";
    private PacienteViewModel pacienteViewModel;
    private AttendanceEntryViewModel attendanceEntryViewModel;
    private SpecialtyViewModel specialtyViewModel;

    private TextInputEditText etCpfCheckin;
    private TextInputLayout tilCpfCheckin;
    private TextView tvNomePacienteEncontrado;
    private Spinner spinnerEspecialidadesCheckin;
    private Button btnGerarSenhaCheckin;
    private TextView tvSenhaGerada, tvPosicaoAtualSenha;
    private RecyclerView recyclerViewFilaAtualRecepcao;
    private FilaRecepcaoAdapter filaRecepcaoAdapter;

    private PacienteDto pacienteEncontrado = null;
    private SpecialtyDto especialidadeSelecionada = null;
    private List<SpecialtyDto> listaEspecialidadesCache = new ArrayList<>();
    private List<PacienteDto> listaPacientesCache = new ArrayList<>();
    private Map<Long, String> mapNomesPacientes = new HashMap<>();
    private Map<Long, String> mapNomesEspecialidades = new HashMap<>();

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshFilaRunnable;
    private final long REFRESH_FILA_INTERVAL = 15000;
    private String nomeFuncionarioLogado = "Recepcionista";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepcionista);

        Toolbar toolbar = findViewById(R.id.toolbarRecepcionista);
        SharedPreferences prefs = getSharedPreferences(MyFirebaseService.SHARED_PREFS_NAME, MODE_PRIVATE);
        nomeFuncionarioLogado = prefs.getString("USER_NAME", "Recepcionista");
        toolbar.setTitle("Olá, " + nomeFuncionarioLogado);
        setSupportActionBar(toolbar);

        pacienteViewModel       = new ViewModelProvider(this).get(PacienteViewModel.class);
        attendanceEntryViewModel = new ViewModelProvider(this).get(AttendanceEntryViewModel.class);
        specialtyViewModel      = new ViewModelProvider(this).get(SpecialtyViewModel.class);

        bindViews();
        setupListeners();
        setupAdapters();
        observarViewModels();
        carregarDadosIniciais();
    }

    private void bindViews() {
        etCpfCheckin               = findViewById(R.id.etCpfCheckin);
        tilCpfCheckin              = findViewById(R.id.tilCpfCheckin);
        tvNomePacienteEncontrado   = findViewById(R.id.tvNomePacienteEncontrado);
        spinnerEspecialidadesCheckin = findViewById(R.id.spinnerEspecialidadesCheckin);
        btnGerarSenhaCheckin       = findViewById(R.id.btnGerarSenhaCheckin);
        tvSenhaGerada              = findViewById(R.id.tvSenhaGerada);
        tvPosicaoAtualSenha        = findViewById(R.id.tvPosicaoAtualSenha);
        recyclerViewFilaAtualRecepcao = findViewById(R.id.recyclerViewFilaAtualRecepcao);
    }

    private void setupListeners() {
        etCpfCheckin.addTextChangedListener(new CpfMaskTextWatcher(etCpfCheckin));
        tilCpfCheckin.setEndIconOnClickListener(v -> buscarPacientePorCpfLocal());
        etCpfCheckin.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pacienteEncontrado != null) {
                    pacienteEncontrado = null;
                    tvNomePacienteEncontrado.setText("Paciente: Buscando...");
                    btnGerarSenhaCheckin.setEnabled(false);
                }
            }
            @Override public void afterTextChanged(Editable s) {
                if (s.toString().replaceAll("[^0-9]", "").length() == 11) {
                    buscarPacientePorCpfLocal();
                }
            }
        });
        btnGerarSenhaCheckin.setOnClickListener(v -> gerarSenhaEEntrarNaFila());
    }

    private void setupAdapters() {
        recyclerViewFilaAtualRecepcao.setLayoutManager(new LinearLayoutManager(this));
        filaRecepcaoAdapter = new FilaRecepcaoAdapter();
        recyclerViewFilaAtualRecepcao.setAdapter(filaRecepcaoAdapter);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecialidadesCheckin.setAdapter(spinnerAdapter);

        spinnerEspecialidadesCheckin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos > 0 && pos <= listaEspecialidadesCache.size()) {
                    especialidadeSelecionada = listaEspecialidadesCache.get(pos - 1);
                } else {
                    especialidadeSelecionada = null;
                }
                atualizarEstadoBotaoGerarSenha();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                especialidadeSelecionada = null;
                atualizarEstadoBotaoGerarSenha();
            }
        });
    }

    private void observarViewModels() {
        pacienteViewModel.listAllPacientes()
                .observe(this, pacientes -> {
                    if (pacientes != null) {
                        listaPacientesCache.clear();
                        listaPacientesCache.addAll(pacientes);
                        mapNomesPacientes.clear();
                        for (PacienteDto p : pacientes) {
                            mapNomesPacientes.put(p.getId(), p.getFullName());
                        }
                        carregarFilaAtual();
                    }
                });

        specialtyViewModel.listAllSpecialties()
                .observe(this, specialties -> {
                    if (specialties != null) {
                        listaEspecialidadesCache.clear();
                        listaEspecialidadesCache.addAll(specialties);
                        mapNomesEspecialidades.clear();
                        List<String> nomes = new ArrayList<>();
                        nomes.add("Selecione a Especialidade...");
                        for (SpecialtyDto s : specialties) {
                            nomes.add(s.getName());
                            mapNomesEspecialidades.put(s.getId(), s.getName());
                        }
                        ArrayAdapter<String> adapter =
                                (ArrayAdapter<String>) spinnerEspecialidadesCheckin.getAdapter();
                        adapter.clear();
                        adapter.addAll(nomes);
                        adapter.notifyDataSetChanged();
                        carregarFilaAtual();
                    } else {
                        Toast.makeText(this, "Erro ao carregar especialidades.", Toast.LENGTH_SHORT).show();
                    }
                });

        attendanceEntryViewModel.listAllAttendanceEntries()
                .observe(this, entries -> {
                    if (entries != null) {
                        List<AttendanceEntryDto> filaOrdenada = entries.stream()
                                .filter(e -> "AGUARDANDO".equalsIgnoreCase(e.getStatus())
                                        || "CHAMADO"   .equalsIgnoreCase(e.getStatus()))
                                .sorted(Comparator.comparing(
                                        AttendanceEntryDto::getCheckInTime,
                                        Comparator.nullsLast(Comparator.naturalOrder())
                                ))
                                .collect(Collectors.toList());
                        filaRecepcaoAdapter.setEntries(filaOrdenada, mapNomesPacientes, mapNomesEspecialidades);
                        atualizarPosicaoSenhaGerada(filaOrdenada);
                    } else {
                        filaRecepcaoAdapter.setEntries(new ArrayList<>(), mapNomesPacientes, mapNomesEspecialidades);
                        Toast.makeText(this, "Erro ao carregar fila atual.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void carregarDadosIniciais() {
        pacienteViewModel.listAllPacientes();
        specialtyViewModel.listAllSpecialties();
    }

    private void buscarPacientePorCpfLocal() {
        String cpf = etCpfCheckin.getText().toString().replaceAll("[^0-9]", "");
        if (cpf.length() != 11) {
            tvNomePacienteEncontrado.setText("Paciente: (Digite o CPF completo)");
            pacienteEncontrado = null;
            atualizarEstadoBotaoGerarSenha();
            return;
        }
        pacienteEncontrado = null;
        for (PacienteDto p : listaPacientesCache) {
            if (cpf.equals(p.getCpf())) {
                pacienteEncontrado = p;
                tvNomePacienteEncontrado.setText("Paciente: " + p.getFullName());
                break;
            }
        }
        if (pacienteEncontrado == null) {
            tvNomePacienteEncontrado.setText("Paciente: Não encontrado");
        }
        atualizarEstadoBotaoGerarSenha();
    }

    private void atualizarEstadoBotaoGerarSenha() {
        btnGerarSenhaCheckin.setEnabled(pacienteEncontrado != null && especialidadeSelecionada != null);
    }

    private void gerarSenhaEEntrarNaFila() {
        if (pacienteEncontrado == null || especialidadeSelecionada == null) {
            Toast.makeText(this, "Selecione um paciente e uma especialidade.", Toast.LENGTH_SHORT).show();
            return;
        }
        AttendanceEntryDto novaEntrada = new AttendanceEntryDto();
        novaEntrada.setPacienteId(pacienteEncontrado.getId());
        novaEntrada.setSpecialtyId(especialidadeSelecionada.getId());
        novaEntrada.setStatus("AGUARDANDO");
        attendanceEntryViewModel.createAttendanceEntry(novaEntrada).observe(this, entryDto -> {
            if (entryDto != null && entryDto.getId() != null) {
                Toast.makeText(RecepcionistaActivity.this, "Senha gerada e paciente na fila!", Toast.LENGTH_LONG).show();
                tvSenhaGerada.setText("E" + entryDto.getId());
                // Remove o observer para não gerar senhas duplicadas
                attendanceEntryViewModel.createAttendanceEntry(novaEntrada).removeObservers(this);
                carregarFilaAtual();
                limparCamposCheckin();
            } else {
                Toast.makeText(RecepcionistaActivity.this, "Erro ao gerar senha.", Toast.LENGTH_SHORT).show();
                tvSenhaGerada.setText("Erro");
                tvPosicaoAtualSenha.setText("-");
            }
        });
    }

    private void limparCamposCheckin() {
        etCpfCheckin.setText("");
        tvNomePacienteEncontrado.setText("Paciente: ");
        spinnerEspecialidadesCheckin.setSelection(0);
        pacienteEncontrado      = null;
        especialidadeSelecionada = null;
        btnGerarSenhaCheckin.setEnabled(false);
    }

    private void atualizarPosicaoSenhaGerada(List<AttendanceEntryDto> fila) {
        String txt = tvSenhaGerada.getText().toString();
        if (txt.startsWith("E")) {
            try {
                long idGerado = Long.parseLong(txt.substring(1));
                int pos = 0;
                for (AttendanceEntryDto e : fila) {
                    pos++;
                    if (e.getId().equals(idGerado)) {
                        tvPosicaoAtualSenha.setText(pos + "º");
                        return;
                    }
                }
                tvPosicaoAtualSenha.setText("-");
            } catch (NumberFormatException ex) {
                tvPosicaoAtualSenha.setText("-");
            }
        } else {
            tvPosicaoAtualSenha.setText("-");
        }
    }

    private void carregarFilaAtual() {
        Log.d(TAG, "Carregando fila atual...");
        attendanceEntryViewModel.listAllAttendanceEntries();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recepcionista_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout_recepcionista) {
            SharedPreferences prefs = getSharedPreferences(MyFirebaseService.SHARED_PREFS_NAME, MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent it = new Intent(this, MainActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(it);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDadosIniciais();
        refreshFilaRunnable = () -> {
            carregarFilaAtual();
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

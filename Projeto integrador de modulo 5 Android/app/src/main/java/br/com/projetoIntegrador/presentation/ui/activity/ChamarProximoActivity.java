package br.com.projetoIntegrador.presentation.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.Map;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.AttendanceEntryDto;
import br.com.projetoIntegrador.network.NotificationRequest;
import br.com.projetoIntegrador.presentation.viewmodel.AttendanceEntryViewModel;
import br.com.projetoIntegrador.presentation.viewmodel.NotificationViewModel;
import br.com.projetoIntegrador.presentation.viewmodel.PacienteViewModel;

public class ChamarProximoActivity extends AppCompatActivity {

    private static final String TAG = "ChamarProximoActivity";
    private TextView tvNomePacienteChamar, tvSenhaPacienteChamar, tvFeedbackChamada;
    private Button btnChamarPacienteConfirmar;

    private NotificationViewModel notificationViewModel;
    private PacienteViewModel pacienteViewModel;
    private AttendanceEntryViewModel attendanceEntryViewModel;

    private Long attendanceEntryId;
    private Long pacienteIdParaNotificar;
    private String nomePacienteParaExibir = "Paciente";
    private String senhaParaExibir = "-";
    private AttendanceEntryDto currentAttendanceEntry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chamar_proximo);

        Toolbar toolbar = findViewById(R.id.toolbarChamarProximo);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        pacienteViewModel = new ViewModelProvider(this).get(PacienteViewModel.class);
        attendanceEntryViewModel = new ViewModelProvider(this).get(AttendanceEntryViewModel.class);

        tvNomePacienteChamar = findViewById(R.id.tvNomePacienteChamar);
        tvSenhaPacienteChamar = findViewById(R.id.tvSenhaPacienteChamar);
        tvFeedbackChamada = findViewById(R.id.tvFeedbackChamada);
        btnChamarPacienteConfirmar = findViewById(R.id.btnChamarPacienteConfirmar);

        if (getIntent().getExtras() != null) {
            attendanceEntryId = getIntent().getLongExtra("ATTENDANCE_ENTRY_ID", -1L);
            pacienteIdParaNotificar = getIntent().getLongExtra("PACIENTE_ID", -1L);
            Log.d(TAG, "onCreate: Recebido attendanceEntryId: " + attendanceEntryId + ", pacienteIdParaNotificar: " + pacienteIdParaNotificar);
        }

        if (attendanceEntryId == -1L || pacienteIdParaNotificar == -1L) {
            Toast.makeText(this, "Erro: Dados do paciente inválidos para chamada.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "ID da entrada ou do paciente não fornecido via Intent. Encerrando Activity.");
            finish();
            return;
        }

        carregarDetalhesPacienteEEntrada();
        setupListeners();
        observarViewModels();
    }

    private void setupListeners() {
        btnChamarPacienteConfirmar.setOnClickListener(v -> {
            if (pacienteIdParaNotificar != null && pacienteIdParaNotificar > 0 && currentAttendanceEntry != null) {
                chamarPaciente();
            } else {
                Log.w(TAG, "setupListeners: Tentativa de chamar paciente com dados inválidos.");
                Toast.makeText(this, "Dados do paciente ou da entrada inválidos para notificação.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarDetalhesPacienteEEntrada() {
        btnChamarPacienteConfirmar.setEnabled(false);
        tvNomePacienteChamar.setText("Carregando nome...");
        tvSenhaPacienteChamar.setText("Senha: ...");
        Log.d(TAG, "carregarDetalhesPacienteEEntrada: Iniciando carregamento de detalhes.");

        pacienteViewModel.getPacienteById(pacienteIdParaNotificar).observe(this, pacienteDto -> {
            if (pacienteDto != null) {
                nomePacienteParaExibir = pacienteDto.getFullName();
                tvNomePacienteChamar.setText(nomePacienteParaExibir);
                Log.d(TAG, "carregarDetalhesPacienteEEntrada: Nome do paciente carregado: " + nomePacienteParaExibir);
            } else {
                nomePacienteParaExibir = "Paciente (ID: " + pacienteIdParaNotificar + ")";
                tvNomePacienteChamar.setText(nomePacienteParaExibir);
                Log.w(TAG, "carregarDetalhesPacienteEEntrada: Nome do paciente não encontrado para ID: " + pacienteIdParaNotificar + ", usando ID.");
            }
            checkIfDataReady();
        });

        attendanceEntryViewModel.getAttendanceEntryById(attendanceEntryId).observe(this, entryDto -> {
            if (entryDto != null) {
                currentAttendanceEntry = entryDto;
                senhaParaExibir = "E" + entryDto.getId();
                tvSenhaPacienteChamar.setText("Senha: " + senhaParaExibir);
                Log.d(TAG, "carregarDetalhesPacienteEEntrada: Detalhes da entrada de atendimento carregados. Status: " + entryDto.getStatus());


                if (!"AGUARDANDO".equalsIgnoreCase(entryDto.getStatus()) && !"CONFIRMADO".equalsIgnoreCase(entryDto.getStatus())) {
                    btnChamarPacienteConfirmar.setText("Paciente já " + entryDto.getStatus().toLowerCase());
                    btnChamarPacienteConfirmar.setEnabled(false);
                    tvFeedbackChamada.setText("Este paciente não está mais aguardando chamada.");
                    tvFeedbackChamada.setVisibility(View.VISIBLE);
                    Log.d(TAG, "carregarDetalhesPacienteEEntrada: Paciente já foi atendido/não compareceu. Desabilitando botão.");
                }
            } else {
                Toast.makeText(this, "Erro ao carregar detalhes da entrada.", Toast.LENGTH_SHORT).show();
                tvSenhaPacienteChamar.setText("Senha: Erro");
                Log.e(TAG, "carregarDetalhesPacienteEEntrada: Erro ao carregar detalhes da entrada para ID: " + attendanceEntryId);
            }
            checkIfDataReady();
        });
    }

    private void checkIfDataReady() {
        if (currentAttendanceEntry != null && nomePacienteParaExibir != null &&
                ("AGUARDANDO".equalsIgnoreCase(currentAttendanceEntry.getStatus()) ||
                        "CONFIRMADO".equalsIgnoreCase(currentAttendanceEntry.getStatus())) ) {
            btnChamarPacienteConfirmar.setEnabled(true);
            Log.d(TAG, "checkIfDataReady: Dados prontos, botão de chamada habilitado.");
        } else {
            Log.d(TAG, "checkIfDataReady: Dados ainda não prontos ou paciente em status incompatível. currentEntry: " + (currentAttendanceEntry != null ? currentAttendanceEntry.getStatus() : "null"));
        }
    }


    private void chamarPaciente() {
        String titulo = "Sua Vez de Ser Atendido!";
        String corpo = "Olá, " + nomePacienteParaExibir +
                "! Dirija-se à sala de atendimento. Senha: " + senhaParaExibir;

        Map<String, String> dataPayload = new HashMap<>();
        dataPayload.put("action", "CHAMADA_PACIENTE");
        dataPayload.put("attendanceEntryId", String.valueOf(attendanceEntryId));
        dataPayload.put("message", corpo); // Pode ser útil para o app do paciente
        dataPayload.put("title", titulo); // Adicionado para garantir que o título vá no payload de dados
        dataPayload.put("body", corpo); // Adicionado para garantir que o corpo vá no payload de dados


        NotificationRequest request = new NotificationRequest(
                String.valueOf(pacienteIdParaNotificar),
                titulo, // Este título e corpo podem ser usados para a notificação de sistema padrão
                corpo,
                dataPayload
        );

        btnChamarPacienteConfirmar.setEnabled(false);
        tvFeedbackChamada.setText("Enviando notificação...");
        tvFeedbackChamada.setVisibility(View.VISIBLE);
        Log.d(TAG, "chamarPaciente: Enviando notificação para paciente ID: " + pacienteIdParaNotificar + " com título: " + titulo + ", corpo: " + corpo + ", data: " + dataPayload);

        notificationViewModel.sendNotification(request);
    }

    private void observarViewModels(){
        if (notificationViewModel.sendNotification(null) == null) {
            Log.e(TAG, "observarViewModels: LiveData de notificação é nulo ao tentar observar.");
            return;
        }

        notificationViewModel.sendNotification(null)
                .observe(this, resultado -> {
                    if (!btnChamarPacienteConfirmar.isEnabled() && resultado != null) {
                        Log.d(TAG, "Resultado do envio da notificação: " + resultado);
                        if (resultado.toLowerCase().contains("erro") || resultado.toLowerCase().contains("falha")) {
                            tvFeedbackChamada.setText("Falha ao enviar notificação: " + resultado);
                            Toast.makeText(ChamarProximoActivity.this, "Falha ao enviar notificação.", Toast.LENGTH_LONG).show();
                            btnChamarPacienteConfirmar.setEnabled(true);
                        } else {
                            tvFeedbackChamada.setText("Notificação enviada para o dispositivo do paciente.");
                            Toast.makeText(ChamarProximoActivity.this, "Notificação enviada!", Toast.LENGTH_SHORT).show();
                            atualizarStatusEntradaParaChamado();
                        }
                    }
                });
    }

    private void atualizarStatusEntradaParaChamado() {
        if (currentAttendanceEntry == null) {
            Log.e(TAG, "currentAttendanceEntry é nulo, não pode atualizar status.");
            Toast.makeText(this, "Erro interno ao atualizar status.", Toast.LENGTH_SHORT).show();
            btnChamarPacienteConfirmar.setEnabled(true);
            return;
        }

        Log.d(TAG, "atualizarStatusEntradaParaChamado: Tentando atualizar status da entrada " + attendanceEntryId + " para CHAMADO.");

        AttendanceEntryDto dtoParaAtualizar = new AttendanceEntryDto();
        dtoParaAtualizar.setId(currentAttendanceEntry.getId());
        dtoParaAtualizar.setPacienteId(currentAttendanceEntry.getPacienteId());
        dtoParaAtualizar.setSpecialtyId(currentAttendanceEntry.getSpecialtyId());
        dtoParaAtualizar.setStatus("CHAMADO");

        attendanceEntryViewModel.updateAttendanceEntry(attendanceEntryId, dtoParaAtualizar).observe(this, updatedEntry -> {
            if (updatedEntry != null && "CHAMADO".equalsIgnoreCase(updatedEntry.getStatus())) {
                Log.i(TAG, "Status da entrada " + attendanceEntryId + " atualizado para CHAMADO com sucesso.");
                Toast.makeText(this, "Paciente chamado. Status atualizado.", Toast.LENGTH_LONG).show();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    setResult(RESULT_OK);
                    finish();
                }, 2500);
            } else {
                Log.e(TAG, "Falha ao atualizar status da entrada para CHAMADO. Resposta da API: " + updatedEntry + ", ou status inesperado.");
                Toast.makeText(this, "Falha ao atualizar status do paciente.", Toast.LENGTH_SHORT).show();
                btnChamarPacienteConfirmar.setEnabled(true);
                tvFeedbackChamada.setText("Falha ao atualizar status. Tente chamar novamente.");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "Botão Voltar pressionado.");
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
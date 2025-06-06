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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.AttendanceEntryDto;
import br.com.projetoIntegrador.network.NotificationRequest;
import br.com.projetoIntegrador.network.PacienteDto;
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
    private String nomePacienteParaExibir = "Paciente"; // Valor padrão
    private String senhaParaExibir = "-"; // Valor padrão
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
        }

        if (attendanceEntryId == -1L || pacienteIdParaNotificar == -1L) {
            Toast.makeText(this, "Erro: Dados do paciente inválidos.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "ID da entrada ou do paciente não fornecido via Intent.");
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
                Toast.makeText(this, "Dados do paciente ou da entrada inválidos para notificação.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarDetalhesPacienteEEntrada() {
        btnChamarPacienteConfirmar.setEnabled(false); // Desabilita enquanto carrega
        tvNomePacienteChamar.setText("Carregando nome...");
        tvSenhaPacienteChamar.setText("Senha: ...");

        pacienteViewModel.getPacienteById(pacienteIdParaNotificar).observe(this, pacienteDto -> {
            if (pacienteDto != null) {
                nomePacienteParaExibir = pacienteDto.getFullName();
                tvNomePacienteChamar.setText(nomePacienteParaExibir);
            } else {
                nomePacienteParaExibir = "Paciente (ID: " + pacienteIdParaNotificar + ")";
                tvNomePacienteChamar.setText(nomePacienteParaExibir);
                Log.w(TAG, "Nome do paciente não encontrado, usando ID.");
            }
            checkIfDataReady();
        });

        attendanceEntryViewModel.getAttendanceEntryById(attendanceEntryId).observe(this, entryDto -> {
            if (entryDto != null) {
                currentAttendanceEntry = entryDto;
                senhaParaExibir = "E" + entryDto.getId(); // Usando o ID da entrada como "senha" visual
                tvSenhaPacienteChamar.setText("Senha: " + senhaParaExibir);

                // Verifica se o paciente já foi chamado ou atendido
                if (!"AGUARDANDO".equalsIgnoreCase(entryDto.getStatus()) && !"CONFIRMADO".equalsIgnoreCase(entryDto.getStatus())) {
                    btnChamarPacienteConfirmar.setText("Paciente já " + entryDto.getStatus().toLowerCase());
                    btnChamarPacienteConfirmar.setEnabled(false);
                    tvFeedbackChamada.setText("Este paciente não está mais aguardando chamada.");
                    tvFeedbackChamada.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(this, "Erro ao carregar detalhes da entrada.", Toast.LENGTH_SHORT).show();
                tvSenhaPacienteChamar.setText("Senha: Erro");
            }
            checkIfDataReady();
        });
    }

    private void checkIfDataReady() {
        // Habilita o botão apenas se todos os dados necessários foram carregados
        // e o paciente ainda está em um estado que permite chamada.
        if (currentAttendanceEntry != null && nomePacienteParaExibir != null &&
                ("AGUARDANDO".equalsIgnoreCase(currentAttendanceEntry.getStatus()) ||
                        "CONFIRMADO".equalsIgnoreCase(currentAttendanceEntry.getStatus())) ) {
            btnChamarPacienteConfirmar.setEnabled(true);
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

        NotificationRequest request = new NotificationRequest(
                String.valueOf(pacienteIdParaNotificar),
                titulo,
                corpo,
                dataPayload
        );

        btnChamarPacienteConfirmar.setEnabled(false);
        tvFeedbackChamada.setText("Enviando notificação...");
        tvFeedbackChamada.setVisibility(View.VISIBLE);

        notificationViewModel.sendNotification(request); // O observer tratará da resposta
    }

    private void observarViewModels(){
        // Observador para o resultado do envio da notificação
        notificationViewModel.sendNotification(null) // Chamada inicial para obter o LiveData
                .observe(this, resultado -> {
                    // Este LiveData é um pouco genérico, pode ser acionado por outras chamadas se o ViewModel for reutilizado.
                    // Idealmente, ter um LiveData específico por operação ou usar um wrapper com ID da operação.
                    // Por enquanto, assumimos que este é o resultado da nossa chamada.

                    // Verificamos se o botão está desabilitado (sinal de que uma chamada foi feita)
                    if (!btnChamarPacienteConfirmar.isEnabled() && resultado != null) {
                        Log.d(TAG, "Resultado do envio da notificação: " + resultado);
                        if (resultado.toLowerCase().contains("erro") || resultado.toLowerCase().contains("falha")) {
                            tvFeedbackChamada.setText("Falha ao enviar notificação: " + resultado);
                            Toast.makeText(ChamarProximoActivity.this, "Falha ao enviar notificação.", Toast.LENGTH_LONG).show();
                            btnChamarPacienteConfirmar.setEnabled(true); // Permite tentar novamente
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
            btnChamarPacienteConfirmar.setEnabled(true); // Permite tentar de novo se algo deu errado
            return;
        }

        // Cria um novo DTO para atualização para não modificar o DTO original observado,
        // ou garante que o currentAttendanceEntry seja uma cópia se for ser modificado.
        AttendanceEntryDto dtoParaAtualizar = new AttendanceEntryDto();
        // Copia os IDs e outros campos que não mudam, se necessário pela API de update.
        // A API de update do backend (AttendanceEntryService.update) só atualiza o status.
        // Mas é mais seguro enviar o ID.
        dtoParaAtualizar.setId(currentAttendanceEntry.getId());
        dtoParaAtualizar.setPacienteId(currentAttendanceEntry.getPacienteId());
        dtoParaAtualizar.setSpecialtyId(currentAttendanceEntry.getSpecialtyId());
        // Outros campos do currentAttendanceEntry se a API de update precisar deles...

        dtoParaAtualizar.setStatus("CHAMADO"); // Novo status
        // O backend deve setar o callTime ao receber esta atualização.

        attendanceEntryViewModel.updateAttendanceEntry(attendanceEntryId, dtoParaAtualizar).observe(this, updatedEntry -> {
            if (updatedEntry != null && "CHAMADO".equalsIgnoreCase(updatedEntry.getStatus())) {
                Log.i(TAG, "Status da entrada " + attendanceEntryId + " atualizado para CHAMADO.");
                Toast.makeText(this, "Paciente chamado. Status atualizado.", Toast.LENGTH_LONG).show();
                // Finaliza a activity após um delay para o usuário ver o feedback.
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    setResult(RESULT_OK); // Informa à activity anterior que a chamada foi bem-sucedida
                    finish();
                }, 2500);
            } else {
                Log.e(TAG, "Falha ao atualizar status da entrada para CHAMADO. Resposta da API: " + updatedEntry);
                Toast.makeText(this, "Falha ao atualizar status do paciente.", Toast.LENGTH_SHORT).show();
                btnChamarPacienteConfirmar.setEnabled(true); // Permite tentar de novo
                tvFeedbackChamada.setText("Falha ao atualizar status. Tente chamar novamente.");
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Volta para a tela anterior (ProfissionalSaudeActivity)
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
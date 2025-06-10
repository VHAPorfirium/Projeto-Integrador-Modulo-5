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
        btnChamarPacienteConfirmar.setEnabled(false); // Desabilita enquanto carrega
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
                senhaParaExibir = "E" + entryDto.getId(); // Usando o ID da entrada como "senha" visual
                tvSenhaPacienteChamar.setText("Senha: " + senhaParaExibir);
                Log.d(TAG, "carregarDetalhesPacienteEEntrada: Detalhes da entrada de atendimento carregados. Status: " + entryDto.getStatus());


                // Verifica se o paciente já foi chamado ou atendido
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
        // Habilita o botão apenas se todos os dados necessários foram carregados
        // e o paciente ainda está em um estado que permite chamada.
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

        NotificationRequest request = new NotificationRequest(
                String.valueOf(pacienteIdParaNotificar),
                titulo,
                corpo,
                dataPayload
        );

        btnChamarPacienteConfirmar.setEnabled(false);
        tvFeedbackChamada.setText("Enviando notificação...");
        tvFeedbackChamada.setVisibility(View.VISIBLE);
        Log.d(TAG, "chamarPaciente: Enviando notificação para paciente ID: " + pacienteIdParaNotificar);

        notificationViewModel.sendNotification(request); // O observer tratará da resposta
    }

    private void observarViewModels(){
        // Observador para o resultado do envio da notificação
        // Garante que o LiveData não seja nulo para observação contínua
        if (notificationViewModel.sendNotification(null) == null) {
            // Este caso só ocorreria se o ViewModel não estivesse configurado corretamente
            // ou se a primeira chamada sendNotification(null) retornasse null, o que é improvável.
            Log.e(TAG, "observarViewModels: LiveData de notificação é nulo ao tentar observar.");
            return;
        }

        notificationViewModel.sendNotification(null)
                .observe(this, resultado -> {
                    // Este LiveData é um pouco genérico, pode ser acionado por outras chamadas se o ViewModel for reutilizado.
                    // Idealmente, ter um LiveData específico por operação ou usar um wrapper com ID da operação.
                    // Por enquanto, assumimos que este é o resultado da nossa chamada.

                    // Verificamos se o botão está desabilitado (sinal de que uma chamada foi feita)
                    // e se o resultado não é nulo (evitar chamadas de inicialização do LiveData)
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

        Log.d(TAG, "atualizarStatusEntradaParaChamado: Tentando atualizar status da entrada " + attendanceEntryId + " para CHAMADO.");

        // Cria um novo DTO para atualização para não modificar o DTO original observado,
        // ou garante que o currentAttendanceEntry seja uma cópia se for ser modificado.
        AttendanceEntryDto dtoParaAtualizar = new AttendanceEntryDto();
        dtoParaAtualizar.setId(currentAttendanceEntry.getId());
        dtoParaAtualizar.setPacienteId(currentAttendanceEntry.getPacienteId());
        dtoParaAtualizar.setSpecialtyId(currentAttendanceEntry.getSpecialtyId());
        // IMPORTANTE: Se o seu backend espera o DTO completo, certifique-se de copiar todos os campos.
        // Se a API de update do backend só usa o ID do path e o status do body, isso é suficiente.
        // A sua API de update atualiza o status.
        // Se houver campos que não podem ser nulos no DTO do corpo da requisição de PUT,
        // você precisará preenchê-los com os valores originais de currentAttendanceEntry
        // antes de setar o status e enviar.

        dtoParaAtualizar.setStatus("CHAMADO"); // Novo status
        // O backend deve setar o callTime ao receber esta atualização.

        attendanceEntryViewModel.updateAttendanceEntry(attendanceEntryId, dtoParaAtualizar).observe(this, updatedEntry -> {
            if (updatedEntry != null && "CHAMADO".equalsIgnoreCase(updatedEntry.getStatus())) {
                Log.i(TAG, "Status da entrada " + attendanceEntryId + " atualizado para CHAMADO com sucesso.");
                Toast.makeText(this, "Paciente chamado. Status atualizado.", Toast.LENGTH_LONG).show();
                // Finaliza a activity após um delay para o usuário ver o feedback.
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    setResult(RESULT_OK); // Informa à activity anterior que a chamada foi bem-sucedida
                    finish();
                }, 2500);
            } else {
                Log.e(TAG, "Falha ao atualizar status da entrada para CHAMADO. Resposta da API: " + updatedEntry + ", ou status inesperado.");
                Toast.makeText(this, "Falha ao atualizar status do paciente.", Toast.LENGTH_SHORT).show();
                btnChamarPacienteConfirmar.setEnabled(true); // Permite tentar de novo
                tvFeedbackChamada.setText("Falha ao atualizar status. Tente chamar novamente.");
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "Botão Voltar pressionado.");
            finish(); // Volta para a tela anterior (ProfissionalSaudeActivity)
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
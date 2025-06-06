package br.com.projetoIntegrador.presentation.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment; // Pode ser um DialogFragment
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.AttendanceEntryDto;
import br.com.projetoIntegrador.presentation.viewmodel.AttendanceEntryViewModel;

public class ConfirmacaoFragment extends Fragment { // Ou estenda DialogFragment

    private static final String TAG = "ConfirmacaoFragment";
    private static final String ARG_ENTRY_ID = "entryId";
    private Long currentAttendanceEntryId; // ID da entrada de atendimento a ser confirmada

    private AttendanceEntryViewModel attendanceEntryViewModel;
    private TextView tvMensagemConfirmacao;
    private TextView tvTempoRestanteConfirmacao;
    private Button btnSimEstouACaminho;
    private Button btnNaoConsigoRetornar;

    private CountDownTimer countDownTimer;
    private final long TIMEOUT_CONFIRMACAO_MS = 60000; // 60 segundos

    // Construtor público vazio é recomendado
    public ConfirmacaoFragment() {}

    // Método factory para passar o ID da entrada de atendimento
    public static ConfirmacaoFragment newInstance(Long entryId) {
        ConfirmacaoFragment fragment = new ConfirmacaoFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ENTRY_ID, entryId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentAttendanceEntryId = getArguments().getLong(ARG_ENTRY_ID);
        }
        attendanceEntryViewModel = new ViewModelProvider(this).get(AttendanceEntryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirmacao, container, false);

        tvMensagemConfirmacao = view.findViewById(R.id.tvMensagemConfirmacao);
        tvTempoRestanteConfirmacao = view.findViewById(R.id.tvTempoRestanteConfirmacao);
        btnSimEstouACaminho = view.findViewById(R.id.btnSimEstouACaminho);
        btnNaoConsigoRetornar = view.findViewById(R.id.btnNaoConsigoRetornar);

        if (currentAttendanceEntryId == null || currentAttendanceEntryId <= 0) {
            Log.e(TAG, "ID da entrada de atendimento inválido.");
            Toast.makeText(getContext(), "Erro: ID de atendimento inválido.", Toast.LENGTH_LONG).show();
            // Fechar o fragment ou tratar o erro
            // Se for DialogFragment: dismiss();
            // Se for Fragment normal: getParentFragmentManager().popBackStack();
            return view; // Ou retornar uma view de erro
        }


        btnSimEstouACaminho.setOnClickListener(v -> {
            confirmarPresenca();
        });

        btnNaoConsigoRetornar.setOnClickListener(v -> {
            indicarNaoComparecimento();
        });

        iniciarTimeout();
        return view;
    }

    private void confirmarPresenca() {
        if (currentAttendanceEntryId == null) return;
        stopTimeout();
        // Chamar API: POST /entradasAtendimento/{id}/confirmar (endpoint hipotético)
        // O frontend.pdf não especifica o payload, apenas o endpoint.
        // O AttendanceEntryService tem um método update, que pode ser adaptado
        // ou um novo método no ViewModel/Repository para essa ação específica.
        Log.d(TAG, "Confirmando presença para entryId: " + currentAttendanceEntryId);
        attendanceEntryViewModel.confirmAttendance(currentAttendanceEntryId).observe(getViewLifecycleOwner(), new Observer<AttendanceEntryDto>() {
            @Override
            public void onChanged(AttendanceEntryDto attendanceEntryDto) {
                if (attendanceEntryDto != null) {
                    Toast.makeText(getContext(), "Presença confirmada! Dirija-se ao atendimento.", Toast.LENGTH_LONG).show();
                    // Fechar o fragment/dialog
                    // Se DialogFragment: dismiss();
                    // Se Fragment: getParentFragmentManager().popBackStack(); ou navegar para outra tela.
                } else {
                    Toast.makeText(getContext(), "Erro ao confirmar presença.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void indicarNaoComparecimento() {
        if (currentAttendanceEntryId == null) return;
        stopTimeout();
        // Chamar API: POST /entradasAtendimento/{id}/naoCompareceu (endpoint hipotético)
        Log.d(TAG, "Indicando não comparecimento para entryId: " + currentAttendanceEntryId);
        attendanceEntryViewModel.markAsNoShow(currentAttendanceEntryId).observe(getViewLifecycleOwner(), new Observer<AttendanceEntryDto>() {
            @Override
            public void onChanged(AttendanceEntryDto attendanceEntryDto) {
                if (attendanceEntryDto != null) {
                    Toast.makeText(getContext(), "Não comparecimento registrado. Você foi retornado ao final da fila.", Toast.LENGTH_LONG).show();
                    // Notificar recepção (pode ser via backend)
                    // Fechar o fragment/dialog
                } else {
                    Toast.makeText(getContext(), "Erro ao registrar não comparecimento.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void iniciarTimeout() {
        tvTempoRestanteConfirmacao.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(TIMEOUT_CONFIRMACAO_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTempoRestanteConfirmacao.setText("Tempo restante: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                tvTempoRestanteConfirmacao.setText("Tempo esgotado!");
                if (isVisible() && getContext() != null) { // Verifica se o fragment ainda está ativo
                    Toast.makeText(getContext(), "Tempo esgotado. Responda rapidamente da próxima vez.", Toast.LENGTH_LONG).show();
                    // Lógica de timeout: volta ao fim da fila + notifica recepção (via backend)
                    indicarNaoComparecimento(); // Ou uma ação específica de timeout
                }
            }
        }.start();
    }

    private void stopTimeout() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        tvTempoRestanteConfirmacao.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimeout(); // Garante que o timer seja cancelado
    }
}
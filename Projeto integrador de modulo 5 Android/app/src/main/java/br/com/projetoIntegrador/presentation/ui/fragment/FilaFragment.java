package br.com.projetoIntegrador.presentation.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.AttendanceEntryDto;
import br.com.projetoIntegrador.presentation.viewmodel.AttendanceEntryViewModel;

public class FilaFragment extends Fragment {

    private static final String TAG = "FilaFragment";
    private static final String ARG_PACIENTE_ID = "pacienteId";

    private AttendanceEntryViewModel attendanceEntryViewModel;
    private TextView tvPosicaoFila;
    private TextView tvTempoEsperaEstimado;
    private Button btnRefreshFila; // Alterado de ImageButton para Button

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;
    private final long REFRESH_INTERVAL = 30000; // 30 segundos

    private Long pacienteIdLogado;

    public static FilaFragment newInstance(Long pacienteId) {
        FilaFragment fragment = new FilaFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PACIENTE_ID, pacienteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pacienteIdLogado = getArguments().getLong(ARG_PACIENTE_ID);
        }
        attendanceEntryViewModel = new ViewModelProvider(this).get(AttendanceEntryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla o novo layout do fragmento da fila
        View view = inflater.inflate(R.layout.fragment_fila, container, false);

        // Associa as views do novo layout
        tvPosicaoFila = view.findViewById(R.id.tvPosicaoFila);
        tvTempoEsperaEstimado = view.findViewById(R.id.tvTempoEsperaEstimado);
        btnRefreshFila = view.findViewById(R.id.btnRefreshFila);

        // Configura o listener do botão de atualizar
        btnRefreshFila.setOnClickListener(v -> carregarDadosFilaComFeedback());

        observarDadosFila();
        carregarDadosFila();

        return view;
    }

    private void carregarDadosFilaComFeedback(){
        Toast.makeText(getContext(), "Atualizando fila...", Toast.LENGTH_SHORT).show();
        carregarDadosFila();
    }

    private void observarDadosFila() {
        attendanceEntryViewModel.listAllAttendanceEntries().observe(getViewLifecycleOwner(), attendanceEntries -> {
            if (attendanceEntries != null) {
                processarFila(attendanceEntries);
            } else {
                tvPosicaoFila.setText("Erro ao carregar fila.");
                tvTempoEsperaEstimado.setText("Tempo estimado: -");
                Log.e(TAG, "Lista de entradas da fila nula.");
            }
        });
    }

    private void processarFila(List<AttendanceEntryDto> todasEntradas) {
        if (pacienteIdLogado == null) return;

        List<AttendanceEntryDto> filaRelevante = todasEntradas.stream()
                .filter(entry -> "AGUARDANDO".equalsIgnoreCase(entry.getStatus()) || "CONFIRMADO".equalsIgnoreCase(entry.getStatus()))
                .sorted(Comparator.comparing(AttendanceEntryDto::getCheckInTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        int minhaPosicaoNaListaGeral = -1;
        for (int i = 0; i < filaRelevante.size(); i++) {
            if (filaRelevante.get(i).getPacienteId().equals(pacienteIdLogado)) {
                minhaPosicaoNaListaGeral = i;
                break;
            }
        }

        if (minhaPosicaoNaListaGeral != -1) {
            long pessoasNaFrente = minhaPosicaoNaListaGeral;
            if (pessoasNaFrente == 0) {
                tvPosicaoFila.setText("Você é o próximo!");
            } else if (pessoasNaFrente == 1) {
                tvPosicaoFila.setText("1 paciente na sua frente");
            } else {
                tvPosicaoFila.setText(pessoasNaFrente + " pacientes na sua frente");
            }

            long tempoEstimadoMin = pessoasNaFrente * 5; // Ex: 5 min por pessoa
            tvTempoEsperaEstimado.setText("Espera estimada: " + tempoEstimadoMin + " minutos");

        } else {
            tvPosicaoFila.setText("Você não está na fila");
            tvTempoEsperaEstimado.setText("Check-in não realizado");
        }
    }


    private void carregarDadosFila() {
        Log.d(TAG, "Carregando dados da fila...");
        attendanceEntryViewModel.listAllAttendanceEntries();
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarDadosFila();
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                carregarDadosFila();
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRunnable);
    }
}
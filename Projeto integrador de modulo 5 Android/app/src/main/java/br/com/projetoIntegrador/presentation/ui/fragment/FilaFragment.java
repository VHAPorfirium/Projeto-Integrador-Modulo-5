package br.com.projetoIntegrador.presentation.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import br.com.projetoIntegrador.presentation.adapter.FilaDetalheAdapter;
import br.com.projetoIntegrador.presentation.viewmodel.AttendanceEntryViewModel;

public class FilaFragment extends Fragment {

    private static final String TAG = "FilaFragment";
    private static final String ARG_PACIENTE_ID = "pacienteId";

    private AttendanceEntryViewModel attendanceEntryViewModel;
    private TextView tvPosicaoFila;
    private TextView tvTempoEsperaEstimado;
    private ImageButton btnRefreshFila;
    private Button btnDetalharFila;
    private RecyclerView recyclerViewFilaCompleta;
    private FilaDetalheAdapter filaAdapter;

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
        View view = inflater.inflate(R.layout.fragment_fila, container, false);

        tvPosicaoFila = view.findViewById(R.id.tvPosicaoFila);
        tvTempoEsperaEstimado = view.findViewById(R.id.tvTempoEsperaEstimado);
        btnRefreshFila = view.findViewById(R.id.btnRefreshFila);
        btnDetalharFila = view.findViewById(R.id.btnDetalharFila);
        recyclerViewFilaCompleta = view.findViewById(R.id.recyclerViewFilaCompleta);

        recyclerViewFilaCompleta.setLayoutManager(new LinearLayoutManager(getContext()));
        filaAdapter = new FilaDetalheAdapter(new ArrayList<>(), pacienteIdLogado);
        recyclerViewFilaCompleta.setAdapter(filaAdapter);

        btnRefreshFila.setOnClickListener(v -> carregarDadosFilaComFeedback());
        btnDetalharFila.setOnClickListener(v -> toggleDetalhesFila());

        observarDadosFila();
        carregarDadosFila();

        return view;
    }

    private void toggleDetalhesFila() {
        if (recyclerViewFilaCompleta.getVisibility() == View.GONE) {
            recyclerViewFilaCompleta.setVisibility(View.VISIBLE);
            btnDetalharFila.setText("Ocultar Detalhes da Fila");
            // Os dados já devem ter sido carregados pelo carregarDadosFila()
        } else {
            recyclerViewFilaCompleta.setVisibility(View.GONE);
            btnDetalharFila.setText("Detalhar Fila Completa");
        }
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
                filaAdapter.setEntradas(new ArrayList<>());
                Log.e(TAG, "Lista de entradas da fila nula.");
            }
        });
    }

    private void processarFila(List<AttendanceEntryDto> todasEntradas) {
        if (pacienteIdLogado == null) return;

        // Filtra entradas relevantes (AGUARDANDO ou CONFIRMADO) e ordena por checkInTime
        List<AttendanceEntryDto> filaRelevante = todasEntradas.stream()
                .filter(entry -> "AGUARDANDO".equalsIgnoreCase(entry.getStatus()) || "CONFIRMADO".equalsIgnoreCase(entry.getStatus()))
                .sorted(Comparator.comparing(AttendanceEntryDto::getCheckInTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        int minhaPosicao = -1;
        Long minhaEspecialidadeId = null;

        for (int i = 0; i < filaRelevante.size(); i++) {
            if (filaRelevante.get(i).getPacienteId().equals(pacienteIdLogado)) {
                minhaPosicao = i + 1; // Posição base 1
                minhaEspecialidadeId = filaRelevante.get(i).getSpecialtyId();
                break;
            }
        }

        if (minhaPosicao != -1 && minhaEspecialidadeId != null) {
            tvPosicaoFila.setText("Você está em: " + minhaPosicao + "º");
            int finalMinhaPosicao = minhaPosicao;
            Long finalMinhaEspecialidadeId1 = minhaEspecialidadeId;
            long pessoasNaFrenteMesmaEspecialidade = filaRelevante.stream()
                    .filter(entry -> entry.getSpecialtyId().equals(finalMinhaEspecialidadeId1) &&
                            filaRelevante.indexOf(entry) < (finalMinhaPosicao -1) ) // Pessoas antes de mim na mesma especialidade
                    .count();

            long tempoEstimadoMin = pessoasNaFrenteMesmaEspecialidade * 5; // Ex: 5 min por pessoa
            tvTempoEsperaEstimado.setText("Tempo estimado: ~" + tempoEstimadoMin + " min");

            // Atualiza a lista detalhada apenas com a fila da minha especialidade
            Long finalMinhaEspecialidadeId = minhaEspecialidadeId;
            List<AttendanceEntryDto> filaDetalhada = filaRelevante.stream()
                    .filter(entry -> entry.getSpecialtyId().equals(finalMinhaEspecialidadeId))
                    .collect(Collectors.toList());
            filaAdapter.setEntradas(filaDetalhada);

        } else {
            tvPosicaoFila.setText("Você não está na fila ou já foi atendido.");
            tvTempoEsperaEstimado.setText("Tempo estimado: -");
            filaAdapter.setEntradas(new ArrayList<>()); // Limpa a lista detalhada
        }
    }


    private void carregarDadosFila() {
        Log.d(TAG, "Carregando dados da fila...");
        attendanceEntryViewModel.listAllAttendanceEntries(); // O observer cuidará de atualizar
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarDadosFila(); // Carrega ao resumir
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
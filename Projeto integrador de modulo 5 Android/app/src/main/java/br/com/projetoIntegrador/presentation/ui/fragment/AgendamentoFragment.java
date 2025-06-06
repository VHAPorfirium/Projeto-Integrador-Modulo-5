package br.com.projetoIntegrador.presentation.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.FollowUpDto;
import br.com.projetoIntegrador.network.FollowUpStatus; // Enum
import br.com.projetoIntegrador.presentation.adapter.HorariosAdapter;
import br.com.projetoIntegrador.presentation.adapter.MeusRetornosAdapter;
import br.com.projetoIntegrador.presentation.viewmodel.FollowUpViewModel;


public class AgendamentoFragment extends Fragment implements HorariosAdapter.OnHorarioClickListener, MeusRetornosAdapter.OnCancelarRetornoListener {

    private static final String TAG = "AgendamentoFragment";
    private static final String ARG_PACIENTE_ID = "pacienteId";

    private FollowUpViewModel followUpViewModel;
    private CalendarView calendarViewRetornos;
    private TextView tvDataSelecionada;
    private RecyclerView recyclerViewHorariosDisponiveis;
    private RecyclerView recyclerViewMeusRetornos;

    private HorariosAdapter horariosAdapter;
    private MeusRetornosAdapter meusRetornosAdapter;

    private Long pacienteIdLogado;
    private String dataSelecionadaFormatadaApi; // yyyy-MM-dd para API
    private SimpleDateFormat sdfApi = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


    public static AgendamentoFragment newInstance(Long pacienteId) {
        AgendamentoFragment fragment = new AgendamentoFragment();
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
        followUpViewModel = new ViewModelProvider(this).get(FollowUpViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agendamento, container, false);

        calendarViewRetornos = view.findViewById(R.id.calendarViewRetornos);
        tvDataSelecionada = view.findViewById(R.id.tvDataSelecionada);
        recyclerViewHorariosDisponiveis = view.findViewById(R.id.recyclerViewHorariosDisponiveis);
        recyclerViewMeusRetornos = view.findViewById(R.id.recyclerViewMeusRetornos);

        recyclerViewHorariosDisponiveis.setLayoutManager(new LinearLayoutManager(getContext()));
        horariosAdapter = new HorariosAdapter(new ArrayList<>(), this);
        recyclerViewHorariosDisponiveis.setAdapter(horariosAdapter);

        recyclerViewMeusRetornos.setLayoutManager(new LinearLayoutManager(getContext()));
        meusRetornosAdapter = new MeusRetornosAdapter(new ArrayList<>(), this);
        recyclerViewMeusRetornos.setAdapter(meusRetornosAdapter);

        Date hoje = new Date();
        calendarViewRetornos.setMinDate(hoje.getTime()); // Não permite selecionar datas passadas
        dataSelecionadaFormatadaApi = sdfApi.format(new Date(calendarViewRetornos.getDate()));
        tvDataSelecionada.setText("Horários para: " + sdfDisplay.format(new Date(calendarViewRetornos.getDate())));
        carregarHorariosDisponiveis(dataSelecionadaFormatadaApi);


        calendarViewRetornos.setOnDateChangeListener((cv, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            dataSelecionadaFormatadaApi = sdfApi.format(calendar.getTime());
            tvDataSelecionada.setText("Horários para: " + sdfDisplay.format(calendar.getTime()));
            carregarHorariosDisponiveis(dataSelecionadaFormatadaApi);
        });

        observarViewModel();
        carregarMeusRetornos();

        return view;
    }

    private void observarViewModel() {
        followUpViewModel.getAvailableFollowUpSlots("").observe(getViewLifecycleOwner(), horarios -> {
            // O LiveData de getAvailableFollowUpSlots é acionado quando chamado com uma data.
            // Esta observação inicial com string vazia é apenas para configurar.
            if (horarios != null && horariosAdapter != null) { // Adicionado check para horariosAdapter
                Log.d(TAG, "Horários recebidos: " + horarios.size());
                horariosAdapter.setHorarios(horarios);
                if (horarios.isEmpty() && dataSelecionadaFormatadaApi != null) { // Apenas mostra se uma data foi selecionada
                    Toast.makeText(getContext(), "Nenhum horário disponível para esta data.", Toast.LENGTH_SHORT).show();
                }
            } else if (horariosAdapter != null) { // Adicionado check para horariosAdapter
                Log.d(TAG, "Nenhum horário disponível ou erro ao carregar.");
                horariosAdapter.setHorarios(new ArrayList<>());
            }
        });

        followUpViewModel.listFollowUpsByPaciente(pacienteIdLogado).observe(getViewLifecycleOwner(), followUps -> {
            if (followUps != null && meusRetornosAdapter != null) { // Adicionado check para meusRetornosAdapter
                meusRetornosAdapter.setRetornos(followUps);
            } else if (meusRetornosAdapter != null) { // Adicionado check para meusRetornosAdapter
                meusRetornosAdapter.setRetornos(new ArrayList<>());
                // Não mostrar toast de erro aqui, pois pode ser que o paciente não tenha retornos.
            }
        });
    }

    private void carregarHorariosDisponiveis(String dataYyyyMmDd) {
        Log.d(TAG, "Carregando horários para: " + dataYyyyMmDd);
        followUpViewModel.getAvailableFollowUpSlots(dataYyyyMmDd);
    }

    private void carregarMeusRetornos() {
        if (pacienteIdLogado != null) {
            followUpViewModel.listFollowUpsByPaciente(pacienteIdLogado);
        }
    }

    @Override
    public void onHorarioClick(String horario) { // Callback do HorariosAdapter
        // Formatar data e horário para Instant (String ISO 8601 que o backend espera)
        try {
            // Ex: dataSelecionadaFormatadaApi (YYYY-MM-DD) e horario (HH:MM)
            String dateTimeString = dataSelecionadaFormatadaApi + "T" + horario + ":00Z"; // Assume UTC para o Instant. Ajuste se necessário.
            Instant scheduledInstant = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                scheduledInstant = Instant.parse(dateTimeString);
            }

            FollowUpDto novoRetorno = new FollowUpDto();
            novoRetorno.setPacienteId(pacienteIdLogado);
            novoRetorno.setScheduledTime(scheduledInstant.toString()); // Envia como String ISO 8601
            novoRetorno.setStatus(FollowUpStatus.AGENDADO.name());

            followUpViewModel.createFollowUp(novoRetorno).observe(getViewLifecycleOwner(), resultado -> {
                if (resultado != null) {
                    Toast.makeText(getContext(), "Retorno agendado com sucesso!", Toast.LENGTH_SHORT).show();
                    carregarMeusRetornos();
                    carregarHorariosDisponiveis(dataSelecionadaFormatadaApi); // Atualiza para remover o slot agendado
                } else {
                    Toast.makeText(getContext(), "Falha ao agendar retorno.", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Erro ao formatar data/hora para agendamento", e);
            Toast.makeText(getContext(), "Erro no formato da data/hora selecionada.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelarClick(FollowUpDto retornoParaCancelar) { // Callback do MeusRetornosAdapter
        // Verifica se pode cancelar (ex: até 12h antes)
        try {
            Instant agora = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                agora = Instant.now();
            }
            Instant scheduledTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                scheduledTime = Instant.parse(retornoParaCancelar.getScheduledTime());
            }
            long horasDeAntecedencia = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                horasDeAntecedencia = ChronoUnit.HOURS.between(agora, scheduledTime);
            }

            if (horasDeAntecedencia < 12) {
                Toast.makeText(getContext(), "Não é possível cancelar com menos de 12 horas de antecedência.", Toast.LENGTH_LONG).show();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao parsear data para verificar cancelamento", e);
            Toast.makeText(getContext(), "Erro ao verificar data do retorno.", Toast.LENGTH_SHORT).show();
            return; // Não prossegue se não puder verificar
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Cancelar Retorno")
                .setMessage("Tem certeza que deseja cancelar este retorno?")
                .setPositiveButton("Sim, Cancelar", (dialog, which) -> {
                    followUpViewModel.deleteFollowUp(retornoParaCancelar.getId()).observe(getViewLifecycleOwner(), sucesso -> {
                        if (sucesso != null && sucesso) {
                            Toast.makeText(getContext(), "Retorno cancelado.", Toast.LENGTH_SHORT).show();
                            carregarMeusRetornos();
                            carregarHorariosDisponiveis(dataSelecionadaFormatadaApi); // Atualizar horários
                        } else {
                            Toast.makeText(getContext(), "Falha ao cancelar retorno.", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Não", null)
                .show();
    }
}
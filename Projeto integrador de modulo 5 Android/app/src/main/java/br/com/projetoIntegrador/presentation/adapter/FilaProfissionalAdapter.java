package br.com.projetoIntegrador.presentation.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.AttendanceEntryDto;

public class FilaProfissionalAdapter extends RecyclerView.Adapter<FilaProfissionalAdapter.FilaProfissionalViewHolder> {

    private List<AttendanceEntryDto> listaEntradasFila;
    private Map<Long, String> nomesPacientes;
    private Map<Long, String> nomesEspecialidades;
    private OnItemFilaClickListener listener;
    private Context context;


    public interface OnItemFilaClickListener {
        void onItemClick(AttendanceEntryDto entrada);
    }

    public FilaProfissionalAdapter(Context context, List<AttendanceEntryDto> listaEntradasFila, Map<Long, String> nomesPacientes, Map<Long, String> nomesEspecialidades, OnItemFilaClickListener listener) {
        this.context = context;
        this.listaEntradasFila = listaEntradasFila != null ? listaEntradasFila : new ArrayList<>();
        this.nomesPacientes = nomesPacientes != null ? nomesPacientes : new android.util.ArrayMap<>();
        this.nomesEspecialidades = nomesEspecialidades != null ? nomesEspecialidades : new android.util.ArrayMap<>();
        this.listener = listener;
    }

    public void updateData(List<AttendanceEntryDto> novasEntradas, Map<Long, String> novosNomesPacientes, Map<Long, String> novosNomesEspecialidades) {
        this.listaEntradasFila.clear();
        if (novasEntradas != null) {
            this.listaEntradasFila.addAll(novasEntradas);
        }
        this.nomesPacientes.clear();
        if (novosNomesPacientes != null) {
            this.nomesPacientes.putAll(novosNomesPacientes);
        }
        this.nomesEspecialidades.clear();
        if (novosNomesEspecialidades != null) {
            this.nomesEspecialidades.putAll(novosNomesEspecialidades);
        }
        notifyDataSetChanged();
    }

    public List<AttendanceEntryDto> getCurrentList() {
        return new ArrayList<>(listaEntradasFila); // Retorna uma cópia para evitar modificação externa
    }


    @NonNull
    @Override
    public FilaProfissionalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fila_profissional, parent, false);
        return new FilaProfissionalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilaProfissionalViewHolder holder, int position) {
        AttendanceEntryDto entrada = listaEntradasFila.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.bind(entrada, nomesPacientes, nomesEspecialidades, listener, context);
        }
    }

    @Override
    public int getItemCount() {
        return listaEntradasFila.size();
    }

    static class FilaProfissionalViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenha, tvNome, tvEspecialidade, tvEspera, tvStatus;

        FilaProfissionalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenha = itemView.findViewById(R.id.tvItemFilaProfissionalSenha);
            tvNome = itemView.findViewById(R.id.tvItemFilaProfissionalNome);
            tvEspecialidade = itemView.findViewById(R.id.tvItemFilaProfissionalEspecialidade);
            tvEspera = itemView.findViewById(R.id.tvItemFilaProfissionalEspera);
            tvStatus = itemView.findViewById(R.id.tvItemFilaProfissionalStatus);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        void bind(final AttendanceEntryDto entrada, Map<Long, String> nomesPacientes, Map<Long, String> nomesEspecialidades, final OnItemFilaClickListener listener, Context context) {
            tvSenha.setText("E" + entrada.getId());

            String nomePaciente = nomesPacientes.get(entrada.getPacienteId());
            tvNome.setText(nomePaciente != null ? nomePaciente : "Paciente ID: " + entrada.getPacienteId());

            String nomeEspecialidade = nomesEspecialidades.get(entrada.getSpecialtyId());
            tvEspecialidade.setText("Especialidade: " + (nomeEspecialidade != null ? nomeEspecialidade : "ID " + entrada.getSpecialtyId()));

            if (entrada.getCheckInTime() != null) {
                try {
                    Instant checkIn = Instant.parse(entrada.getCheckInTime());
                    Instant agora = Instant.now();
                    Duration duracao = Duration.between(checkIn, agora);
                    long minutosEspera = duracao.toMinutes();
                    tvEspera.setText("Espera: " + minutosEspera + " min");
                } catch (DateTimeParseException e) {
                    tvEspera.setText("Espera: N/A");
                }
            } else {
                tvEspera.setText("Espera: N/A");
            }

            tvStatus.setText(entrada.getStatus());
            GradientDrawable background = (GradientDrawable) tvStatus.getBackground();
            if ("AGUARDANDO".equalsIgnoreCase(entrada.getStatus())) {
                background.setColor(ContextCompat.getColor(context, R.color.status_aguardando)); // Defina essas cores
            } else if ("CHAMADO".equalsIgnoreCase(entrada.getStatus())) {
                background.setColor(ContextCompat.getColor(context, R.color.status_chamado));
            } else if ("CONFIRMADO".equalsIgnoreCase(entrada.getStatus())) {
                background.setColor(ContextCompat.getColor(context, R.color.status_confirmado));
            } else {
                background.setColor(ContextCompat.getColor(context, R.color.light_gray));
            }


            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(entrada);
                }
            });
        }
    }
}
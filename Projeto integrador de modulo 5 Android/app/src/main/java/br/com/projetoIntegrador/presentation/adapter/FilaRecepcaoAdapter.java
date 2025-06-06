// Arquivo: app/src/main/java/br/com/projetoIntegrador/presentation/adapter/FilaRecepcaoAdapter.java

package br.com.projetoIntegrador.presentation.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.AttendanceEntryDto;
import br.com.projetoIntegrador.presentation.ui.activity.RecepcionistaActivity;

public class FilaRecepcaoAdapter extends RecyclerView.Adapter<FilaRecepcaoAdapter.FilaRecepcaoViewHolder> {

    private List<AttendanceEntryDto> listaEntradasFila;
    private Map<Long, String> nomesPacientes; // Mapa de PacienteID para Nome
    private Map<Long, String> nomesEspecialidades; // Mapa de SpecialtyID para Nome

    // CORREÇÃO: Deixei apenas o construtor correto.
    public FilaRecepcaoAdapter(List<AttendanceEntryDto> listaEntradasFila, Map<Long, String> nomesPacientes, Map<Long, String> nomesEspecialidades) {
        this.listaEntradasFila = listaEntradasFila != null ? listaEntradasFila : new ArrayList<>();
        this.nomesPacientes = nomesPacientes != null ? nomesPacientes : new android.util.ArrayMap<>();
        this.nomesEspecialidades = nomesEspecialidades != null ? nomesEspecialidades : new android.util.ArrayMap<>();
    }

    public FilaRecepcaoAdapter(RecepcionistaActivity recepcionistaActivity, ArrayList<Object> objects, Map<Long, String> mapNomesPacientes, Map<Long, String> mapNomesEspecialidades, Object o) {
    }

    // CORREÇÃO: O nome do método é 'setEntries', não 'updateData'. Mudei aqui para refletir o que existe.
    public void setEntries(List<AttendanceEntryDto> novasEntradas, Map<Long, String> novosNomesPacientes, Map<Long, String> novosNomesEspecialidades) {
        this.listaEntradasFila.clear();
        if (novasEntradas != null) {
            this.listaEntradasFila.addAll(novasEntradas);
        }
        this.nomesPacientes.clear();
        if(novosNomesPacientes != null){
            this.nomesPacientes.putAll(novosNomesPacientes);
        }
        this.nomesEspecialidades.clear();
        if(novosNomesEspecialidades != null){
            this.nomesEspecialidades.putAll(novosNomesEspecialidades);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilaRecepcaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fila_recepcao, parent, false);
        return new FilaRecepcaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilaRecepcaoViewHolder holder, int position) {
        AttendanceEntryDto entrada = listaEntradasFila.get(position);
        holder.tvItemFilaSenhaRecepcao.setText("E" + entrada.getId()); // Senha visual

        String nomePaciente = nomesPacientes.get(entrada.getPacienteId());
        holder.tvItemFilaNomeRecepcao.setText(nomePaciente != null ? nomePaciente : "Paciente ID: " + entrada.getPacienteId());

        String nomeEspecialidade = nomesEspecialidades.get(entrada.getSpecialtyId());
        holder.tvItemFilaEspecialidadeRecepcao.setText(nomeEspecialidade != null ? nomeEspecialidade : "Especialidade ID: " + entrada.getSpecialtyId());

        if (entrada.getCheckInTime() != null) {
            try {
                Instant instant = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    instant = Instant.parse(entrada.getCheckInTime());
                }
                LocalDateTime localDateTime = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                }
                DateTimeFormatter formatter = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    holder.tvItemFilaChegadaRecepcao.setText(localDateTime.format(formatter));
                }
            } catch (Exception e) {
                holder.tvItemFilaChegadaRecepcao.setText("N/A");
            }
        } else {
            holder.tvItemFilaChegadaRecepcao.setText("N/A");
        }

        holder.tvItemFilaStatusRecepcao.setText(entrada.getStatus());
    }

    @Override
    public int getItemCount() {
        return listaEntradasFila.size();
    }

    static class FilaRecepcaoViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemFilaSenhaRecepcao, tvItemFilaNomeRecepcao, tvItemFilaEspecialidadeRecepcao,
                tvItemFilaChegadaRecepcao, tvItemFilaStatusRecepcao;
        FilaRecepcaoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemFilaSenhaRecepcao = itemView.findViewById(R.id.tvItemFilaSenhaRecepcao);
            tvItemFilaNomeRecepcao = itemView.findViewById(R.id.tvItemFilaNomeRecepcao);
            tvItemFilaEspecialidadeRecepcao = itemView.findViewById(R.id.tvItemFilaEspecialidadeRecepcao);
            tvItemFilaChegadaRecepcao = itemView.findViewById(R.id.tvItemFilaChegadaRecepcao);
            tvItemFilaStatusRecepcao = itemView.findViewById(R.id.tvItemFilaStatusRecepcao);
        }
    }
}
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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.AttendanceEntryDto;

public class FilaRecepcaoAdapter extends RecyclerView.Adapter<FilaRecepcaoAdapter.FilaRecepcaoViewHolder> {

    // GARANTA QUE A LISTA SEJA INICIALIZADA AQUI!
    private List<AttendanceEntryDto> listaEntradas = new ArrayList<>();
    private Map<Long, String> nomesPacientes = Collections.emptyMap();
    private Map<Long, String> nomesEspecialidades = Collections.emptyMap();

    // Construtor vazio que será usado na Activity.
    public FilaRecepcaoAdapter() {
        // O construtor está vazio, o que é ótimo. A inicialização é feita acima.
    }

    // Método para atualizar os dados do adapter.
    public void setEntries(List<AttendanceEntryDto> novasEntradas, Map<Long, String> novosNomesPacientes, Map<Long, String> novosNomesEspecialidades) {
        // Esta verificação também previne que a lista se torne nula
        this.listaEntradas = (novasEntradas != null) ? novasEntradas : new ArrayList<>();
        this.nomesPacientes = (novosNomesPacientes != null) ? novosNomesPacientes : Collections.emptyMap();
        this.nomesEspecialidades = (novosNomesEspecialidades != null) ? novosNomesEspecialidades : Collections.emptyMap();

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
        // A lista nunca será nula aqui por causa das verificações acima.
        AttendanceEntryDto entrada = listaEntradas.get(position);
        holder.bind(entrada, nomesPacientes, nomesEspecialidades);
    }

    @Override
    public int getItemCount() {
        // CÓDIGO CORRIGIDO E SEGURO:
        // Se a lista for nula (o que não deve acontecer com o código acima), retorna 0.
        return listaEntradas != null ? listaEntradas.size() : 0;
    }

    static class FilaRecepcaoViewHolder extends RecyclerView.ViewHolder {
        // ... seu código do ViewHolder permanece o mesmo ...
        TextView tvSenha, tvNomePaciente, tvEspecialidade, tvHoraChegada, tvStatus;

        FilaRecepcaoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenha = itemView.findViewById(R.id.tvItemFilaSenhaRecepcao);
            tvNomePaciente = itemView.findViewById(R.id.tvItemFilaNomeRecepcao);
            tvEspecialidade = itemView.findViewById(R.id.tvItemFilaEspecialidadeRecepcao);
            tvHoraChegada = itemView.findViewById(R.id.tvItemFilaChegadaRecepcao);
            tvStatus = itemView.findViewById(R.id.tvItemFilaStatusRecepcao);
        }

        void bind(AttendanceEntryDto entrada, Map<Long, String> nomesPacientes, Map<Long, String> nomesEspecialidades) {
            tvSenha.setText("E" + entrada.getId());
            tvNomePaciente.setText(nomesPacientes.getOrDefault(entrada.getPacienteId(), "ID: " + entrada.getPacienteId()));
            tvEspecialidade.setText(nomesEspecialidades.getOrDefault(entrada.getSpecialtyId(), "ID: " + entrada.getSpecialtyId()));
            tvStatus.setText(formatarStatus(entrada.getStatus()));
            tvHoraChegada.setText(formatarHora(entrada.getCheckInTime()));
        }

        private String formatarHora(String checkInTimeString) {
            if (checkInTimeString == null || checkInTimeString.isEmpty()) return "N/A";
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
                    Instant instant = Instant.parse(checkInTimeString);
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    return localDateTime.format(formatter);
                } else {
                    return checkInTimeString.substring(11, 16);
                }
            } catch (DateTimeParseException e) {
                return "Inválida";
            }
        }

        private String formatarStatus(String status) {
            if (status == null) return "";
            return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase().replace("_", " ");
        }
    }
}
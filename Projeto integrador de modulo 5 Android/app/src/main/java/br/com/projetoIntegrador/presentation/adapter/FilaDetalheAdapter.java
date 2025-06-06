package br.com.projetoIntegrador.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.AttendanceEntryDto;

public class FilaDetalheAdapter extends RecyclerView.Adapter<FilaDetalheAdapter.FilaViewHolder> {

    private List<AttendanceEntryDto> listaEntradasFila;
    private Long pacienteIdLogado; // Para destacar o usuário logado, se necessário

    public FilaDetalheAdapter(List<AttendanceEntryDto> listaEntradasFila, Long pacienteIdLogado) {
        this.listaEntradasFila = listaEntradasFila;
        this.pacienteIdLogado = pacienteIdLogado;
    }

    public void setEntradas(List<AttendanceEntryDto> novasEntradas) {
        this.listaEntradasFila.clear();
        if (novasEntradas != null) {
            this.listaEntradasFila.addAll(novasEntradas);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fila_detalhe, parent, false);
        return new FilaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilaViewHolder holder, int position) {
        AttendanceEntryDto entrada = listaEntradasFila.get(position);
        holder.tvItemPosicao.setText((position + 1) + "º"); // Posição visual na lista filtrada
        holder.tvItemStatusFila.setText(entrada.getStatus());

        // Idealmente, o nome do paciente e da especialidade viriam no DTO ou seriam buscados.
        // Aqui, uma simplificação:
        if (entrada.getPacienteId().equals(pacienteIdLogado)) {
            holder.tvItemNomePacienteFila.setText("Você (ID: " + entrada.getPacienteId() + ")");
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.light_gray)); // Destaque
        } else {
            holder.tvItemNomePacienteFila.setText("Paciente (ID: " + entrada.getPacienteId() + ")");
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
        }
        holder.tvItemEspecialidadeFila.setText("Especialidade ID: " + entrada.getSpecialtyId());

    }

    @Override
    public int getItemCount() {
        return listaEntradasFila.size();
    }

    static class FilaViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemPosicao, tvItemNomePacienteFila, tvItemEspecialidadeFila, tvItemStatusFila;

        FilaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemPosicao = itemView.findViewById(R.id.tvItemPosicao);
            tvItemNomePacienteFila = itemView.findViewById(R.id.tvItemNomePacienteFila);
            tvItemEspecialidadeFila = itemView.findViewById(R.id.tvItemEspecialidadeFila);
            tvItemStatusFila = itemView.findViewById(R.id.tvItemStatusFila);
        }
    }
}
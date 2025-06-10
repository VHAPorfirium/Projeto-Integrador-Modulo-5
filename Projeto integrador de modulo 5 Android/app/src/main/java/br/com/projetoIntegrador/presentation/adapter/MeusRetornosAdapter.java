package br.com.projetoIntegrador.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.FollowUpDto;
import br.com.projetoIntegrador.util.DateUtil; // <-- IMPORTANTE: Importa a classe de utilidade

public class MeusRetornosAdapter extends RecyclerView.Adapter<MeusRetornosAdapter.RetornoViewHolder> {

    private List<FollowUpDto> listaRetornos;
    private OnCancelarRetornoListener listener;

    public interface OnCancelarRetornoListener {
        void onCancelarClick(FollowUpDto retorno);
    }

    public MeusRetornosAdapter(List<FollowUpDto> listaRetornos, OnCancelarRetornoListener listener) {
        this.listaRetornos = listaRetornos;
        this.listener = listener;
    }

    public void setRetornos(List<FollowUpDto> novosRetornos) {
        this.listaRetornos.clear();
        if (novosRetornos != null) {
            this.listaRetornos.addAll(novosRetornos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RetornoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meu_retorno, parent, false);
        return new RetornoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RetornoViewHolder holder, int position) {
        FollowUpDto retorno = listaRetornos.get(position);

        // ===== AQUI ESTÁ A CORREÇÃO PRINCIPAL =====
        // Trocamos todo o bloco 'try-catch' complicado por uma única chamada à nossa classe 'DateUtil'.
        String dataFormatada = DateUtil.formatTimestamp(retorno.getScheduledTime());
        holder.tvDataHoraMeuRetorno.setText("Data: " + dataFormatada);
        // ============================================

        holder.tvStatusMeuRetorno.setText("Status: " + retorno.getStatus());

        boolean podeCancelar = "AGENDADO".equalsIgnoreCase(retorno.getStatus());
        holder.btnCancelarMeuRetorno.setEnabled(podeCancelar);
        holder.btnCancelarMeuRetorno.setVisibility(podeCancelar ? View.VISIBLE : View.GONE);

        if (podeCancelar) {
            holder.btnCancelarMeuRetorno.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelarClick(retorno);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaRetornos != null ? listaRetornos.size() : 0;
    }

    static class RetornoViewHolder extends RecyclerView.ViewHolder {
        TextView tvDataHoraMeuRetorno, tvStatusMeuRetorno;
        Button btnCancelarMeuRetorno;

        RetornoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDataHoraMeuRetorno = itemView.findViewById(R.id.tvDataHoraMeuRetorno);
            tvStatusMeuRetorno = itemView.findViewById(R.id.tvStatusMeuRetorno);
            btnCancelarMeuRetorno = itemView.findViewById(R.id.btnCancelarMeuRetorno);
        }
    }
}
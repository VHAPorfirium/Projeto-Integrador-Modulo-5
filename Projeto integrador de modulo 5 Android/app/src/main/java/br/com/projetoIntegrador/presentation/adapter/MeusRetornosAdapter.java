package br.com.projetoIntegrador.presentation.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.FollowUpDto;

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

        // Formatar data e hora do scheduledTime (String ISO para display)
        try {
            // Assume que retorno.getScheduledTime() é uma String ISO 8601 (Instant)
            Instant instant = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                instant = Instant.parse(retorno.getScheduledTime());
            }
            LocalDateTime localDateTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            DateTimeFormatter formatter = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm", Locale.getDefault());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                holder.tvDataHoraMeuRetorno.setText("Data: " + localDateTime.format(formatter));
            }
        } catch (Exception e) {
            holder.tvDataHoraMeuRetorno.setText("Data: " + retorno.getScheduledTime() + " (Formato inválido)");
        }

        holder.tvStatusMeuRetorno.setText("Status: " + retorno.getStatus());

        // Lógica para habilitar/desabilitar botão de cancelar
        // Ex: só pode cancelar se status for AGENDADO e com antecedência
        boolean podeCancelar = "AGENDADO".equalsIgnoreCase(retorno.getStatus()); // Adicionar lógica de antecedência
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
        return listaRetornos.size();
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
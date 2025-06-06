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

public class HorariosAdapter extends RecyclerView.Adapter<HorariosAdapter.HorarioViewHolder> {

    private List<String> listaHorarios;
    private OnHorarioClickListener listener;

    public interface OnHorarioClickListener {
        void onHorarioClick(String horario);
    }

    public HorariosAdapter(List<String> listaHorarios, OnHorarioClickListener listener) {
        this.listaHorarios = listaHorarios;
        this.listener = listener;
    }

    public void setHorarios(List<String> novosHorarios) {
        this.listaHorarios.clear();
        if (novosHorarios != null) {
            this.listaHorarios.addAll(novosHorarios);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horario_disponivel, parent, false);
        return new HorarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) {
        String horario = listaHorarios.get(position);
        holder.tvHorarioSlot.setText(horario);
        holder.btnAgendarHorario.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHorarioClick(horario);
            }
        });
        holder.itemView.setOnClickListener(v -> { // Permite clicar no item todo também
            if (listener != null) {
                listener.onHorarioClick(horario);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaHorarios.size();
    }

    static class HorarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvHorarioSlot;
        Button btnAgendarHorario;

        HorarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHorarioSlot = itemView.findViewById(R.id.tvHorarioSlot);
            btnAgendarHorario = itemView.findViewById(R.id.btnAgendarHorario);
        }
    }
}
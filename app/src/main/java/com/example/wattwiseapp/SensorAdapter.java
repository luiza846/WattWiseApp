package com.example.wattwiseapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    private Context context;
    private List<Sensor> sensorList;
    private OnItemClickListener listener;


    public interface OnItemClickListener {
        void onEditClick(Sensor sensor);
        void onDeleteClick(Sensor sensor);
    }


    public SensorAdapter(Context context, List<Sensor> sensorList, OnItemClickListener listener) {
        this.listener = listener;
        this.context = context;
        this.sensorList = sensorList;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_sensor, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        Sensor sensor = sensorList.get(position);

        holder.textNomeDoSensor.setText(sensor.getIdSensor());

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String idEletro = sensor.getIdEletroAtivo();

        if(idEletro != null && !idEletro.trim().isEmpty()) {
            DatabaseReference eletroRef = FirebaseDatabase
                    .getInstance()
                    .getReference("Usuarios")
                    .child(userId)
                    .child("Eletronicos")
                    .child(sensor.getIdEletroAtivo());

            eletroRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nomeFormatado = snapshot.child("nomeEletro").getValue(String.class);
                        holder.textTipoEletro.setText(nomeFormatado != null ? nomeFormatado : "Eletrônico Desconhecido");
                    } else {
                        holder.textTipoEletro.setText("Aparelho deletado ou não encontrado");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    holder.textTipoEletro.setText("Erro ao carregar!");
                }
            });
        } else {
            holder.textTipoEletro.setText("Nenhum aparelho vinculado");
        }

        holder.textTipoEletro.setText(sensor.getIdEletroAtivo());

        holder.btnEditar.setOnClickListener(view -> {
            if (listener != null) listener.onEditClick(sensor);
        });

        holder.btnExcluir.setOnClickListener(view -> {
            if (listener != null) listener.onDeleteClick(sensor);
        });
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    static class SensorViewHolder extends RecyclerView.ViewHolder {
        TextView textNomeDoSensor, textTipoEletro;
        MaterialButton btnEditar, btnExcluir;

        SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            textNomeDoSensor = itemView.findViewById(R.id.textNomeComodo);
            textTipoEletro = itemView.findViewById(R.id.textTipoComodo);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}

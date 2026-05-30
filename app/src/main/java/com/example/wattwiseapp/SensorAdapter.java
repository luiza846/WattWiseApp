package com.example.wattwiseapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    private Context context;
    private List<Sensor> sensorList;

    public SensorAdapter(Context context, List<Sensor> sensorList) {
        this.context = context;
        this.sensorList = sensorList;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        Sensor sensor = sensorList.get(position);
        holder.tvNome.setText(sensor.getIdSensor());
        holder.tvEletro.setText(sensor.getIdEletroAtivo());
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    static class SensorViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvEletro;

        SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.textNomeComodo);
            tvEletro = itemView.findViewById(R.id.textTipoComodo);
        }
    }
}

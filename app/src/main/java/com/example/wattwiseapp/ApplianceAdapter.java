package com.example.wattwiseapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.*;

public class ApplianceAdapter extends RecyclerView.Adapter<ApplianceAdapter.ApplianceViewHolder> {

    private Context context;
    private List<Appliance> applianceList;

    private OnApplianceActionListener listener;

    //interface para o editar
    public interface OnApplianceActionListener {
        void onEdit(Appliance appliance);
        void onDelete(String idEletro);
    }

    //construtor

    public ApplianceAdapter(Context context, List<Appliance> applianceList, OnApplianceActionListener listener) {
        this.context = context;
        this.applianceList = applianceList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ApplianceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_appliance, parent, false);
        return new ApplianceViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull ApplianceViewHolder holder, int position) {
        Appliance appliance = applianceList.get(position);

        holder.tvNome.setText(appliance.getNomeEletro());
        holder.tvPotencia.setText(appliance.getPotenciaEletro() + " W");
        holder.tvComodo.setText(appliance.getComodoEletro()); // mostra em qual cômodo está


        holder.btnEdit.setOnClickListener(v -> {

            listener.onEdit(appliance);

        });

        holder.btnDelete.setOnClickListener(v -> {

            listener.onDelete(appliance.getIdEletro());

        });

    }


    @Override
    public int getItemCount() {
        return applianceList.size();
    }


    //classe interna que vai "segurar" os componentes do layout
    public static class ApplianceViewHolder extends RecyclerView.ViewHolder {

        TextView tvNome;
        TextView tvPotencia;
        TextView tvComodo;
        MaterialButton btnEdit;
        MaterialButton btnDelete;


        public ApplianceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNome = itemView.findViewById(R.id.tvApplianceName);
            tvPotencia = itemView.findViewById(R.id.tvAppliancePower);
            tvComodo = itemView.findViewById(R.id.tvApplianceRoom);
            btnEdit = itemView.findViewById(R.id.btnEditAppliance);
            btnDelete = itemView.findViewById(R.id.btnDeleteAppliance);

        }

    }



}

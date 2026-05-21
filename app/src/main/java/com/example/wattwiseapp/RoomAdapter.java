package com.example.wattwiseapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<Room> roomList;

    public RoomAdapter(List<Room> roomList) {
        this.roomList = roomList;
    }



    @NonNull
    @Override
    public RoomAdapter.RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RoomAdapter.RoomViewHolder holder, int position) {

        Room room = roomList.get(position);

        holder.textNomeComodo.setText(room.getNomeComodo());

        holder.textTipoComodo.setText(room.getTipoComodo());

        holder.btnExcluir.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Preparando exclusão: " + room.getNomeComodo(), Toast.LENGTH_SHORT).show();
        });

        holder.btnEditar.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Preparando edição: " + room.getNomeComodo(), Toast.LENGTH_SHORT).show();
        });

    }


    @Override
    public int getItemCount() {
        return roomList.size();
    }


    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView textNomeComodo, textTipoComodo;
        Button btnEditar, btnExcluir;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            textNomeComodo = itemView.findViewById(R.id.textNomeComodo);
            textTipoComodo = itemView.findViewById(R.id.textTipoComodo);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }

}

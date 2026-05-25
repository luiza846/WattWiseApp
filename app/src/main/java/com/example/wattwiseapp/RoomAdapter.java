package com.example.wattwiseapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;


public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private Context context;

    private List<Room> roomList;

    private OnRoomActionListener listener;

    public interface OnRoomActionListener {
        void onEdit(Room room);
        void onDelete(String idComodo);
    }

    public RoomAdapter(Context context, List<Room> roomList, OnRoomActionListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.listener = listener;
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

        holder.tvNome.setText(room.getNomeComodo());

        holder.tvTomadas.setText(room.getQtdTomadas() + " tomadas");

        holder.tvTipo.setText(room.getTipoComodo());

        holder.btnEditar.setOnClickListener(view -> {
            listener.onEdit(room);
        });

        holder.btnExcluir.setOnClickListener(v -> {
            listener.onDelete(room.getIdComodo());
        });

    }


    @Override
    public int getItemCount() {
        return roomList.size();
    }


    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvTipo, tvTomadas;
        MaterialButton btnEditar, btnExcluir;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNome = itemView.findViewById(R.id.textNomeComodo);
            tvTipo = itemView.findViewById(R.id.textTipoComodo);
            tvTomadas = itemView.findViewById(R.id.editQtdTomadas);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }

}

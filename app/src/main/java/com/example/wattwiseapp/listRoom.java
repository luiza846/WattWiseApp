package com.example.wattwiseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

public class listRoom extends AppCompatActivity {

    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private RoomAdapter adapter;
    private java.util.List<Room> listaDeComodos;

    private LinearLayout layoutVazio;
    TextView txtVazio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_room);

        layoutVazio = findViewById(R.id.layoutVazio);

        txtVazio = findViewById(R.id.txtVazio);
        layoutVazio.setVisibility(View.GONE);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaDeComodos = new java.util.ArrayList<>();
        recyclerView = findViewById(R.id.listaComodos);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        adapter = new RoomAdapter(this, listaDeComodos, new RoomAdapter.OnRoomActionListener() {
            @Override
            public void onEdit(Room room) {
                Intent intent = new Intent(listRoom.this, editRoom.class);
                intent.putExtra("idComodo", room.getIdComodo());
                intent.putExtra("nomeComodo", room.getNomeComodo());
                intent.putExtra("tipoComodo", room.getTipoComodo());
                intent.putExtra("qtdTomadas", room.getQtdTomadas());
                intent.putExtra("descricaoComodo", room.getDescricao());
                startActivity(intent);
            }

            @Override
            public void onDelete(String idComodo) {
                com.google.firebase.auth.FirebaseUser currentUser =
                        com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    String userId = currentUser.getUid();

                    com.google.firebase.database.FirebaseDatabase.getInstance()
                            .getReference("Usuarios")
                            .child(userId)
                            .child("Comodos")
                            .child(idComodo)
                            .removeValue()
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(listRoom.this, "Cômodo excluído!", Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(listRoom.this, "Erro ao excluir: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                } else {
                    Toast.makeText(listRoom.this, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setAdapter(adapter);


        // Listagem do Firebase
        com.google.firebase.auth.FirebaseUser currentUser =
                com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            com.google.firebase.database.DatabaseReference comodosRef =
                    com.google.firebase.database.FirebaseDatabase.getInstance()
                            .getReference("Usuarios")
                            .child(userId)
                            .child("Comodos");

            comodosRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                    listaDeComodos.clear();

                    for (com.google.firebase.database.DataSnapshot comodoSnapshot : snapshot.getChildren()) {
                        Room room = comodoSnapshot.getValue(Room.class);

                        if (room != null) {
                            // salva a key do Firebase no objeto
                            room.setIdComodo(comodoSnapshot.getKey());
                            listaDeComodos.add(room);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    // se nao tiver cadastrado nenhum comodo
                    if (listaDeComodos.isEmpty()) {
                        layoutVazio.setVisibility(View.VISIBLE);
                        txtVazio.setText(
                                "Nenhum registro encontrado " +
                                        " Cadastre um cômodo"
                        );
                    } else {
                        txtVazio.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                    android.util.Log.e("FirebaseError", "Erro ao buscar cômodos: " + error.getMessage());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reg_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sensor) {
            Intent i = new Intent(listRoom.this, listSensor.class);
            startActivity(i);
            return true;
        } else if (id == R.id.comodo) {
            startActivity(new Intent(listRoom.this, listRoom.class));
            return true;
        } else if (id == R.id.eletronicos) {
            startActivity(new Intent(listRoom.this, listAppliance.class));
            return true;
        } else if (id == R.id.relatorios) {
            startActivity(new Intent(listRoom.this, report.class));
            return true;
        } else if (id == R.id.metas) {
            startActivity(new Intent(listRoom.this, Dashboard.class));
            return true;
        } else if (id == R.id.configuracao) {
            startActivity(new Intent(listRoom.this, settings.class));
            return true;
        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(listRoom.this, MainActivity.class);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        } else if (id == R.id.menu_cadastrar) {
            startActivity(new Intent(listRoom.this, registerRoom.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
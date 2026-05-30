package com.example.wattwiseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class listAppliance extends AppCompatActivity {

    private RecyclerView recyclerViewEletro;
    private ApplianceAdapter adapter;
    private List<Appliance> applianceList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_appliance);

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

        // ---CONFIGURAÇÃO DO RECYCLERVIEW ---
        recyclerViewEletro = findViewById(R.id.listaEletrodomesticos);
        recyclerViewEletro.setLayoutManager(new LinearLayoutManager(this));

        applianceList = new ArrayList<>();

        adapter = new ApplianceAdapter(this, applianceList, new ApplianceAdapter.OnApplianceActionListener() {
            @Override
            public void onEdit(Appliance appliance) {
                Intent intent = new Intent(listAppliance.this, editAppliance.class);
                intent.putExtra("idEletro", appliance.getIdEletro());
                intent.putExtra("nomeEletro", appliance.getNomeEletro());
                intent.putExtra("tipoEletro", appliance.getTipoEletro());
                intent.putExtra("comodoEletro", appliance.getComodoEletro());
                intent.putExtra("potenciaEletro", appliance.getPotenciaEletro());
                intent.putExtra("descricaoEletro", appliance.getDescricaoEletro());
                startActivity(intent);
            }

            @Override
            public void onDelete(String idEletro) {
                databaseReference.child(idEletro).removeValue()
                        .addOnSuccessListener(unused ->
                                Toast.makeText(listAppliance.this, "Eletrodoméstico excluído!", Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(listAppliance.this, "Erro ao excluir: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            }
        });

        recyclerViewEletro.setAdapter(adapter);


        // --- CONEXÃO COM O FIREBASE (Mock Data - Dados Fictícios) ---
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("Usuarios")
                    .child(userId)
                    .child("Eletronicos");

            carregarEletrodomesticos();

        } else {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
        }
    }
        private void carregarEletrodomesticos() {

            databaseReference.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    applianceList.clear();

                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {

                        String id = itemSnapshot.getKey();
                        String nome = itemSnapshot.child("nomeEletro").getValue(String.class);
                        String tipo = itemSnapshot.child("tipoEletro").getValue(String.class);
                        String comodo = itemSnapshot.child("comodoEletro").getValue(String.class);
                        String potencia = String.valueOf(itemSnapshot.child("potenciaEletro").getValue());
                        String desc = itemSnapshot.child("descricaoEletro").getValue(String.class);

                        if (nome != null) {
                            Appliance appliance = new Appliance(id, nome, tipo, comodo, potencia, desc);
                            applianceList.add(appliance);
                        }
                    }

                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(listAppliance.this, "Erro ao carregar dados: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        // --- CONFIGURAÇÃO DO MENU ---
        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.menu_reg_appliance, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (@NonNull MenuItem item){
            int id = item.getItemId();

            if (id == R.id.sensor) {
                Intent i = new Intent(listAppliance.this, connectSensor.class);
                startActivity(i);
                return true;
            } else if (id == R.id.comodo) {
                Intent i = new Intent(listAppliance.this, listRoom.class);
                startActivity(i);
                return true;
            } else if (id == R.id.eletronicos) {
                Intent i = new Intent(listAppliance.this, listAppliance.class);
                startActivity(i);
                return true;
            } else if (id == R.id.relatorios) {
                Intent i = new Intent(listAppliance.this, report.class);
                startActivity(i);
                return true;
            } else if (id == R.id.metas) {
                Intent i = new Intent(listAppliance.this, metas.class);
                startActivity(i);
                return true;
            } else if (id == R.id.configuracao) {
                Intent i = new Intent(listAppliance.this, settings.class);
                startActivity(i);
                return true;
            } else if (id == R.id.logout) {
                Intent i = new Intent(listAppliance.this, MainActivity.class);
                startActivity(i);
                return true;
            } else if (id == R.id.menu_cadastrar) {
                Intent i = new Intent(listAppliance.this, registerAppliance.class);
                startActivity(i);
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    }

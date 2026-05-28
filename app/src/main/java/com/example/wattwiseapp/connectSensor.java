package com.example.wattwiseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class connectSensor extends AppCompatActivity {

    private Spinner spinnerSensores;
    private Spinner spinnerEletros;
    private Button btnConnectSensor;

    //lista para preencher os Spinners
    private List<String> listaSensores = new ArrayList<>();
    private List<String> nomesEletros = new ArrayList<>();

    private List<String> idsEletros = new ArrayList<>(); //lista paralela para guardar os IDs reais dos Eletros.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connect_sensor);

        // menu
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerSensores = findViewById(R.id.spinnerSensores);
        spinnerEletros = findViewById(R.id.spinnerEletros);
        btnConnectSensor = findViewById(R.id.btnConnectSensor);

        //preenche os spinner de sensores (manual/estático por enquanto) TODO AJUSTAR DEPOIS COM O FERNANDO
        listaSensores.add("sensor_01"); //TODO: TEM QUE SER O ID EXATO QUE O FERNANDO VAI USAR NO BANCO
        ArrayAdapter<String> adapterSensores = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaSensores);
        adapterSensores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSensores.setAdapter(adapterSensores);

        //preenche o spinner de eletrodomésticos puxando do firebase
        carregarEletrodomesticos();

        //ação do botão conectar
        btnConnectSensor.setOnClickListener(view -> conectarEletroAoSensor());


        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

    }

    private void carregarEletrodomesticos() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) return;

        String userId = currentUser.getUid();
        DatabaseReference eletrosRef = FirebaseDatabase.getInstance()
                .getReference("Usuarios")
                .child(userId)
                .child("Eletronicos");

        //adapter para os eletrodomesticos
        ArrayAdapter<String> adapterEletros = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomesEletros);
        adapterEletros.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEletros.setAdapter(adapterEletros);


        eletrosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nomesEletros.clear();
                idsEletros.clear();

                for (DataSnapshot eletroSnap : snapshot.getChildren()) {
                    String id = eletroSnap.getKey();
                    String nome = eletroSnap.child("nomeEletro").getValue(String.class);

                    if (nome != null && id != null) {
                        nomesEletros.add(nome);
                        idsEletros.add(id); //guarda o ID verdadeiro de maneira oculta
                    }
                }

                ArrayAdapter<String> adapterEletros = (ArrayAdapter<String>) spinnerEletros.getAdapter();
                if(adapterEletros != null) {
                    adapterEletros.notifyDataSetChanged();
                }

                if (nomesEletros.isEmpty()) {
                    Toast.makeText(connectSensor.this, "Nenhum eletrodoméstico cadastrado.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(connectSensor.this, "Erro ao carregar aparelhos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void conectarEletroAoSensor() {
        //valida se o usuario escolheu algo
        if (spinnerSensores.getSelectedItem() == null || spinnerEletros.getSelectedItem() == null) {
            Toast.makeText(this, "Selecione um sensor e um aparelho!", Toast.LENGTH_SHORT).show();
            return;
        }

        String idSensorEscolhido = spinnerSensores.getSelectedItem().toString();

        //pega a posição que o usuário clicou no spinner e usa ela para puxar o ID real da lista

        int posicaoEletro = spinnerEletros.getSelectedItemPosition();
        String idEletroEscolhido = idsEletros.get(posicaoEletro);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            //grava o ID do aparelho dentro da pasta do sensor no firebase
            DatabaseReference sensorRef = FirebaseDatabase.getInstance()
                    .getReference("Usuarios")
                    .child(userId)
                    .child("dados")
                    .child(idSensorEscolhido);

            sensorRef.child("idEletroAtivo").setValue(idEletroEscolhido).addOnSuccessListener(unused -> {
                Toast.makeText(this, "Sensor conectado com sucesso!", Toast.LENGTH_SHORT).show();

                finish(); // volta pra tela anterior

            })
                    .addOnFailureListener(e -> {
                            Toast.makeText(this, "Erro ao conectar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

    }

    // menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sensor) {
            Intent i = new Intent(connectSensor.this, connectSensor.class);
            startActivity(i);
            return true;
        } else if (id == R.id.comodo) {
            Intent i = new Intent(connectSensor.this, listRoom.class);
            startActivity(i);
            return true;
        } else if (id == R.id.eletronicos) {
            Intent i = new Intent(connectSensor.this, listAppliance.class);
            startActivity(i);
            return true;
        } else if (id == R.id.relatorios) {
            Intent i = new Intent(connectSensor.this, report.class);
            startActivity(i);
            return true;
        } else if (id == R.id.metas) {
            Intent i = new Intent(connectSensor.this, metas.class);
            startActivity(i);
            return true;
        } else if (id == R.id.configuracao) {
            Intent i = new Intent(connectSensor.this, settings.class);
            startActivity(i);
            return true;
        } else if (id == R.id.logout) {
            Intent i = new Intent(connectSensor.this, MainActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

}
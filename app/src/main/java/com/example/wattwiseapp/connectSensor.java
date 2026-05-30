package com.example.wattwiseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    private EditText edtNomeSensor;
    private Spinner spinnerEletros;
    private Button btnConnectSensor;

    // Listas para preencher o Spinner de Aparelhos
    private List<String> nomesEletros = new ArrayList<>();
    private List<String> idsEletros = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_sensor);

        // Configura Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Vincula os itens da tela
        edtNomeSensor = findViewById(R.id.edtNomeSensor);
        spinnerEletros = findViewById(R.id.spinnerEletros);
        btnConnectSensor = findViewById(R.id.btnConnectSensor);

        // Preenche o spinner de eletrodomésticos
        carregarEletrodomesticos();

        // Ação do Botão Conectar
        btnConnectSensor.setOnClickListener(view -> conectarEletroAoSensor());
    }

    private void carregarEletrodomesticos() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        DatabaseReference eletrosRef = FirebaseDatabase.getInstance()
                .getReference("Usuarios")
                .child(userId)
                .child("Eletronicos");

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
                        idsEletros.add(id);
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
        String idSensorDigitado = edtNomeSensor.getText().toString().trim();

        // Valida se o usuário escreveu e escolheu algo
        if (idSensorDigitado.isEmpty() || spinnerEletros.getSelectedItem() == null) {
            Toast.makeText(this, "Preencha a licença e selecione um aparelho!", Toast.LENGTH_SHORT).show();
            return;
        }

        int posicaoEletro = spinnerEletros.getSelectedItemPosition();
        String idEletroEscolhido = idsEletros.get(posicaoEletro);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        String userId = currentUser.getUid();

        // Consulta no banco global de Sensores para ver se ele existe
        DatabaseReference sensorGlobalRef = FirebaseDatabase.getInstance()
                .getReference("dados")
                .child(idSensorDigitado);

        sensorGlobalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    DatabaseReference userSensorRef = FirebaseDatabase.getInstance()
                            .getReference("Usuarios")
                            .child(userId)
                            .child("dados")
                            .child(idSensorDigitado);

                    userSensorRef.child("idEletroAtivo").setValue(idEletroEscolhido)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(connectSensor.this, "Sensor conectado com sucesso!", Toast.LENGTH_LONG).show();
                                finish(); // volta pra tela anterior
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(connectSensor.this, "Erro ao registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // ERRO: SENSOR NÃO EXISTE NO BANCO
                    Toast.makeText(connectSensor.this, "Sensor não encontrado! Verifique a chave.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(connectSensor.this, "Erro de conexão.", Toast.LENGTH_SHORT).show();
            }
        });
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
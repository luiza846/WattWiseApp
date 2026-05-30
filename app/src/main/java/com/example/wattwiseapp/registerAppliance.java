package com.example.wattwiseapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class registerAppliance extends AppCompatActivity {

    EditText edtNomeEletroReg,edtPotenciaReg, edtDescricaoEletroReg;
    Spinner edtTipoEletroReg, edtEletroComodoReg;
    Button btnRegisterEletroReg;
    TextView txtDisplayInfoReg;

    private List<String> nomesComodos = new ArrayList<>();
    private ArrayAdapter<String> comodoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_appliance);

        //campos
        edtNomeEletroReg = findViewById(R.id.edtNomeEletroReg);
        edtPotenciaReg = findViewById(R.id.edtPotenciaReg);
        edtDescricaoEletroReg = findViewById(R.id.edtDescricaoEletroReg);
        edtTipoEletroReg = findViewById(R.id.edtTipoEletroReg);
        edtEletroComodoReg = findViewById(R.id.edtEletroComodoReg);
        // txt de msg
        txtDisplayInfoReg = findViewById(R.id.txtDisplayInfoReg);
        // botao
        btnRegisterEletroReg = findViewById(R.id.btnRegisterEletroReg);

        // menu
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

        // buscar cômodos no firebase para preencher o SPINNER!

        comodoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomesComodos);
        comodoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtEletroComodoReg.setAdapter(comodoAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference comodosRef = FirebaseDatabase.getInstance()
                    .getReference("Usuarios")
                    .child(userId)
                    .child("Comodos");

            comodosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    nomesComodos.clear();
                    for (DataSnapshot comodoSnap : snapshot.getChildren()) {
                        String nomeComodo = comodoSnap.child("nomeComodo").getValue(String.class);

                        if (nomeComodo != null) {
                            nomesComodos.add(nomeComodo);
                        }
                    }

                    //se o usuário ainda não cadastrou nenhum cômodo
                    if (nomesComodos.isEmpty()) {
                        nomesComodos.add("Nenhum cômodo cadastrado!");
                        Toast.makeText(registerAppliance.this, "Atenção: Cadastre um cômodo primeiro!", Toast.LENGTH_SHORT).show();
                    }

                    comodoAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(registerAppliance.this, "Erro ao carregar cômodos: ", Toast.LENGTH_SHORT).show();
                }
            });

        }


        //realizar cadastro do eletrônico
        btnRegisterEletroReg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                String strNomeEletro = edtNomeEletroReg.getText().toString();
                String strPotencia = edtPotenciaReg.getText().toString();
                String strDescricaoEletro = edtDescricaoEletroReg.getText().toString();
                String strTipoEletro = edtTipoEletroReg.getSelectedItem() != null ? edtTipoEletroReg.getSelectedItem().toString() : "";

                //pegando o cômodo do spinner dinâmico
                String strEletroComodo = edtEletroComodoReg.getSelectedItem() != null ? edtEletroComodoReg.getSelectedItem().toString() : "";


                if(strEletroComodo.equals("Nenhum cômodo cadastrado")) {
                    txtDisplayInfoReg.setText("Você precisa criar um cômodo antes de adicionar um eletrodoméstico!");
                    return;
                }


                if(strNomeEletro.isEmpty() || strPotencia.isEmpty() || strDescricaoEletro.isEmpty() || strTipoEletro.isEmpty() || strEletroComodo.isEmpty()){

                    txtDisplayInfoReg.setText(("Preencha todos os campos!"));

                } else {

                    if (currentUser != null) {
                        String userId = currentUser.getUid();

                        DatabaseReference aparelhoRef = FirebaseDatabase.getInstance()
                                .getReference("Usuarios")
                                .child(userId)
                                .child("Eletronicos")
                                .push();

                        String idGeradoPeloFirebase = aparelhoRef.getKey();


                        Appliance appliance = new Appliance(
                                idGeradoPeloFirebase,
                                strNomeEletro,
                                strTipoEletro,
                                strEletroComodo,
                                strPotencia,
                                strDescricaoEletro
                        );

                        aparelhoRef.setValue(appliance)
                                .addOnCompleteListener( task ->  {
                                    if (task.isSuccessful()) {
                                        txtDisplayInfoReg.setText("Eletrodoméstico registrado com sucesso!");

                                        edtNomeEletroReg.setText("");
                                        edtPotenciaReg.setText("");
                                        edtDescricaoEletroReg.setText("");

                                    } else {
                                        txtDisplayInfoReg.setText("Erro ao salvar: " + task.getException().getLocalizedMessage());
                                    }
                                });

                    } else {
                        txtDisplayInfoReg.setText("Erro: Nenhum usuário autenticado no app.");
                    }


                }

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
            Intent i = new Intent(registerAppliance.this, connectSensor.class);
            startActivity(i);
            return true;
        } else if (id == R.id.comodo) {
            Intent i = new Intent(registerAppliance.this, listRoom.class);
            startActivity(i);
            return true;
        } else if (id == R.id.eletronicos) {
            Intent i = new Intent(registerAppliance.this, listAppliance.class);
            startActivity(i);
            return true;
        } else if (id == R.id.relatorios) {
            Intent i = new Intent(registerAppliance.this, report.class);
            startActivity(i);
            return true;
        } else if (id == R.id.metas) {
            Intent i = new Intent(registerAppliance.this, metas.class);
            startActivity(i);
            return true;
        } else if (id == R.id.configuracao) {
            Intent i = new Intent(registerAppliance.this, settings.class);
            startActivity(i);
            return true;
        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(registerAppliance.this, MainActivity.class);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
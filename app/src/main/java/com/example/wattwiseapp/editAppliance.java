package com.example.wattwiseapp;

import android.os.Bundle;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class editAppliance extends AppCompatActivity {

    private EditText editNome, editPotencia, editDescricao;
    private Spinner spinnerTipo, spinnerComodo;
    private DatabaseReference eletroRef;
    private String idEletro;

    // Para controlar a seleção do Spinner de cômodos
    private List<String> nomesComodos = new ArrayList<>();
    private ArrayAdapter<String> comodoAdapter;
    private String comodoAtual; // cômodo que veio pelo Intent



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_appliance);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Vincula os campos do layout
        editNome      = findViewById(R.id.editNomeEletro);
        editPotencia  = findViewById(R.id.editPotenciaEletro);
        editDescricao = findViewById(R.id.editDescricaoEletro);
        spinnerTipo   = findViewById(R.id.editTipoEletro);
        spinnerComodo = findViewById(R.id.editComodoEletro);

        // Recebe os dados passados pela listAppliance
        idEletro             = getIntent().getStringExtra("idEletro");
        String nome          = getIntent().getStringExtra("nomeEletro");
        String tipo          = getIntent().getStringExtra("tipoEletro");
        comodoAtual          = getIntent().getStringExtra("comodoEletro");
        String potencia      = getIntent().getStringExtra("potenciaEletro");
        String descricao     = getIntent().getStringExtra("descricaoEletro");

        // Preenche os campos de texto
        editNome.setText(nome);
        editPotencia.setText(potencia);
        editDescricao.setText(descricao);

        // Pré-seleciona o tipo no Spinner estático (igual ao cadastro)
        if (tipo != null) {
            ArrayAdapter tipoAdapter = (ArrayAdapter) spinnerTipo.getAdapter();
            if (tipoAdapter != null) {
                int pos = tipoAdapter.getPosition(tipo);
                if (pos >= 0) spinnerTipo.setSelection(pos);
            }
        }

        // Configura referências do Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String userId = currentUser.getUid();

        eletroRef = FirebaseDatabase.getInstance()
                .getReference("Usuarios")
                .child(userId)
                .child("Eletronicos") // ← mesmo nome que você usa na listAppliance
                .child(idEletro);

        // Carrega os cômodos do Firebase dinamicamente
        DatabaseReference comodosRef = FirebaseDatabase.getInstance()
                .getReference("Usuarios")
                .child(userId)
                .child("Comodos");

        comodoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomesComodos);
        comodoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerComodo.setAdapter(comodoAdapter);

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
                comodoAdapter.notifyDataSetChanged();

                // Pré-seleciona o cômodo atual do eletrodoméstico
                if (comodoAtual != null) {
                    int pos = nomesComodos.indexOf(comodoAtual);
                    if (pos >= 0) spinnerComodo.setSelection(pos);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(editAppliance.this,
                        "Erro ao carregar cômodos: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Botão salvar
        Button btnSalvar = findViewById(R.id.btnSalvarEdicao);
        btnSalvar.setOnClickListener(v -> salvarEdicao());
    }

    private void salvarEdicao() {
        String nome      = editNome.getText().toString().trim();
        String tipo      = spinnerTipo.getSelectedItem().toString();
        String comodo    = spinnerComodo.getSelectedItem() != null
                ? spinnerComodo.getSelectedItem().toString() : "";
        String potencia  = editPotencia.getText().toString().trim();
        String descricao = editDescricao.getText().toString().trim();

        // Validação básica
        if (nome.isEmpty() || potencia.isEmpty()) {
            Toast.makeText(this, "Preencha pelo menos o nome e a potência!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("nomeEletro",      nome);
        updates.put("tipoEletro",      tipo);
        updates.put("comodoEletro",    comodo);
        updates.put("potenciaEletro",  potencia);
        updates.put("descricaoEletro", descricao);

        eletroRef.updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    finish(); // volta para a lista
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
package com.example.wattwiseapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class editRoom extends AppCompatActivity {

    private EditText editNome, editQtdTomadas, editDescricao;
    private Spinner spinnerTipo;
    private DatabaseReference comodoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Vincula os campos do layout
        editNome       = findViewById(R.id.editNomeComodo);
        spinnerTipo    = findViewById(R.id.editTipoComodo);
        editQtdTomadas = findViewById(R.id.editQtdTomadasComodo);
        editDescricao  = findViewById(R.id.editDescricaoComodo);

        // Recebe os dados enviados pela listRoom
        String idComodo   = getIntent().getStringExtra("idComodo");
        String nome       = getIntent().getStringExtra("nomeComodo");
        String tipo       = getIntent().getStringExtra("tipoComodo");
        String qtdTomadas = getIntent().getStringExtra("qtdTomadas");
        String descricao  = getIntent().getStringExtra("descricaoComodo");

        // Preenche os campos de texto
        editNome.setText(nome);
        editQtdTomadas.setText(qtdTomadas);
        editDescricao.setText(descricao);

        // Pré-seleciona o tipo no Spinner
        if (tipo != null) {
            ArrayAdapter tipoAdapter = (ArrayAdapter) spinnerTipo.getAdapter();
            if (tipoAdapter != null) {
                int pos = tipoAdapter.getPosition(tipo);
                if (pos >= 0) spinnerTipo.setSelection(pos);
            }
        }

        // Referência ao nó do cômodo no Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        comodoRef = FirebaseDatabase.getInstance()
                .getReference("Usuarios")
                .child(currentUser.getUid())
                .child("Comodos")
                .child(idComodo);

        // Botão salvar
        Button btnSalvar = findViewById(R.id.btnSalvarEdicaoComodo);
        btnSalvar.setOnClickListener(v -> salvarEdicao());
    }

    private void salvarEdicao() {
        String nome       = editNome.getText().toString().trim();
        String tipo       = spinnerTipo.getSelectedItem().toString();
        String qtdTomadas = editQtdTomadas.getText().toString().trim();
        String descricao  = editDescricao.getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(this, "O nome do cômodo é obrigatório!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("nomeComodo",  nome);
        updates.put("tipoComodo",  tipo);
        updates.put("qtdTomadas",  qtdTomadas);
        updates.put("descricao",   descricao);

        comodoRef.updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cômodo salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    finish(); // volta para a lista
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
package com.example.wattwiseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
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

        // menu
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Garante que o título do app fique em branco na Action Bar de suporte
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


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

    // menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sensor) {
            Intent i = new Intent(editRoom.this, listSensor.class);
            startActivity(i);
            return true;
        } else if (id == R.id.comodo) {
            Intent i = new Intent(editRoom.this, listRoom.class);
            startActivity(i);
            return true;
        } else if (id == R.id.eletronicos) {
            Intent i = new Intent(editRoom.this, listAppliance.class);
            startActivity(i);
            return true;
        } else if (id == R.id.relatorios) {
            Intent i = new Intent(editRoom.this, report.class);
            startActivity(i);
            return true;
        } else if (id == R.id.metas) {
            Intent i = new Intent(editRoom.this, Dashboard.class);
            startActivity(i);
            return true;
        } else if (id == R.id.configuracao) {
            Intent i = new Intent(editRoom.this, settings.class);
            startActivity(i);
            return true;
        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(editRoom.this, MainActivity.class);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
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
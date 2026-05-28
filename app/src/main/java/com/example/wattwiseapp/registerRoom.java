package com.example.wattwiseapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class registerRoom extends AppCompatActivity {

    EditText edtNomeComodoReg, edtQtdTomadasReg, edtDescricaoReg;
    Spinner edtTipoComodoReg;
    Button btnRegisterRoomReg;
    TextView txtDisplayInfoReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_room);

        //campos
        edtNomeComodoReg = findViewById(R.id.edtNomeComodoReg);
        edtTipoComodoReg = findViewById(R.id.edtTipoComodoReg);
        edtQtdTomadasReg = findViewById(R.id.edtQtdTomadasReg);
        edtDescricaoReg = findViewById(R.id.edtDescricaoReg);
        // txt de msg
        txtDisplayInfoReg = findViewById(R.id.txtDisplayInfoReg);
        // botao
        btnRegisterRoomReg = findViewById(R.id.btnRegisterRoomReg);

        // menu
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // realizar o cadastro do comodo
        btnRegisterRoomReg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                String strNomeComodo = edtNomeComodoReg.getText().toString();
                String strTipoComodo = edtTipoComodoReg.getSelectedItem().toString();
                String strQtdTomadas = edtQtdTomadasReg.getText().toString();
                String strDescricao = edtDescricaoReg.getText().toString();

                if(strNomeComodo.isEmpty() || strTipoComodo.isEmpty() || strQtdTomadas.isEmpty() || strDescricao.isEmpty()){

                    txtDisplayInfoReg.setText(("Preencha todos os campos!"));

                } else {

                    com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();

                    if (currentUser != null) {
                        String userId = currentUser.getUid();

                        com.google.firebase.database.DatabaseReference comodoRef = com.google.firebase.database.FirebaseDatabase.getInstance()
                                .getReference("Usuarios")
                                .child(userId)
                                .child("Comodos")
                                .push();

                        String idGeradoPeloFirebase = comodoRef.getKey();


                        Room room = new Room(
                                idGeradoPeloFirebase,
                                strNomeComodo,
                                strTipoComodo,
                                strQtdTomadas,
                                strDescricao
                        );

                        comodoRef.setValue(room)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        txtDisplayInfoReg.setText("Cômodo registrado com sucesso!");

                                        edtNomeComodoReg.setText("");
                                        edtQtdTomadasReg.setText("");
                                        edtDescricaoReg.setText("");

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
            Intent i = new Intent(registerRoom.this, connectSensor.class);
            startActivity(i);
            return true;
        } else if (id == R.id.comodo) {
            Intent i = new Intent(registerRoom.this, listRoom.class);
            startActivity(i);
            return true;
        } else if (id == R.id.eletronicos) {
            Intent i = new Intent(registerRoom.this, listAppliance.class);
            startActivity(i);
            return true;
        } else if (id == R.id.relatorios) {
            Intent i = new Intent(registerRoom.this, report.class);
            startActivity(i);
            return true;
        } else if (id == R.id.metas) {
            Intent i = new Intent(registerRoom.this, metas.class);
            startActivity(i);
            return true;
        } else if (id == R.id.configuracao) {
            Intent i = new Intent(registerRoom.this, settings.class);
            startActivity(i);
            return true;
        } else if (id == R.id.logout) {
            Intent i = new Intent(registerRoom.this, MainActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
package com.example.wattwiseapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

                    txtDisplayInfoReg.setText(("All fields required"));

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
                                        txtDisplayInfoReg.setText("Room registered sucessfully!");

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
}
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

public class registerAppliance extends AppCompatActivity {

    EditText edtNomeEletroReg,edtPotenciaReg, edtDescricaoEletroReg;
    Spinner edtTipoEletroReg, edtEletroComodoReg;
    Button btnRegisterEletroReg;
    TextView txtDisplayInfoReg;

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // realizar o cadastro do eletronico
        btnRegisterEletroReg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                String strNomeEletro = edtNomeEletroReg.getText().toString();
                String strPotencia = edtPotenciaReg.getText().toString();
                String strDescricaoEletro = edtDescricaoEletroReg.getText().toString();
                String strTipoEletro = edtTipoEletroReg.getSelectedItem().toString();
                String strEletroComodo = edtEletroComodoReg.getSelectedItem().toString();

                if(strNomeEletro.isEmpty() && strPotencia.isEmpty() && strDescricaoEletro.isEmpty() && strTipoEletro.isEmpty() && strEletroComodo.isEmpty()){

                    txtDisplayInfoReg.setText(("All fields required"));

                } else {

                    // chamar a classe
                    Appliance appliance = new Appliance(
                            0,
                            strNomeEletro,
                            strPotencia,
                            strDescricaoEletro,
                            strTipoEletro,
                            strEletroComodo
                    );



                    txtDisplayInfoReg.setText("Appliance registered successfully!");

                }

            }
        });

    }
}
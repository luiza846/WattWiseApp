package com.example.wattwiseapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText edtEmailAddressLog, edtPasswordLog;
    Button btnLoginLog, btnRegisterLog;
    TextView txtDisplayInfoLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        edtEmailAddressLog = findViewById(R.id.edtEmailAddressLog);
        edtPasswordLog = findViewById(R.id.edtPasswordLog);
        btnLoginLog = findViewById(R.id.btnLoginLog);
        btnRegisterLog = findViewById(R.id.btnRegisterLog);
        txtDisplayInfoLog = findViewById(R.id.txtDisplayInfoLog);

        btnRegisterLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this,register.class);
                startActivity(i);

            }
        });

        btnLoginLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // trim(); remove o espaço branco
                String email = edtEmailAddressLog.getText().toString().trim();
                String password = edtPasswordLog.getText().toString().trim();

                // Coloquei o || para barrar se qualquer um dos campos estiver vazio!
                if(email.isEmpty() || password.isEmpty()){
                    txtDisplayInfoLog.setText("Preencha os campos vazios");
                    txtDisplayInfoLog.setTextColor(Color.RED);
                    return;
                }

                txtDisplayInfoLog.setText("Autenticando...");
                txtDisplayInfoLog.setTextColor(Color.GRAY);

                com.google.firebase.auth.FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {

                                txtDisplayInfoLog.setText("Login sucessfully!");
                                txtDisplayInfoLog.setTextColor(Color.GRAY);

                                //vai pra tela principal
                                Intent i = new Intent(MainActivity.this, Dashboard.class);
                                startActivity(i);
                                finish();

                            } else {
                                txtDisplayInfoLog.setText("Email ou senha incorreta!");
                                txtDisplayInfoLog.setTextColor(Color.RED);
                            }
                        });
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
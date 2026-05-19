package com.example.wattwiseapp;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class register extends AppCompatActivity {

    EditText edtFullNameReg, edtEmailAddressReg, edtPasswordReg, edtPhoneNumberReg, edtDateofBirth, edtBioReg;
    Button btnRegisterReg, btnLoginReg;
    TextView txtDisplayInfoReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        edtEmailAddressReg = findViewById(R.id.edtEmailAddressReg);
        edtFullNameReg = findViewById(R.id.edtFullNameReg);
        edtPasswordReg = findViewById(R.id.edtPasswordReg);
        edtPhoneNumberReg = findViewById(R.id.edtPhoneNumberReg);
        edtBioReg = findViewById(R.id.edtBioReg);
        edtDateofBirth = findViewById(R.id.edtDateofBirth);
        txtDisplayInfoReg = findViewById(R.id.txtDisplayInfoReg);

        btnRegisterReg = findViewById(R.id.btnRegisterReg);
        btnLoginReg = findViewById(R.id.btnLoginReg);

        btnLoginReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(register.this, MainActivity.class);
                startActivity(i);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnRegisterReg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                String strFullname = edtFullNameReg.getText().toString();
                String strEmailAddress = edtEmailAddressReg.getText().toString();
                String strPassword = edtPasswordReg.getText().toString();
                String strPhoneNumber = edtPhoneNumberReg.getText().toString();
                String strBio = edtBioReg.getText().toString();
                String strDOB = edtDateofBirth.getText().toString();


                if(strFullname.isEmpty() || strEmailAddress.isEmpty() || strPassword.isEmpty() || strPhoneNumber.isEmpty() || strBio.isEmpty() || strDOB.isEmpty()){

                    txtDisplayInfoReg.setText("All fields required");

                } else {

                    txtDisplayInfoReg.setText("Registrando no Firebase...");


                    com.google.firebase.auth.FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(strEmailAddress, strPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // conta foi criada no console do Firebase.
                                    txtDisplayInfoReg.setText("User registered successfully!");

                                    // Redireciona o usuário para a tela principal (opcional)
                                    Intent i = new Intent(register.this, MainActivity.class);
                                    startActivity(i);
                                    finish();

                                } else {
                                    txtDisplayInfoReg.setText("Erro: " + task.getException().getLocalizedMessage());
                                }
                            });
                }
            }
        });

    }
}
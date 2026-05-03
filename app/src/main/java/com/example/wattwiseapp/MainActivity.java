package com.example.wattwiseapp;

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

                if(email.isEmpty() && password.isEmpty()){
                    txtDisplayInfoLog.setText("Fill all Fields");
                    return;
                }

                //conectar com o arquivo db
                dbConnect db = new dbConnect(MainActivity.this);
                //acessar o metodo checkLogin
                boolean success = db.checkLogin(email, password);

                if (success){
                    txtDisplayInfoLog.setText("Login successfully!");

                    //ir pra tela prncipal
                    Intent i = new Intent(MainActivity.this,Dashboard.class);
                    startActivity(i);

                } else {
                    txtDisplayInfoLog.setText("Invalid email or password");
                }

            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
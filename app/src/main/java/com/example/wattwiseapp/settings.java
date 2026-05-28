package com.example.wattwiseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class settings extends AppCompatActivity {

    Button btnLogOut, btnChangePassword;
    TextView txtDisplayNome, txtDisplayEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // botao
        btnLogOut = findViewById(R.id.btnLogOut);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        // chamar o metodo
        carregarDadosUsuario();

        // display
        txtDisplayNome = findViewById(R.id.txtDisplayNome);
        txtDisplayEmail = findViewById(R.id.txtDisplayEmail);


        // menu
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // btn logout
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(settings.this, MainActivity.class);
                startActivity(i);
            }
        });

        // btn trocar senha
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 1. Infla o layout XML da tela flutuante
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);

                // 2. Cria e configura o AlertDialog passando o tema customizado
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(settings.this, R.style.AlertDialogCustom);
                builder.setView(dialogView);

                // 3. Adiciona os botões de ação na parte inferior do pop-up
                builder.setPositiveButton("Alterar", (dialog, which) -> {
                    dialog.dismiss();
                });

                builder.setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                });

                // 4. Mostra a tela flutuante na tela
                androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.show();

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
            Intent i = new Intent(settings.this, connectSensor.class);
            startActivity(i);
            return true;
        } else if (id == R.id.comodo) {
            Intent i = new Intent(settings.this, listRoom.class);
            startActivity(i);
            return true;
        } else if (id == R.id.eletronicos) {
            Intent i = new Intent(settings.this, listAppliance.class);
            startActivity(i);
            return true;
        } else if (id == R.id.relatorios) {
            Intent i = new Intent(settings.this, report.class);
            startActivity(i);
            return true;
        } else if (id == R.id.metas) {
            Intent i = new Intent(settings.this, metas.class);
            startActivity(i);
            return true;
        } else if (id == R.id.configuracao) {
            Intent i = new Intent(settings.this, settings.class);
            startActivity(i);
            return true;
        } else if (id == R.id.logout) {
            Intent i = new Intent(settings.this, MainActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    private void carregarDadosUsuario() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Usuarios")
                .child(userId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String nome = snapshot.child("fullname").getValue(String.class);
                String email = user.getEmail();

                txtDisplayNome.setText(nome != null ? nome : "Nome não informado");
                txtDisplayEmail.setText(email != null ? email : "Email não informado");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(settings.this,
                        "Erro ao carregar dados: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


}
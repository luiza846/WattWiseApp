package com.example.wattwiseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DatabaseError;

public class listAppliance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_appliance);
        // menu
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // listagem

        com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            com.google.firebase.database.DatabaseReference eletrosRef = com.google.firebase.database.FirebaseDatabase.getInstance()
                    .getReference("Usuarios")
                    .child(userId)
                    .child("Eletronicos");


            eletrosRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {




                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    android.util.Log.e("FirebaseError", "Erro ao buscar Eletrodomésticos: " + error.getMessage());

                }


            });

        }





    }

    // menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reg_appliance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sensor) {
            Toast.makeText(this, "Clicou em Sensor", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.comodo) {
            Intent i = new Intent(listAppliance.this, listRoom.class);
            startActivity(i);
            return true;
        } else if (id == R.id.eletronicos) {
            Intent i = new Intent(listAppliance.this, listAppliance.class);
            startActivity(i);
            return true;
        } else if (id == R.id.relatorios) {
            Intent i = new Intent(listAppliance.this, report.class);
            startActivity(i);
            return true;
        } else if (id == R.id.metas) {
            Intent i = new Intent(listAppliance.this, metas.class);
            startActivity(i);
            return true;
        } else if (id == R.id.configuracao) {
            Intent i = new Intent(listAppliance.this, settings.class);
            startActivity(i);
            return true;
        } else if (id == R.id.logout) {
            Intent i = new Intent(listAppliance.this, MainActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.menu_cadastrar) {
            Intent i = new Intent(listAppliance.this, registerAppliance.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
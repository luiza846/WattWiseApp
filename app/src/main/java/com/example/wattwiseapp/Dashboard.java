package com.example.wattwiseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.wattwiseapp.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class Dashboard extends AppCompatActivity {

    Button btnRegisterRoom, btnRegisterAppliance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        // menu
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnRegisterRoom = findViewById(R.id.btnRegisterRoom);
        btnRegisterAppliance = findViewById(R.id.btnRegisterAppliance);

        btnRegisterRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, registerRoom.class);
                startActivity(i);
            }
        });

        btnRegisterAppliance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, registerAppliance.class);
                startActivity(i);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
            Toast.makeText(this, "Clicou em Sensor", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.comodo) {
            Intent i = new Intent(Dashboard.this, registerRoom.class);
            startActivity(i);
            return true;
        } else if (id == R.id.eletronicos) {
            Intent i = new Intent(Dashboard.this, registerAppliance.class);
            startActivity(i);
            return true;
        } else if (id == R.id.relatorios) {
            Toast.makeText(this, "Clicou em Sair", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.metas) {
            Toast.makeText(this, "Clicou em Sair", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.configuracao) {
            Toast.makeText(this, "Clicou em Sair", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.logout) {
            Toast.makeText(this, "Clicou em Sair", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
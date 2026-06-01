package com.example.wattwiseapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class listSensor extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SensorAdapter adapter;
    private List<Sensor> listaSensores;
    private LinearLayout layoutVazio;
    TextView txtVazio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_sensor);

        layoutVazio = findViewById(R.id.layoutVazio);

        txtVazio = findViewById(R.id.txtVazio);
        layoutVazio.setVisibility(View.GONE);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        listaSensores = new ArrayList<>();
        recyclerView = findViewById(R.id.listaSensores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SensorAdapter(this, listaSensores);
        recyclerView.setAdapter(adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference sensoresRef = FirebaseDatabase.getInstance()
                    .getReference("Usuarios")
                    .child(userId)
                    .child("dados");

            sensoresRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listaSensores.clear();

                    for (DataSnapshot sensorSnapshot : snapshot.getChildren()) {
                        Sensor sensor = sensorSnapshot.getValue(Sensor.class);
                        if (sensor != null) {
                            sensor.setIdSensor(sensorSnapshot.getKey());
                            listaSensores.add(sensor);
                        }
                    }
                    adapter.notifyDataSetChanged();


                    // se nao tiver cadastrado nenhum sensor
                    if (listaSensores.isEmpty()) {
                        layoutVazio.setVisibility(View.VISIBLE);
                        txtVazio.setText(
                                "Nenhum sensor disponível " +
                                        " Conecte um para prosseguir"
                        );
                    } else {
                        txtVazio.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", error.getMessage());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sensor, menu);
        return true;
    }

    // menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sensor) {
            Intent i = new Intent(listSensor.this, listSensor.class);
            startActivity(i);
            return true;
        } else if (id == R.id.comodo) {
            Intent i = new Intent(listSensor.this, listRoom.class);
            startActivity(i);
            return true;
        } else if (id == R.id.eletronicos) {
            Intent i = new Intent(listSensor.this, listAppliance.class);
            startActivity(i);
            return true;
        } else if (id == R.id.relatorios) {
            Intent i = new Intent(listSensor.this, report.class);
            startActivity(i);
            return true;
        } else if (id == R.id.metas) {
            Intent i = new Intent(listSensor.this, Dashboard.class);
            startActivity(i);
            return true;
        } else if (id == R.id.configuracao) {
            Intent i = new Intent(listSensor.this, settings.class);
            startActivity(i);
            return true;
        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(listSensor.this, MainActivity.class);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        } else if (id == R.id.menu_conectar) {
            Intent i = new Intent(listSensor.this, connectSensor.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
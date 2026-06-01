package com.example.wattwiseapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends AppCompatActivity {

    //grafico
    private LineChart lineChart;
    private PieChart pieChartConsumo;
    TextView displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        // menu
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Garante que o título do app fique em branco na Action Bar de suporte
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        carregarDadosUsuario();
        displayName = findViewById(R.id.displayName);

        //grafico
        lineChart = findViewById(R.id.energyChart);
        configurarGrafico();

        // griafico (pie)
        pieChartConsumo = findViewById(R.id.pieChartConsumo);
        configurarGraficoRosca();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sensor) {
            Intent i = new Intent(Dashboard.this, listSensor.class);
            startActivity(i);
            return true;
        } else if (id == R.id.comodo) {
            Intent i = new Intent(Dashboard.this, listRoom.class);
            startActivity(i);
            return true;
        } else if (id == R.id.eletronicos) {
            Intent i = new Intent(Dashboard.this, listAppliance.class);
            startActivity(i);
            return true;
        } else if (id == R.id.relatorios) {
            Intent i = new Intent(Dashboard.this, report.class);
            startActivity(i);
            return true;
        } else if (id == R.id.metas) {
            Intent i = new Intent(Dashboard.this, Dashboard.class);
            startActivity(i);
            return true;
        } else if (id == R.id.configuracao) {
            Intent i = new Intent(Dashboard.this, settings.class);
            startActivity(i);
            return true;
        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(Dashboard.this, MainActivity.class);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    // grafico
    private void configurarGrafico() {
        List<Entry> entradas = new ArrayList<>();
        entradas.add(new Entry(0, 0.5f));
        entradas.add(new Entry(6, 1.2f));
        entradas.add(new Entry(12, 3.5f));
        entradas.add(new Entry(18, 4.2f));
        entradas.add(new Entry(22, 2.0f));

        LineDataSet dataSet = new LineDataSet(entradas, "Consumo de Energia");
        dataSet.setColor(Color.parseColor("#3498DB"));
        dataSet.setCircleColor(Color.parseColor("#2C3E50"));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(5f);

        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            // formatar o valor pra kwh
            @Override
            public String getFormattedValue(float value) {
                return value + " kWh";
            }
        });

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // horas
        com.github.mikephil.charting.components.XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(24f);
        xAxis.setGranularity(2f); // Mostra de 2 em 2 horas (ex: 0h, 2h, 4h...) para não embolar
        xAxis.setDrawGridLines(true);

        // formatar o valor pra hrs
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ((int) value) + "h";
            }
        });

        // kwh
        com.github.mikephil.charting.components.YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f);

        // formatar o valor pra kwh
        yAxisLeft.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + " kWh";
            }
        });


        lineChart.getAxisRight().setEnabled(false);


        lineChart.getDescription().setEnabled(false);

        lineChart.animateX(1200);
        lineChart.invalidate();
    }
    private void configurarGraficoRosca() {

        // Configurações básicas do gráfico (rosca)
        pieChartConsumo.setDrawHoleEnabled(true);
        pieChartConsumo.setHoleRadius(55f);
        pieChartConsumo.setTransparentCircleRadius(60f);
        pieChartConsumo.setCenterText("Consumo por\nCômodo");
        pieChartConsumo.setCenterTextSize(16f);
        pieChartConsumo.setCenterTextColor(Color.DKGRAY);
        pieChartConsumo.getDescription().setEnabled(false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();

        DatabaseReference comodosRef = FirebaseDatabase.getInstance()
                .getReference("Usuarios")
                .child(userId)
                .child("Comodos");

        comodosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<PieEntry> entries = new ArrayList<>();

                for (DataSnapshot comodoSnap : snapshot.getChildren()) {

                    String nomeComodo = comodoSnap
                            .child("nomeComodo")
                            .getValue(String.class);

                    if (nomeComodo == null) continue;

                    // 🔹 Consumo fictício (0.30 a 2.50 kWh)
                    float consumoFake = 0.3f + (float) (Math.random() * 2.2f);

                    entries.add(new PieEntry(consumoFake, nomeComodo));
                }

                // Caso não existam cômodos cadastrados
                if (entries.isEmpty()) {
                    pieChartConsumo.setCenterText("Nenhum cômodo\ncadastrado");
                    pieChartConsumo.invalidate();
                    return;
                }

                // DataSet (visual das fatias)
                PieDataSet dataSet = new PieDataSet(entries, "Consumo (kWh)");
                dataSet.setSliceSpace(3f);
                dataSet.setValueTextSize(12f);
                dataSet.setValueTextColor(Color.WHITE);
                dataSet.setColors(
                        com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS
                );

                // 🔥 Formatação dos valores em kWh
                dataSet.setValueFormatter(
                        new com.github.mikephil.charting.formatter.ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                return String.format(
                                        java.util.Locale.US,
                                        "%.2f kWh",
                                        value
                                );
                            }
                        }
                );

                // Aplica os dados no gráfico
                PieData data = new PieData(dataSet);
                pieChartConsumo.setData(data);
                pieChartConsumo.invalidate();
                pieChartConsumo.animateY(1200);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        Dashboard.this,
                        "Erro ao carregar gráfico",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // carregar o nome do usuário
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

                if (nome != null) {
                    displayName.setText("Olá, " + nome + "!");
                } else {
                    displayName.setText("Olá!");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this,
                        "Erro ao carregar dados: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    }
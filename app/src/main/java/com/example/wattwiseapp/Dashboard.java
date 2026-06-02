package com.example.wattwiseapp;

import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
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
    private TextView displayConsumoTotal, displayCustoEstimado, displayPico, displayHoraPico, displayMenorConsumo, displayHoraMenor;

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

        displayConsumoTotal = findViewById(R.id.displayConsumoTotal);
        displayCustoEstimado = findViewById(R.id.displayCustoEstimado);
        displayPico = findViewById(R.id.displayPico);
        displayHoraPico = findViewById(R.id.displayHoraPico);
        displayMenorConsumo = findViewById(R.id.displayMenorConsumo);
        displayHoraMenor = findViewById(R.id.displayHoraMenor);

        calcularConsumoECusto();

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

    // exibir infos de consumo de energia (total e custo)
    private void calcularConsumoECusto() {

        DatabaseReference historicoRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("historico_sensores");

        historicoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                float consumoTotal = 0f;

                // consumo por hora (0–23)
                Map<Integer, Float> consumoPorHora = new HashMap<>();

                for (DataSnapshot sensorSnap : snapshot.getChildren()) {

                    for (DataSnapshot leituraSnap : sensorSnap.getChildren()) {

                        Object energiaObj = leituraSnap.child("energia").getValue();
                        String horaStr = leituraSnap.child("hora").getValue(String.class);

                        if (energiaObj == null || horaStr == null) continue;

                        try {
                            String energiaLimpa = energiaObj.toString()
                                    .replaceAll("[^\\d.]", "");

                            float energia = Float.parseFloat(energiaLimpa);

                            consumoTotal += energia;

                            // pega só a hora (ex: "13:05:36" -> 13)
                            int hora = Integer.parseInt(horaStr.split(":")[0]);

                            float atual = consumoPorHora.containsKey(hora)
                                    ? consumoPorHora.get(hora)
                                    : 0f;

                            consumoPorHora.put(hora, atual + energia);

                        } catch (Exception e) {
                            // ignora erro
                        }
                    }
                }

                final float VALOR_KWH = 0.92f;
                float custoEstimado = consumoTotal * VALOR_KWH;

                // TOTAL
                displayConsumoTotal.setText(
                        String.format(Locale.getDefault(),
                                "%.2f kWh",
                                consumoTotal)
                );

                displayCustoEstimado.setText(
                        String.format(Locale.getDefault(),
                                "R$ %.2f",
                                custoEstimado)
                );

                // 🔥 PICO E MENOR (por hora)
                float maior = Float.MIN_VALUE;
                float menor = Float.MAX_VALUE;

                int horaPico = -1;
                int horaMenor = -1;

                for (Map.Entry<Integer, Float> entry : consumoPorHora.entrySet()) {

                    int h = entry.getKey();
                    float valor = entry.getValue();

                    if (valor > maior) {
                        maior = valor;
                        horaPico = h;
                    }

                    if (valor < menor && valor > 0) {
                        menor = valor;
                        horaMenor = h;
                    }
                }

                // PICO
                displayPico.setText(
                        String.format(Locale.getDefault(),
                                "%.2f kWh",
                                maior == Float.MIN_VALUE ? 0f : maior)
                );

                displayHoraPico.setText(
                        horaPico >= 0 ? "" + horaPico + "h" : ""
                );

                // MENOR
                displayMenorConsumo.setText(
                        String.format(Locale.getDefault(),
                                "%.2f kWh",
                                menor == Float.MAX_VALUE ? 0f : menor)
                );

                displayHoraMenor.setText(
                        horaMenor >= 0 ? "" + horaMenor + "h" : ""
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        Dashboard.this,
                        "Erro ao calcular consumo",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // grafico
    private void configurarGrafico() {

        DatabaseReference historicoRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("historico_sensores");

        historicoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // chave = "data hora"  → consumo total
                Map<String, Float> consumoPorHora = new HashMap<>();

                SimpleDateFormat sdf =
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                for (DataSnapshot sensorSnap : snapshot.getChildren()) {

                    for (DataSnapshot leituraSnap : sensorSnap.getChildren()) {

                        String data = leituraSnap.child("data").getValue(String.class);
                        String hora = leituraSnap.child("hora").getValue(String.class);
                        Object energiaObj = leituraSnap.child("energia").getValue();

                        if (data == null || hora == null || energiaObj == null) continue;

                        float energia;

                        try {
                            String energiaLimpa = energiaObj.toString()
                                    .replaceAll("[^\\d.]", "");
                            energia = Float.parseFloat(energiaLimpa);
                        } catch (Exception e) {
                            continue;
                        }

                        // chave única por hora (ex: 01/06/2026 13)
                        String horaCompleta = data + " " + hora;

                        float totalAtual = consumoPorHora.containsKey(horaCompleta)
                                ? consumoPorHora.get(horaCompleta)
                                : 0f;

                        consumoPorHora.put(horaCompleta, totalAtual + energia);
                    }
                }

                if (consumoPorHora.isEmpty()) {
                    lineChart.clear();
                    lineChart.setNoDataText("Sem dados de consumo");
                    lineChart.invalidate();
                    return;
                }

                // ordenar por data/hora
                List<Map.Entry<String, Float>> listaOrdenada =
                        new ArrayList<>(consumoPorHora.entrySet());

                Collections.sort(listaOrdenada, (a, b) -> {
                    try {
                        Date d1 = sdf.parse(a.getKey());
                        Date d2 = sdf.parse(b.getKey());
                        return d1.compareTo(d2);
                    } catch (ParseException e) {
                        return 0;
                    }
                });

                // montar entradas do gráfico
                List<Entry> entradas = new ArrayList<>();
                int index = 0;

                for (Map.Entry<String, Float> item : listaOrdenada) {
                    entradas.add(new Entry(index, item.getValue()));
                    index++;
                }

                LineDataSet dataSet =
                        new LineDataSet(entradas, "Consumo total por hora (kWh)");

                dataSet.setColor(Color.parseColor("#3498DB"));
                dataSet.setCircleColor(Color.parseColor("#2C3E50"));
                dataSet.setLineWidth(2.5f);
                dataSet.setCircleRadius(5f);
                dataSet.setValueTextSize(10f);

                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.format(
                                Locale.getDefault(),
                                "%.2f kWh",
                                value
                        );
                    }
                });

                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);

                // eixo X = data + hora
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setDrawGridLines(false);
                SimpleDateFormat sdfEntrada =
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                SimpleDateFormat sdfHora =
                        new SimpleDateFormat("HH'h'", Locale.getDefault());

                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int i = (int) value;

                        if (i < 0 || i >= listaOrdenada.size()) return "";

                        try {
                            Date date = sdfEntrada.parse(listaOrdenada.get(i).getKey());
                            return sdfHora.format(date); // ex: 20h, 21h
                        } catch (ParseException e) {
                            return "";
                        }
                    }
                });

                // eixo Y = kWh
                YAxis yAxisLeft = lineChart.getAxisLeft();
                yAxisLeft.setAxisMinimum(0f);
                yAxisLeft.setValueFormatter(new ValueFormatter() {
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        Dashboard.this,
                        "Erro ao carregar consumo",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
    private void configurarGraficoRosca() {

        pieChartConsumo.setDrawHoleEnabled(true);
        pieChartConsumo.setHoleRadius(55f);
        pieChartConsumo.setTransparentCircleRadius(60f);
        pieChartConsumo.setCenterText("Consumo por\nCômodo (24h)");
        pieChartConsumo.setCenterTextSize(16f);
        pieChartConsumo.setCenterTextColor(Color.DKGRAY);
        pieChartConsumo.getDescription().setEnabled(false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Float> consumoPorComodo = new HashMap<>();

                DataSnapshot userSnap = snapshot.child("Usuarios").child(userId);
                DataSnapshot historicoSnap = snapshot.child("historico_sensores");


                //  MAPEAR SENSOR -> ELETRO

                Map<String, String> sensorParaEletro = new HashMap<>();

                for (DataSnapshot sensorSnap : userSnap.child("dados").getChildren()) {

                    String sensorId = sensorSnap.getKey();
                    String idEletro = sensorSnap.child("idEletroAtivo").getValue(String.class);

                    if (sensorId != null && idEletro != null) {
                        sensorParaEletro.put(sensorId, idEletro);
                    }
                }


                // MAPEAR ELETRO -> CÔMODO

                Map<String, String> eletroParaComodo = new HashMap<>();

                for (DataSnapshot eletroSnap : userSnap.child("Eletronicos").getChildren()) {

                    String idEletro = eletroSnap.getKey();

                    String nomeComodo = eletroSnap.child("comodoEletro")
                            .getValue(String.class);

                    if (idEletro != null && nomeComodo != null) {
                        eletroParaComodo.put(idEletro, nomeComodo);
                    }
                }


                // FILTRO 24H

                SimpleDateFormat sdf =
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                long agora = System.currentTimeMillis();
                long limite24h = agora - (24 * 60 * 60 * 1000);


                // PERCORRER TODOS OS SENSORES

                for (DataSnapshot sensorSnap : userSnap.child("dados").getChildren()) {

                    String sensorId = sensorSnap.getKey();
                    String idEletro = sensorSnap.child("idEletroAtivo").getValue(String.class);

                    if (sensorId == null || idEletro == null) continue;

                    String nomeComodo = eletroParaComodo.get(idEletro);
                    if (nomeComodo == null) continue;

                    DataSnapshot sensorHistorico =
                            historicoSnap.child(sensorId);

                    float somaEnergia = 0f;

                    for (DataSnapshot leitura : sensorHistorico.getChildren()) {

                        String energiaStr = leitura.child("energia").getValue(String.class);
                        String data = leitura.child("data").getValue(String.class);
                        String hora = leitura.child("hora").getValue(String.class);

                        if (energiaStr == null || data == null || hora == null) continue;

                        try {
                            Date date = sdf.parse(data + " " + hora);
                            if (date == null) continue;

                            long time = date.getTime();

                            // fora das 24h
                            if (time < limite24h) continue;

                            energiaStr = energiaStr.replace("kWh", "").trim();
                            somaEnergia += Float.parseFloat(energiaStr);

                        } catch (Exception ignored) {}
                    }

                    // AGRUPAR POR CÔMODO

                    float atual = consumoPorComodo.containsKey(nomeComodo)
                            ? consumoPorComodo.get(nomeComodo)
                            : 0f;

                    consumoPorComodo.put(nomeComodo, atual + somaEnergia);
                }


                // GERAR GRÁFICO

                ArrayList<PieEntry> entries = new ArrayList<>();

                for (Map.Entry<String, Float> item : consumoPorComodo.entrySet()) {
                    entries.add(new PieEntry(item.getValue(), item.getKey()));
                }

                if (entries.isEmpty()) {
                    pieChartConsumo.setCenterText("Sem dados\n24h");
                    pieChartConsumo.invalidate();
                    return;
                }

                PieDataSet dataSet = new PieDataSet(entries, "Consumo (kWh)");
                dataSet.setSliceSpace(3f);
                dataSet.setValueTextSize(12f);
                dataSet.setColors(
                        com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS
                );

                dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.format(Locale.getDefault(), "%.2f kWh", value);
                    }
                });

                pieChartConsumo.setData(new PieData(dataSet));
                pieChartConsumo.animateY(1200);
                pieChartConsumo.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this,
                        "Erro ao carregar gráfico",
                        Toast.LENGTH_SHORT).show();
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
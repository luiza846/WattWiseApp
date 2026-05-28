package com.example.wattwiseapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
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

public class Dashboard extends AppCompatActivity {

    //grafico
    private LineChart lineChart;
    private PieChart pieChartConsumo;
    //botoes
    Button btnRegisterRoom, btnRegisterAppliance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        // menu
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //grafico
        lineChart = findViewById(R.id.energyChart);
        configurarGrafico();

        // griafico (pie)
        pieChartConsumo = findViewById(R.id.pieChartConsumo);
        configurarGraficoRosca();

        //botoes
//        btnRegisterRoom = findViewById(R.id.btnRegisterRoom);
//        btnRegisterAppliance = findViewById(R.id.btnRegisterAppliance);
//
//        btnRegisterRoom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(Dashboard.this, registerRoom.class);
//                startActivity(i);
//            }
//        });
//
//        btnRegisterAppliance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(Dashboard.this, registerAppliance.class);
//                startActivity(i);
//            }
//        });




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
            Intent i = new Intent(Dashboard.this, connectSensor.class);
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
            Intent i = new Intent(Dashboard.this, metas.class);
            startActivity(i);
            return true;
        } else if (id == R.id.configuracao) {
            Intent i = new Intent(Dashboard.this, settings.class);
            startActivity(i);
            return true;
        } else if (id == R.id.logout) {
            Intent i = new Intent(Dashboard.this, MainActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    // grafico
    private void configurarGrafico() {
        // 1. Criar os dados (Exemplo: Consumo em 5 dias)
        List<Entry> entradas = new ArrayList<>();
        entradas.add(new Entry(1, 10f)); // Dia 1, 10kWh
        entradas.add(new Entry(2, 15f)); // Dia 2, 15kWh
        entradas.add(new Entry(3, 12f));
        entradas.add(new Entry(4, 20f));
        entradas.add(new Entry(5, 18f));

        // 2. Configurar o conjunto de dados (A linha em si)
        LineDataSet dataSet = new LineDataSet(entradas, "Consumo (kWh)");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.DKGRAY);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true); // Mostra o valor em cima do ponto

        // 3. Aplicar os dados ao gráfico
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // 4. Customizações Visuais
        lineChart.getDescription().setText("Monitoramento Diário");
        lineChart.animateX(1000); // Animação de entrada
        lineChart.invalidate(); // Atualiza o gráfico
    }

    private void configurarGraficoRosca() {
        // 1. Ativa o "buraco" no meio para virar um gráfico Doughnut
        pieChartConsumo.setDrawHoleEnabled(true);
        pieChartConsumo.setHoleRadius(60f); // Tamanho da rosca (em %)
        pieChartConsumo.setTransparentCircleRadius(65f); // Efeito de sombra interna

        // 2. Adiciona o texto centralizado (Foco em Conscientização: Gasto Total)
        pieChartConsumo.setCenterText("Total de Hoje\nR$ 18,50");
        pieChartConsumo.setCenterTextSizePixels(18f);
        pieChartConsumo.setCenterTextColor(Color.DKGRAY);

        // 3. Criando a lista de dados (Entries)
        ArrayList<PieEntry> entradas = new ArrayList<>();
        entradas.add(new PieEntry(40f, "Cozinha"));
        entradas.add(new PieEntry(35f, "Quarto"));
        entradas.add(new PieEntry(25f, "Banheiro"));

        // 4. Configurando o DataSet (Visual das fatias)
        PieDataSet dataSet = new PieDataSet(entradas, "Consumo por Cômodo");
        dataSet.setSliceSpace(3f); // Espaçamento elegante entre as fatias

        // Definindo as cores das fatias usando Hexadecimal
        ArrayList<Integer> cores = new ArrayList<>();
        cores.add(Color.parseColor("#2ECC71")); // Verde (Cozinha)
        cores.add(Color.parseColor("#3498DB")); // Azul (Quarto)
        cores.add(Color.parseColor("#E74C3C")); // Vermelho (Banheiro)
        dataSet.setColors(cores);

        // Configuração dos textos dentro das fatias
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        // 5. Junta tudo e renderiza na tela
        PieData data = new PieData(dataSet);
        pieChartConsumo.setData(data);

        // Ajustes estéticos finais de UX
        pieChartConsumo.getDescription().setEnabled(false); // Remove legenda padrão do canto
        pieChartConsumo.getLegend().setEnabled(true); // Ativa a legenda dos cômodos
        pieChartConsumo.animateY(1400); // Animação de entrada bem fluida
    }
}
package com.example.wattwiseapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class report extends AppCompatActivity {

    //tabela
    private TableLayout tableLayout;

    Spinner edtPeriodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);

        //campos
        edtPeriodo = findViewById(R.id.edtPeriodo);

        // menu
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //tabela
        tableLayout = findViewById(R.id.tableAppliance);

        // inserir dados ficiticios (provisorio
        List<String[]> dadosFicticios = new ArrayList<>();

        dadosFicticios.add(new String[]{"Quarto", "Ar Condicionado", "120 kWh"});
        dadosFicticios.add(new String[]{"Cozinha", "Geladeira", "60 kWh"});
        dadosFicticios.add(new String[]{"Banheiro", "Chuveiro", "180 kWh"});
        dadosFicticios.add(new String[]{"Sala", "Televisão", "15 kWh"});
        dadosFicticios.add(new String[]{"Lavanderia", "Máquina de Lavar", "45 kWh"});

        // chamar a funcao
        inserirDadosTabela(dadosFicticios);

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
            Intent i = new Intent(report.this, listRoom.class);
            startActivity(i);
            return true;
        } else if (id == R.id.eletronicos) {
            Intent i = new Intent(report.this, listAppliance.class);
            startActivity(i);
            return true;
        } else if (id == R.id.relatorios) {
            Intent i = new Intent(report.this, report.class);
            startActivity(i);
            return true;
        } else if (id == R.id.metas) {
            Intent i = new Intent(report.this, metas.class);
            startActivity(i);
            return true;
        } else if (id == R.id.configuracao) {
            Intent i = new Intent(report.this, settings.class);
            startActivity(i);
            return true;
        } else if (id == R.id.logout) {
            Intent i = new Intent(report.this, MainActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // dados provisorio (gerado pela ia) Pedro realizar a consulta no banco de dados

    private void inserirDadosTabela(List<String[]> listaItens) {

        for (int i = 0; i < listaItens.size(); i++) {
            String[] item = listaItens.get(i);

            TableRow row = new TableRow(this);
            row.setPadding(10, 25, 10, 25);

            if (i % 2 == 0) {
                row.setBackgroundColor(Color.parseColor("#F9F9F9"));
            } else {
                row.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }

            // Configuração de LayoutParams para aplicar os pesos via código
            TableRow.LayoutParams paramsOrigem = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.6f);
            TableRow.LayoutParams paramsImpacto = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.2f);
            TableRow.LayoutParams paramsCusto = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.2f);

            // origem
            android.widget.LinearLayout containerOrigem = new android.widget.LinearLayout(this);
            containerOrigem.setOrientation(android.widget.LinearLayout.VERTICAL);
            containerOrigem.setGravity(Gravity.CENTER);
            containerOrigem.setLayoutParams(paramsOrigem);

            TextView txtEletrodomestico = new TextView(this);
            txtEletrodomestico.setText(item[1]);
            txtEletrodomestico.setTextColor(Color.BLACK);
            txtEletrodomestico.setTypeface(null, android.graphics.Typeface.BOLD);
            txtEletrodomestico.setTextSize(15);
            txtEletrodomestico.setGravity(Gravity.CENTER);

            TextView txtComodo = new TextView(this);
            txtComodo.setText(item[0]);
            txtComodo.setTextColor(Color.GRAY);
            txtComodo.setTextSize(12);
            txtComodo.setGravity(Gravity.CENTER);

            containerOrigem.addView(txtEletrodomestico);
            containerOrigem.addView(txtComodo);

            // consumo (kWh)
            TextView txtConsumo = new TextView(this);
            txtConsumo.setText(item[2]);
            txtConsumo.setTextColor(Color.BLACK);
            txtConsumo.setTextSize(15);
            txtConsumo.setGravity(Gravity.CENTER);
            txtConsumo.setLayoutParams(paramsImpacto);

            // custo
            TextView txtCusto = new TextView(this);
            // calcula o custo (REVISAR)
            txtCusto.setText("R$ " + (45.50 + (i * 12)));
            txtCusto.setTextColor(Color.parseColor("#2F3B75"));
            txtCusto.setTypeface(null, android.graphics.Typeface.BOLD);
            txtCusto.setTextSize(15);
            txtCusto.setGravity(Gravity.CENTER);
            txtCusto.setLayoutParams(paramsCusto);

            // add linha
            row.addView(containerOrigem);
            row.addView(txtConsumo);
            row.addView(txtCusto);

            tableLayout.addView(row);
        }
    }

}
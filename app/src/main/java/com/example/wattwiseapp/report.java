package com.example.wattwiseapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);

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
        inserirDadosFicticiosNaTabela(dadosFicticios);

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
            Toast.makeText(this, "Clicou em Sair", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.configuracao) {
            Toast.makeText(this, "Clicou em Sair", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.logout) {
            Intent i = new Intent(report.this, MainActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // dados provisorio (gerado pela ia) Pedro realizar a consulta no banco de dados
    private void inserirDadosFicticiosNaTabela(List<String[]> listaItens) {

        for (int i = 0; i < listaItens.size(); i++) {
            String[] item = listaItens.get(i);

            // cria a linha física da tabela
            TableRow row = new TableRow(this);
            row.setPadding(10, 20, 10, 20);

            // linhas alternadas (Zebradas) para facilitar a leitura
            if (i % 2 == 0) {
                row.setBackgroundColor(Color.parseColor("#F9F9F9")); // cinza bem clarinho
            } else {
                row.setBackgroundColor(Color.parseColor("#FFFFFF")); // branco
            }

            //CÔMODO
            TextView txtComodo = new TextView(this);
            txtComodo.setText(item[0]);
            txtComodo.setTextColor(Color.BLACK);
            txtComodo.setTextSize(15);
            txtComodo.setGravity(Gravity.CENTER);

            //ELETRODOMÉSTICO
            TextView txtEletro = new TextView(this);
            txtEletro.setText(item[1]);
            txtEletro.setTextColor(Color.BLACK);
            txtEletro.setTextSize(15);
            txtEletro.setGravity(Gravity.CENTER);

            //CONSUMO
            TextView txtConsumo = new TextView(this);
            txtConsumo.setText(item[2]);
            txtConsumo.setTextColor(Color.parseColor("#2F3B75"));
            txtConsumo.setTextSize(15);
            txtConsumo.setGravity(Gravity.CENTER);

            row.addView(txtComodo);
            row.addView(txtEletro);
            row.addView(txtConsumo);


            tableLayout.addView(row);
        }
    }
}
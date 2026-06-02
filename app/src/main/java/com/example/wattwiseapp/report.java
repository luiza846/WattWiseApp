package com.example.wattwiseapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import com.google.firebase.*;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class report extends AppCompatActivity {

    //tabela
    private TableLayout tableLayout;

    Spinner edtPeriodo;

    Button btnRelatorio;

    private List<String[]> dadosReais = new ArrayList<>();
    private int diasSelecionados = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);

        // tabela no app
        edtPeriodo = findViewById(R.id.edtPeriodo);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.periodo,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtPeriodo.setAdapter(adapter);


        edtPeriodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                edtPeriodo.setPopupBackgroundResource(android.R.color.white);

                // texto do item selecionado (spinner fechado)
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(Color.BLACK);
                }


                switch (position) {
                    case 0: diasSelecionados = 1; break;
                    case 1: diasSelecionados = 7; break;
                    case 2: diasSelecionados = 30; break;
                    case 3: diasSelecionados = 90; break;
                }

                carregarTabela();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // botao
        btnRelatorio = findViewById(R.id.btnRelatorio);


        //campos
        edtPeriodo = findViewById(R.id.edtPeriodo);

        // menu
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //tabela
        tableLayout = findViewById(R.id.tableAppliance);


        // inserir dados reais do usuário puxando do firebase

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {

            String userId = currentUser.getUid();

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

            rootRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dadosReais.clear(); //limpa lista antiga
                    tableLayout.removeViews(1, tableLayout.getChildCount() - 1); //limpa linhas visuais da tela

                    DataSnapshot userSnap = snapshot.child("Usuarios").child(userId);
                    DataSnapshot sensoresGlobaisSnap = snapshot.child("dados");

                    Map<String, String> eletroParaSensor = new HashMap<>();
                    DataSnapshot userDadosSnap = userSnap.child("dados");

                    for (DataSnapshot meuSensorSnap : userDadosSnap.getChildren()) {
                        String nomeDoSensor = meuSensorSnap.getKey();
                        String idEletroAtivo = meuSensorSnap.child("idEletroAtivo").getValue(String.class);

                        if (idEletroAtivo != null && !idEletroAtivo.isEmpty()) {
                            eletroParaSensor.put(idEletroAtivo, nomeDoSensor);
                        }
                    }


                    DataSnapshot eletronicosSnap = userSnap.child("Eletronicos");

                    for (DataSnapshot eletroSnap : eletronicosSnap.getChildren()) {
                        String idDoAparelho = eletroSnap.getKey();
                        String comodo = eletroSnap.child("comodoEletro").getValue(String.class);
                        String nome = eletroSnap.child("nomeEletro").getValue(String.class);

                        if (comodo == null) comodo = "Não definido";
                        if (nome == null) nome = "Aparelho";

                        String consumoTexto = "-";

                        if (idDoAparelho != null && eletroParaSensor.containsKey(idDoAparelho)) {

                            String nomeSensorAtrelado = eletroParaSensor.get(idDoAparelho);


                            Object energiaObj = sensoresGlobaisSnap.child(nomeSensorAtrelado).child("energia").getValue();
                            Double energiaReal = null;

                            if (energiaObj != null) {
                                try {
                                    String energiaLimpa = String.valueOf(energiaObj).replaceAll("[^\\d.]", ""); //regex para ignorar o "kWh" que está atrelado ao sensor no firebase para não ocorrer conflito ao puxar os dados para a tabela de relatorios
                                    energiaReal = Double.parseDouble(energiaLimpa);
                                } catch (NumberFormatException e) {
                                    energiaReal = null;
                                }
                            }

                            if (energiaReal != null) {
                                consumoTexto = String.format(Locale.getDefault(), "%.2f kWh", energiaReal);

                                /* TODO: CÁLCULO TEMPORÁRIO PARA REALIZAR O CUSTO DE ENERGIA DO ELETRODOMÉSTICO
                                double tarifaPorKwh = 0.85;
                                double custoCalculado = energiaReal * tarifaPorKwh; */

                            } else {
                                consumoTexto = "Agurdando dados..."; // Sensor conectado, mas ESP32 ainda não enviou energia
                            }
                        }

                        dadosReais.add(new String[]{comodo, nome, consumoTexto});

                    }

                    inserirDadosTabela(dadosReais);

                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(report.this, "Erro no banco: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }


        btnRelatorio.setOnClickListener(v -> {

            DatabaseReference rootRef =
                    FirebaseDatabase.getInstance().getReference();

            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    gerarRelatorioPdf("Relatorio", snapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(report.this,
                            "Erro ao gerar relatório",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // gerar pdf
    public void gerarRelatorioPdf(String title, DataSnapshot snapshot) {

        PdfDocument pdfDocument = new PdfDocument();

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // ================= LOGO =================
        Bitmap logoOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.wattwise);

        float larguraDesejadaPdf = 150f;
        float escala = larguraDesejadaPdf / logoOriginal.getWidth();

        Matrix matrix = new Matrix();
        matrix.postScale(escala, escala);
        matrix.postTranslate(40, 25);

        Paint paintLogo = new Paint();
        paintLogo.setAntiAlias(true);
        paintLogo.setFilterBitmap(true);

        canvas.drawBitmap(logoOriginal, matrix, paintLogo);

        // ================= TÍTULO =================
        paint.setColor(Color.BLACK);
        paint.setTextSize(14);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("Relatório de Consumo (Últimos 30 dias)", 40, 110, paint);

        // ================= FIREBASE =================
        DataSnapshot userSnap = snapshot.child("Usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        DataSnapshot historicoSnap = snapshot.child("historico_sensores");

        // ================= MAPAS =================
        Map<String, String> sensorToEletro = new HashMap<>();
        Map<String, String> eletroToNome = new HashMap<>();
        Map<String, String> eletroToComodo = new HashMap<>();

        for (DataSnapshot s : userSnap.child("dados").getChildren()) {
            sensorToEletro.put(s.getKey(), s.child("idEletroAtivo").getValue(String.class));
        }

        for (DataSnapshot e : userSnap.child("Eletronicos").getChildren()) {
            eletroToNome.put(e.getKey(), e.child("nomeEletro").getValue(String.class));
            eletroToComodo.put(e.getKey(), e.child("comodoEletro").getValue(String.class));
        }

        // ================= 30 DIAS =================
        long limite30dias = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        // ================= STRUCT CORRETA =================
        class Linha {
            String comodo;
            String aparelho;
            String data;
            float consumo;

            Linha(String c, String a, String d, float v) {
                comodo = c;
                aparelho = a;
                data = d;
                consumo = v;
            }
        }

        Map<String, Linha> dadosPorDia = new HashMap<>();
        float total = 0f;

        // ================= PROCESSAMENTO =================
        for (DataSnapshot sensorSnap : historicoSnap.getChildren()) {

            String sensorId = sensorSnap.getKey();
            String idEletro = sensorToEletro.get(sensorId);

            if (idEletro == null) continue;

            String aparelho = eletroToNome.get(idEletro);
            String comodo = eletroToComodo.get(idEletro);

            if (aparelho == null || comodo == null) continue;

            for (DataSnapshot leitura : sensorSnap.getChildren()) {

                try {
                    String energiaStr = leitura.child("energia").getValue(String.class);
                    String data = leitura.child("data").getValue(String.class);
                    String hora = leitura.child("hora").getValue(String.class);

                    if (energiaStr == null || data == null || hora == null) continue;

                    Date date = sdf.parse(data + " " + hora);
                    if (date == null || date.getTime() < limite30dias) continue;

                    float energia = Float.parseFloat(
                            energiaStr.replace("kWh", "").trim()
                    );

                    total += energia;

                    Linha atual = dadosPorDia.get(data);

                    if (atual == null) {
                        dadosPorDia.put(data,
                                new Linha(comodo, aparelho, data, energia));
                    } else {
                        atual.consumo += energia;
                    }

                } catch (Exception ignored) {}
            }
        }

        // ================= ORDENAR =================
        List<Linha> lista = new ArrayList<>(dadosPorDia.values());

        Collections.sort(lista, (a, b) -> {
            try {
                Date d1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(a.data);
                Date d2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(b.data);
                return d1.compareTo(d2);
            } catch (Exception e) {
                return 0;
            }
        });

        // ================= RESUMO =================
        float custo = total * 0.92f;

        paint.setTextSize(12);
        paint.setTypeface(Typeface.DEFAULT);

        canvas.drawText("Consumo do Mês: " +
                        String.format(Locale.getDefault(), "%.2f kWh", total),
                40, 145, paint);

        canvas.drawText("Valor Estimado Total: R$ " +
                        String.format(Locale.getDefault(), "%.2f", custo),
                40, 162, paint);

        // ================= LINHA =================
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(1.5f);
        canvas.drawLine(40, 85, 555, 85, paint);

        // ================= TABELA =================
        int y = 190;

        int colComodo = 40;
        int colAparelho = 160;
        int colData = 320;
        int colConsumo = 460;

        paint.setColor(Color.parseColor("#2F3B75"));
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(13);

        canvas.drawText("Cômodo", colComodo, y, paint);
        canvas.drawText("Aparelho", colAparelho, y, paint);
        canvas.drawText("Data", colData, y, paint);
        canvas.drawText("Consumo", colConsumo, y, paint);

        paint.setColor(Color.BLACK);
        canvas.drawLine(40, y + 8, 555, y + 8, paint);

        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(12);

        y += 25;

        for (Linha l : lista) {

            canvas.drawText(l.comodo, colComodo, y, paint);
            canvas.drawText(l.aparelho, colAparelho, y, paint);
            canvas.drawText(l.data, colData, y, paint);

            canvas.drawText(
                    String.format(Locale.getDefault(), "%.3f kWh", l.consumo),
                    colConsumo,
                    y,
                    paint
            );

            paint.setColor(Color.parseColor("#E0E0E0"));
            canvas.drawLine(40, y + 8, 555, y + 8, paint);
            paint.setColor(Color.BLACK);

            y += 22;

            if (y > 800) break;
        }

        // ================= FINAL =================
        pdfDocument.finishPage(page);

        File dir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);

        File file = new File(dir,
                "Relatorio_30_dias_" + System.currentTimeMillis() + ".pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF salvo em Downloads!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sensor) {
            Intent i = new Intent(report.this, connectSensor.class);
            startActivity(i);
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
            Intent i = new Intent(report.this, Dashboard.class);
            startActivity(i);
            return true;
        } else if (id == R.id.configuracao) {
            Intent i = new Intent(report.this, settings.class);
            startActivity(i);
            return true;
        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(report.this, MainActivity.class);

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

    // dados provisorio (gerado pela ia) Pedro realizar a consulta no banco de dados

    private void inserirDadosTabela(List<String[]> listaItens) {

        tableLayout.removeAllViews();

        for (int i = 0; i < listaItens.size(); i++) {

            String[] item = listaItens.get(i);

            TableRow row = new TableRow(this);
            row.setPadding(10, 25, 10, 25);

            row.setBackgroundColor(i % 2 == 0 ?
                    Color.parseColor("#F9F9F9") :
                    Color.parseColor("#FFFFFF"));

            TableRow.LayoutParams paramsOrigem =
                    new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.6f);

            TableRow.LayoutParams paramsImpacto =
                    new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.2f);

            TableRow.LayoutParams paramsCusto =
                    new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.2f);

            // ORIGEM (aparelho + cômodo)
            LinearLayout containerOrigem = new LinearLayout(this);
            containerOrigem.setOrientation(LinearLayout.VERTICAL);
            containerOrigem.setGravity(Gravity.CENTER);
            containerOrigem.setLayoutParams(paramsOrigem);

            TextView txtEletro = new TextView(this);
            txtEletro.setText(item[1]);
            txtEletro.setTypeface(null, Typeface.BOLD);
            txtEletro.setTextSize(15);
            txtEletro.setTextColor(Color.BLACK);
            txtEletro.setGravity(Gravity.CENTER);

            TextView txtComodo = new TextView(this);
            txtComodo.setText(item[0]);
            txtComodo.setTextSize(12);
            txtComodo.setTextColor(Color.GRAY);
            txtComodo.setGravity(Gravity.CENTER);

            containerOrigem.addView(txtEletro);
            containerOrigem.addView(txtComodo);

            // consumo
            TextView txtConsumo = new TextView(this);
            txtConsumo.setText(item[2]);
            txtConsumo.setLayoutParams(paramsImpacto);
            txtConsumo.setTextColor(Color.BLACK);
            txtConsumo.setTypeface(null, Typeface.BOLD);
            txtConsumo.setGravity(Gravity.CENTER);

            // custo (AGORA CORRETO)
            TextView txtCusto = new TextView(this);
            txtCusto.setText(item.length > 3 ? item[3] : "R$ -");
            txtCusto.setTypeface(null, Typeface.BOLD);
            txtCusto.setTextColor(Color.BLACK);
            txtCusto.setLayoutParams(paramsCusto);
            txtCusto.setGravity(Gravity.CENTER);

            row.addView(containerOrigem);
            row.addView(txtConsumo);
            row.addView(txtCusto);

            tableLayout.addView(row);
        }
    }
    private void carregarTabela() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dadosReais.clear();
                tableLayout.removeAllViews();

                DataSnapshot userSnap = snapshot.child("Usuarios").child(user.getUid());
                DataSnapshot historicoSnap = snapshot.child("historico_sensores");

                Map<String, String> sensorToEletro = new HashMap<>();
                Map<String, String> eletroToNome = new HashMap<>();
                Map<String, String> eletroToComodo = new HashMap<>();

                for (DataSnapshot s : userSnap.child("dados").getChildren()) {
                    sensorToEletro.put(s.getKey(), s.child("idEletroAtivo").getValue(String.class));
                }

                for (DataSnapshot e : userSnap.child("Eletronicos").getChildren()) {
                    eletroToNome.put(e.getKey(), e.child("nomeEletro").getValue(String.class));
                    eletroToComodo.put(e.getKey(), e.child("comodoEletro").getValue(String.class));
                }

                long limite = System.currentTimeMillis()
                        - (diasSelecionados * 24L * 60 * 60 * 1000);

                SimpleDateFormat sdf =
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                Map<String, Float> consumoPorEletro = new HashMap<>();
                Map<String, String> comodoMap = new HashMap<>();

                for (DataSnapshot sensorSnap : historicoSnap.getChildren()) {

                    String sensorId = sensorSnap.getKey();
                    String idEletro = sensorToEletro.get(sensorId);

                    if (idEletro == null) continue;

                    String nome = eletroToNome.get(idEletro);
                    String comodo = eletroToComodo.get(idEletro);

                    if (nome == null || comodo == null) continue;

                    for (DataSnapshot leitura : sensorSnap.getChildren()) {

                        try {
                            String energiaStr = leitura.child("energia").getValue(String.class);
                            String data = leitura.child("data").getValue(String.class);
                            String hora = leitura.child("hora").getValue(String.class);

                            if (energiaStr == null || data == null || hora == null) continue;

                            Date date = sdf.parse(data + " " + hora);
                            if (date == null || date.getTime() < limite) continue;

                            float energia = Float.parseFloat(
                                    energiaStr.replace("kWh", "").trim()
                            );

                            float atual = consumoPorEletro.getOrDefault(nome, 0f);
                            consumoPorEletro.put(nome, atual + energia);
                            comodoMap.put(nome, comodo);

                        } catch (Exception ignored) {}
                    }
                }

                List<String[]> lista = new ArrayList<>();

                for (String eletro : consumoPorEletro.keySet()) {

                    String comodo = comodoMap.get(eletro);
                    float consumo = consumoPorEletro.get(eletro);

                    float custo = consumo * 0.92f;

                    lista.add(new String[]{
                            comodo,
                            eletro,
                            String.format(Locale.getDefault(), "%.2f kWh", consumo),
                            String.format(Locale.getDefault(), "R$ %.2f", custo)
                    });
                }

                inserirDadosTabela(lista);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

}
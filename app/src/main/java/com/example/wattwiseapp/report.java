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

        // Configuração inicial da primeira página A4 (595 x 842 pixels)
        final int PAGE_WIDTH = 595;
        final int PAGE_HEIGHT = 842;
        int pageNumber = 1;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create();
        final PdfDocument.Page[] page = {pdfDocument.startPage(pageInfo)};
        final Canvas[] canvas = {page[0].getCanvas()};
        Paint paint = new Paint();

        // Método auxiliar para desenhar o cabeçalho padrão em novas páginas
        Runnable desenharCabecalho = new Runnable() {
            @Override
            public void run() {
                // ================= LOGO =================
                Bitmap logoOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.wattwise);
                if (logoOriginal != null) {
                    float larguraDesejadaPdf = 150f;
                    float escala = larguraDesejadaPdf / logoOriginal.getWidth();
                    Matrix matrix = new Matrix();
                    matrix.postScale(escala, escala);
                    matrix.postTranslate(40, 25);

                    Paint paintLogo = new Paint();
                    paintLogo.setAntiAlias(true);
                    paintLogo.setFilterBitmap(true);
                    canvas[0].drawBitmap(logoOriginal, matrix, paintLogo);
                }

                // ================= TÍTULO =================
                paint.setColor(Color.BLACK);
                paint.setTextSize(14);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                canvas[0].drawText("Relatório de Consumo (Últimos 30 dias)", 40, 110, paint);

                // ================= LINHA DIVISÓRIA =================
                paint.setColor(Color.GRAY);
                paint.setStrokeWidth(1.5f);
                canvas[0].drawLine(40, 85, 555, 85, paint);
            }
        };

        // Desenha o cabeçalho na primeira página
        desenharCabecalho.run();

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

        // ================= LIMITAÇÃO 30 DIAS =================
        long limite30dias = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        // Classe para estruturar as linhas da tabela
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

        // Chave composta para agrupar individualmente por "Data_Aparelho"
        Map<String, Linha> agrupamentoDados = new HashMap<>();
        float totalGeralConsumo = 0f;

        // ================= PROCESSAMENTO COM DELTAS =================
        for (DataSnapshot sensorSnap : historicoSnap.getChildren()) {

            String sensorId = sensorSnap.getKey();
            String idEletro = sensorToEletro.get(sensorId);
            if (idEletro == null) continue;

            String aparelho = eletroToNome.get(idEletro);
            String comodo = eletroToComodo.get(idEletro);
            if (aparelho == null || comodo == null) continue;

            Float energiaAnteriorParaPdf = null;

            for (DataSnapshot leitura : sensorSnap.getChildren()) {
                try {
                    String energiaStr = leitura.child("energia").getValue(String.class);
                    String data = leitura.child("data").getValue(String.class);
                    String hora = leitura.child("hora").getValue(String.class);

                    if (energiaStr == null || data == null || hora == null) continue;

                    Date date = sdf.parse(data + " " + hora);
                    if (date == null) continue;

                    long time = date.getTime();

                    // Se for leitura anterior ao corte de 30 dias, guarda como base
                    if (time < limite30dias) {
                        energiaStr = energiaStr.replace("kWh", "").trim();
                        energiaAnteriorParaPdf = Float.parseFloat(energiaStr);
                        continue;
                    }

                    energiaStr = energiaStr.replace("kWh", "").trim();
                    float energia = Float.parseFloat(energiaStr);

                    if (energiaAnteriorParaPdf == null) {
                        energiaAnteriorParaPdf = energia;
                    }

                    // CALCULA O DELTA (Consumo real do período)
                    float deltaParaPdf = energia - energiaAnteriorParaPdf;

                    if (deltaParaPdf >= 0 && deltaParaPdf <= 5) {
                        totalGeralConsumo += deltaParaPdf;

                        // Agrupa por Dia + Aparelho para não sobrepor registros de eletrodomésticos diferentes no mesmo dia
                        String chaveAgrupamento = data + "_" + aparelho;
                        Linha atual = agrupamentoDados.get(chaveAgrupamento);

                        if (atual == null) {
                            agrupamentoDados.put(chaveAgrupamento, new Linha(comodo, aparelho, data, deltaParaPdf));
                        } else {
                            atual.consumo += deltaParaPdf;
                        }
                        energiaAnteriorParaPdf = energia;
                    }

                } catch (Exception ignored) {}
            }
        }

        // ================= ORDENAÇÃO POR DATA =================
        List<Linha> lista = new ArrayList<>(agrupamentoDados.values());
        Collections.sort(lista, (a, b) -> {
            try {
                Date d1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(a.data);
                Date d2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(b.data);
                return d1.compareTo(d2);
            } catch (Exception e) {
                return 0;
            }
        });

        // ================= EXIBIÇÃO DO RESUMO DO MÊS =================
        float custo = totalGeralConsumo * 0.92f;
        paint.setTextSize(12);
        paint.setTypeface(Typeface.DEFAULT);

        canvas[0].drawText("Consumo Total do Mês: " + String.format(Locale.getDefault(), "%.2f kWh", totalGeralConsumo), 40, 145, paint);
        canvas[0].drawText("Valor Estimado Total: R$ " + String.format(Locale.getDefault(), "%.2f", custo), 40, 162, paint);

        // ================= CONFIGURAÇÃO DA TABELA =================
        int y = 190;
        int colComodo = 40;
        int colAparelho = 160;
        int colData = 320;
        int colConsumo = 460;

        paint.setColor(Color.parseColor("#2F3B75"));
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(13);

        canvas[0].drawText("Cômodo", colComodo, y, paint);
        canvas[0].drawText("Aparelho", colAparelho, y, paint);
        canvas[0].drawText("Data", colData, y, paint);
        canvas[0].drawText("Consumo", colConsumo, y, paint);

        paint.setColor(Color.BLACK);
        canvas[0].drawLine(40, y + 8, 555, y + 8, paint);

        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(12);
        y += 25;

        // ================= RENDERIZAR AS LINHAS (MÚLTIPLAS PÁGINAS) =================
        for (Linha l : lista) {
            // Se a folha encher (y próximo do limite de 842), fecha a página atual e abre uma nova
            if (y > 800) {
                pdfDocument.finishPage(page[0]);
                pageNumber++;
                pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create();
                page[0] = pdfDocument.startPage(pageInfo);
                canvas[0] = page[0].getCanvas();

                desenharCabecalho.run();

                // Reinicia a posição Y inicial para a nova página
                y = 150;
                paint.setTypeface(Typeface.DEFAULT);
                paint.setTextSize(12);
                paint.setColor(Color.BLACK);
            }

            canvas[0].drawText(l.comodo, colComodo, y, paint);
            canvas[0].drawText(l.aparelho, colAparelho, y, paint);
            canvas[0].drawText(l.data, colData, y, paint);
            canvas[0].drawText(String.format(Locale.getDefault(), "%.3f kWh", l.consumo), colConsumo, y, paint);

            paint.setColor(Color.parseColor("#E0E0E0"));
            canvas[0].drawLine(40, y + 8, 555, y + 8, paint);
            paint.setColor(Color.BLACK);

            y += 22;
        }

        // ================= SALVAMENTO E FINALIZAÇÃO =================
        pdfDocument.finishPage(page[0]);

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(dir, "Relatorio_30_dias_" + System.currentTimeMillis() + ".pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF salvo em Downloads!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao gerar PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sensor) {
            Intent i = new Intent(report.this, listSensor.class);
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

                // Define o limite com base na quantidade de dias selecionados
                long limite = System.currentTimeMillis() - (diasSelecionados * 24L * 60 * 60 * 1000);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                Map<String, Float> consumoPorEletro = new HashMap<>();
                Map<String, String> comodoMap = new HashMap<>();

                // ==========================================
                // PERCORRER HISTÓRICO E CALCULAR DELTAS
                // ==========================================
                for (DataSnapshot sensorSnap : historicoSnap.getChildren()) {

                    String sensorId = sensorSnap.getKey();
                    String idEletro = sensorToEletro.get(sensorId);

                    if (idEletro == null) continue;

                    String nome = eletroToNome.get(idEletro);
                    String comodo = eletroToComodo.get(idEletro);

                    if (nome == null || comodo == null) continue;

                    // Controla o ponto de partida do acumulador para cada sensor na tabela
                    Float energiaAnteriorParaTabela = null;
                    float totalDeltaSensor = 0f;

                    for (DataSnapshot leitura : sensorSnap.getChildren()) {

                        try {
                            String energiaStr = leitura.child("energia").getValue(String.class);
                            String data = leitura.child("data").getValue(String.class);
                            String hora = leitura.child("hora").getValue(String.class);

                            if (energiaStr == null || data == null || hora == null) continue;

                            Date date = sdf.parse(data + " " + hora);
                            if (date == null) continue;

                            long time = date.getTime();

                            // Se a leitura for mais antiga que o limite de dias, serve de base inicial
                            if (time < limite) {
                                energiaStr = energiaStr.replace("kWh", "").trim();
                                energiaAnteriorParaTabela = Float.parseFloat(energiaStr);
                                continue;
                            }

                            energiaStr = energiaStr.replace("kWh", "").trim();
                            float energia = Float.parseFloat(energiaStr);

                            // Caso não haja registros antes do limite, o primeiro registro dentro do intervalo vira a base
                            if (energiaAnteriorParaTabela == null) {
                                energiaAnteriorParaTabela = energia;
                            }

                            // CALCULA O CONSUMO REAL NO INTERVALO (Delta)
                            float deltaParaTabela = energia - energiaAnteriorParaTabela;

                            // Proteção contra ruídos ou reinicializações do medidor
                            if (deltaParaTabela >= 0 && deltaParaTabela <= 5) {
                                totalDeltaSensor += deltaParaTabela;
                                energiaAnteriorParaTabela = energia;
                            }

                        } catch (Exception ignored) {}
                    }

                    // Acumula o consumo real final calculado do sensor para o eletrodoméstico correspondente
                    if (totalDeltaSensor > 0) {
                        float atual = consumoPorEletro.getOrDefault(nome, 0f);
                        consumoPorEletro.put(nome, atual + totalDeltaSensor);
                        comodoMap.put(nome, comodo);
                    }
                }

                // ==========================================
                // CONSTRUÇÃO DA LISTA DA TABELA UI
                // ==========================================
                List<String[]> lista = new ArrayList<>();

                for (String eletro : consumoPorEletro.keySet()) {

                    String comodo = comodoMap.get(eletro);
                    float consumo = consumoPorEletro.get(eletro);

                    // Aplica a tarifa de R$ 0,92 sobre o delta real acumulado
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
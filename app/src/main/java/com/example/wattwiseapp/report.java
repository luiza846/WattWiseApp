package com.example.wattwiseapp;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class report extends AppCompatActivity {

    //tabela
    private TableLayout tableLayout;

    Spinner edtPeriodo;

    Button btnRelatorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);

        // botao
        btnRelatorio = findViewById(R.id.btnRelatorio);

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

        btnRelatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarPdf("Relatorio");
            }
        });
    }

    // gerar pdf
    public void criarPdf(String title){

        //TESTE GERADO POR AI
        // 1. Criar o documento PDF
        PdfDocument pdfDocument = new PdfDocument();

        // 2. Definir o tamanho da página (Tamanho A4 padrão em pontos: 595 x 842)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        // 3. Iniciar a página
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // --- Desenhar o Conteúdo do Relatório ---

// 1. Carrega a imagem original em alta resolução
        android.graphics.Bitmap logoOriginal = android.graphics.BitmapFactory.decodeResource(getResources(), R.drawable.wattwise);

        // 2. Definimos a largura ideal que queremos que ela ocupe no PDF (ex: 150 pontos)
        int larguraDesejadaPdf = 150;

        // 3. MATEMÁTICA DA PROPORÇÃO: Calcula a altura correta baseada na proporção original (1872x603)
        // Isso evita que a imagem fique esticada ou achatada
        int alturaProporcionalPdf = (larguraDesejadaPdf * logoOriginal.getHeight()) / logoOriginal.getWidth();

        // 4. Redimensiona com o filtro 'filter = true' ativado (isso remove o efeito pixelado e suaviza as bordas)
        android.graphics.Bitmap logoPerfeita = android.graphics.Bitmap.createScaledBitmap(logoOriginal, larguraDesejadaPdf, alturaProporcionalPdf, true);

        // 5. Desenha a logo ajustada (Margem esquerda X=40, Topo Y=25)
        canvas.drawBitmap(logoPerfeita, 40, 25, null);

        // 6. Define onde o texto deve começar logo após a logo ajustada
        int fimDaLogoY = 25 + alturaProporcionalPdf;

        // texto
        paint.setColor(Color.BLACK);
        paint.setTextSize(14);
        canvas.drawText("Relatório Consolidado de Consumo Energético por Tomada", 40, 110, paint);

        // Resumo rápido
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Consumo do Mês: 250 kWh", 40, 125, paint);
        canvas.drawText("Valor Estimado Total: R$ 180,00", 40, 147, paint);

        // Linha divisória abaixo dos títulos (Ajustada para Y = 90)
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(1.5f);
        canvas.drawLine(40, 85, 555, 85, paint);

        // ---------------------------------------------------------
        // 2. CONFIGURAÇÃO DA TABELA (Coordenadas)
        // ---------------------------------------------------------
        int inicioX = 40;       // Margem esquerda
        int fimX = 555;         // Margem direita
        int linhaY = 170;       // Posição Y inicial da tabela
        int alturaLinha = 30;   // Espaçamento vertical entre as linhas

        // Definição das colunas (Posição X onde cada uma começa)
        int colComodo = 40;
        int colAparelho = 160;
        int colConsumo = 340;
        int colCusto = 460;

        // --- CABEÇALHO DA TABELA ---
        paint.setColor(Color.parseColor("#2F3B75")); // Cor escura igual ao seu app
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(13);

        canvas.drawText("Cômodo", colComodo, linhaY, paint);
        canvas.drawText("Aparelho", colAparelho, linhaY, paint);
        canvas.drawText("Consumo", colConsumo, linhaY, paint);
        canvas.drawText("Custo Est.", colCusto, linhaY, paint);

        // Linha abaixo do cabeçalho da tabela
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1.5f);
        canvas.drawLine(inicioX, linhaY + 8, fimX, linhaY + 8, paint);

        // ---------------------------------------------------------
        // 3. POPULAR OS DADOS DA TABELA (Dinâmico)
        // ---------------------------------------------------------
        // Recuperando a mesma lista fictícia que você usou na tela
        List<String[]> dadosFicticios = new ArrayList<>();
        dadosFicticios.add(new String[]{"Quarto", "Ar Condicionado", "120 kWh"});
        dadosFicticios.add(new String[]{"Cozinha", "Geladeira", "60 kWh"});
        dadosFicticios.add(new String[]{"Banheiro", "Chuveiro", "180 kWh"});
        dadosFicticios.add(new String[]{"Sala", "Televisão", "15 kWh"});
        dadosFicticios.add(new String[]{"Lavanderia", "Máquina de Lavar", "45 kWh"});

        // Mudar estilo para o corpo da tabela
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(12);

        for (int i = 0; i < dadosFicticios.size(); i++) {
            String[] item = dadosFicticios.get(i);

            // Avança o Y para a próxima linha
            linhaY += alturaLinha;

            // Estilização zebrada opcional (muda a cor do texto para diferenciar as linhas)
            if (i % 2 == 0) {
                paint.setColor(Color.BLACK);
            } else {
                paint.setColor(Color.parseColor("#555555")); // Um cinza escuro
            }

            // Cálculo do custo fictício igual você fez no TableLayout
            String custoCalculado = "R$ " + (45.50 + (i * 12));

            // Desenha o texto de cada coluna na linha atual (Y)
            canvas.drawText(item[0], colComodo, linhaY, paint);   // Cômodo
            canvas.drawText(item[1], colAparelho, linhaY, paint); // Aparelho
            canvas.drawText(item[2], colConsumo, linhaY, paint);  // Consumo

            // Destacar a coluna de custo com a cor azul escura do seu app
            paint.setColor(Color.parseColor("#2F3B75"));
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText(custoCalculado, colCusto, linhaY, paint);

            // Reseta estilos padrão para a próxima volta do laço
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            // Desenha uma linha fina separadora sob o registro atual
            paint.setColor(Color.parseColor("#E0E0E0"));
            paint.setStrokeWidth(0.5f);
            canvas.drawLine(inicioX, linhaY + 8, fimX, linhaY + 8, paint);
        }

        // 4. Finalizar a página
        pdfDocument.finishPage(page);

        // 5. Salvar o arquivo no armazenamento do dispositivo
        File diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Criar um nome único usando o milissegundo atual
        String nomeArquivo = "Relatorio_Consumo_" + System.currentTimeMillis() + ".pdf";
        File arquivo = new File(diretorio, nomeArquivo);

        try {
            pdfDocument.writeTo(new FileOutputStream(arquivo));
            Toast.makeText(this, "PDF salvo em Downloads!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gerar PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        // 6. Fechar o documento
        pdfDocument.close();
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
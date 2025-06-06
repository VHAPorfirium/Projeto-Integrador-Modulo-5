package br.com.projetoIntegrador.presentation.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.fcm.MyFirebaseService;
import br.com.projetoIntegrador.network.IndicadoresDto;
import br.com.projetoIntegrador.presentation.viewmodel.IndicadoresViewModel;

public class AdministradorActivity extends AppCompatActivity {

    private IndicadoresViewModel indicadoresViewModel;

    private TextView tvTempoMedioEsperaValor, tvNaoComparecimentoValor;
    private BarChart chartAtendimentosDia;
    private PieChart chartAtendimentosEspecialidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        Toolbar toolbar = findViewById(R.id.toolbarAdministrador);
        setSupportActionBar(toolbar);

        indicadoresViewModel = new ViewModelProvider(this).get(IndicadoresViewModel.class);

        tvTempoMedioEsperaValor = findViewById(R.id.tvTempoMedioEsperaValor);
        tvNaoComparecimentoValor = findViewById(R.id.tvNaoComparecimentoValor);
        chartAtendimentosDia = findViewById(R.id.chartAtendimentosDia);
        chartAtendimentosEspecialidade = findViewById(R.id.chartAtendimentosEspecialidade);

        setupCharts();
        observarIndicadores();
        carregarIndicadores();
    }

    private void setupCharts() {
        // Configurações gerais para o gráfico de barras
        chartAtendimentosDia.getDescription().setEnabled(false);
        chartAtendimentosDia.setDrawGridBackground(false);
        chartAtendimentosDia.setFitBars(true);
        XAxis xAxis = chartAtendimentosDia.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // Configurações gerais para o gráfico de pizza
        chartAtendimentosEspecialidade.getDescription().setEnabled(false);
        chartAtendimentosEspecialidade.setUsePercentValues(true);
        chartAtendimentosEspecialidade.setEntryLabelTextSize(12f);
        chartAtendimentosEspecialidade.setEntryLabelColor(Color.BLACK);
        chartAtendimentosEspecialidade.setHoleRadius(40f);
        chartAtendimentosEspecialidade.setTransparentCircleRadius(45f);
    }

    private void observarIndicadores() {
        indicadoresViewModel.getIndicadores().observe(this, indicadores -> {
            if (indicadores != null) {
                // Popula os cards e gráficos com os dados recebidos
                tvTempoMedioEsperaValor.setText(String.format(Locale.getDefault(), "%.1f min", indicadores.getTempoMedioEsperaMinutos()));
                tvNaoComparecimentoValor.setText(String.format(Locale.getDefault(), "%.1f%%", indicadores.getPercentualNaoComparecimento()));
                popularGraficoAtendimentosDia(indicadores.getAtendimentosPorDia());
                popularGraficoAtendimentosEspecialidade(indicadores.getAtendimentosPorEspecialidade());
            } else {
                Toast.makeText(this, "Falha ao carregar indicadores.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void popularGraficoAtendimentosDia(Map<String, Integer> dados) {
        if (dados == null || dados.isEmpty()) return;

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Integer> entry : dados.entrySet()) {
            entries.add(new BarEntry(i, entry.getValue()));
            labels.add(entry.getKey());
            i++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Atendimentos");
        dataSet.setColor(ContextCompat.getColor(this, R.color.primary_blue));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        chartAtendimentosDia.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartAtendimentosDia.setData(barData);
        chartAtendimentosDia.invalidate(); // Refresh do gráfico
    }

    private void popularGraficoAtendimentosEspecialidade(Map<String, Integer> dados) {
        if (dados == null || dados.isEmpty()) return;

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dados.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData pieData = new PieData(dataSet);
        chartAtendimentosEspecialidade.setData(pieData);
        chartAtendimentosEspecialidade.invalidate(); // Refresh do gráfico
    }

    private void carregarIndicadores() {
        Toast.makeText(this, "Atualizando indicadores...", Toast.LENGTH_SHORT).show();
        indicadoresViewModel.refreshIndicadores(); // Usa o método refresh para buscar novos dados
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.administrador_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh_indicadores) {
            carregarIndicadores();
            return true;
        } else if (itemId == R.id.action_logout_administrador) {
            SharedPreferences prefs = getSharedPreferences(MyFirebaseService.SHARED_PREFS_NAME, MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
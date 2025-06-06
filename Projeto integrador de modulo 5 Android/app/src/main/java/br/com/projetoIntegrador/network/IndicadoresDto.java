package br.com.projetoIntegrador.network;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

// DTO de exemplo para a resposta do endpoint hipotético GET /indicadores
public class IndicadoresDto {

    @SerializedName("atendimentosPorDia")
    private Map<String, Integer> atendimentosPorDia; // Ex: {"2025-06-05": 50, "2025-06-04": 45}

    @SerializedName("tempoMedioEsperaMinutos")
    private double tempoMedioEsperaMinutos;

    @SerializedName("percentualNaoComparecimento")
    private double percentualNaoComparecimento;

    @SerializedName("atendimentosPorEspecialidade")
    private Map<String, Integer> atendimentosPorEspecialidade; // Ex: {"Clínica Geral": 25, "Cardiologia": 15}

    // Getters e Setters
    public Map<String, Integer> getAtendimentosPorDia() {
        return atendimentosPorDia;
    }

    public void setAtendimentosPorDia(Map<String, Integer> atendimentosPorDia) {
        this.atendimentosPorDia = atendimentosPorDia;
    }

    public double getTempoMedioEsperaMinutos() {
        return tempoMedioEsperaMinutos;
    }

    public void setTempoMedioEsperaMinutos(double tempoMedioEsperaMinutos) {
        this.tempoMedioEsperaMinutos = tempoMedioEsperaMinutos;
    }

    public double getPercentualNaoComparecimento() {
        return percentualNaoComparecimento;
    }

    public void setPercentualNaoComparecimento(double percentualNaoComparecimento) {
        this.percentualNaoComparecimento = percentualNaoComparecimento;
    }

    public Map<String, Integer> getAtendimentosPorEspecialidade() {
        return atendimentosPorEspecialidade;
    }

    public void setAtendimentosPorEspecialidade(Map<String, Integer> atendimentosPorEspecialidade) {
        this.atendimentosPorEspecialidade = atendimentosPorEspecialidade;
    }
}
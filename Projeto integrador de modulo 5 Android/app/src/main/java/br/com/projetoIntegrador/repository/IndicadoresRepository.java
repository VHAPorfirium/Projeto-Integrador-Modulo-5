package br.com.projetoIntegrador.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import br.com.projetoIntegrador.network.ApiClient;
import br.com.projetoIntegrador.network.ApiService;
import br.com.projetoIntegrador.network.IndicadoresDto; // Usando nosso DTO de exemplo
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.annotation.NonNull;

public class IndicadoresRepository {

    private ApiService apiService;

    public IndicadoresRepository() {
        this.apiService = ApiClient.get();
    }

    // Este método buscará os indicadores.
    // A API real retornará um objeto que pode ser mapeado para IndicadoresDto.
    // Por enquanto, o endpoint getIndicadores() no ApiService espera um Map.
    // Vamos ajustar para esperar nosso novo DTO.
    // Lembre-se de ajustar o ApiService.java:
    // @GET("indicadores")
    // Call<IndicadoresDto> getIndicadores();

    public LiveData<IndicadoresDto> fetchIndicadores() {
        MutableLiveData<IndicadoresDto> data = new MutableLiveData<>();
        // AINDA NÃO IMPLEMENTADO NO BACKEND - SIMULAÇÃO
        // apiService.getIndicadores().enqueue(new Callback<IndicadoresDto>() {
        //     @Override
        //     public void onResponse(@NonNull Call<IndicadoresDto> call, @NonNull Response<IndicadoresDto> response) {
        //         if (response.isSuccessful()) {
        //             data.setValue(response.body());
        //         } else {
        //             data.setValue(null);
        //         }
        //     }
        //
        //     @Override
        //     public void onFailure(@NonNull Call<IndicadoresDto> call, @NonNull Throwable t) {
        //         data.setValue(null);
        //     }
        // });

        // ** SIMULAÇÃO DE DADOS ATÉ O BACKEND ESTAR PRONTO **
        IndicadoresDto mockData = new IndicadoresDto();
        mockData.setTempoMedioEsperaMinutos(15.5);
        mockData.setPercentualNaoComparecimento(12.3);
        java.util.Map<String, Integer> atendPorDia = new java.util.HashMap<>();
        atendPorDia.put("Seg", 45);
        atendPorDia.put("Ter", 52);
        atendPorDia.put("Qua", 48);
        mockData.setAtendimentosPorDia(atendPorDia);
        java.util.Map<String, Integer> atendPorEspec = new java.util.HashMap<>();
        atendPorEspec.put("Clínica Geral", 25);
        atendPorEspec.put("Cardiologia", 15);
        atendPorEspec.put("Ortopedia", 12);
        mockData.setAtendimentosPorEspecialidade(atendPorEspec);
        data.setValue(mockData);
        // ** FIM DA SIMULAÇÃO **

        return data;
    }
}
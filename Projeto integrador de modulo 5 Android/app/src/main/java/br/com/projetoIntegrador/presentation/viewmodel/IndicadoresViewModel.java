package br.com.projetoIntegrador.presentation.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import br.com.projetoIntegrador.network.IndicadoresDto;
import br.com.projetoIntegrador.repository.IndicadoresRepository;

public class IndicadoresViewModel extends AndroidViewModel {

    private IndicadoresRepository indicadoresRepository;
    private LiveData<IndicadoresDto> indicadoresData;

    public IndicadoresViewModel(@NonNull Application application) {
        super(application);
        indicadoresRepository = new IndicadoresRepository();
    }

    public LiveData<IndicadoresDto> getIndicadores() {
        if (indicadoresData == null) {
            indicadoresData = indicadoresRepository.fetchIndicadores();
        }
        return indicadoresData;
    }

    public void refreshIndicadores() {
        // Força a busca por novos dados
        indicadoresData = indicadoresRepository.fetchIndicadores();
    }
}
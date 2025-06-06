package br.com.projetoIntegrador.presentation.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import br.com.projetoIntegrador.network.SpecialtyDto;
import br.com.projetoIntegrador.repository.SpecialtyRepository;

public class SpecialtyViewModel extends AndroidViewModel {
    private SpecialtyRepository specialtyRepository;

    public SpecialtyViewModel(@NonNull Application application) {
        super(application);
        this.specialtyRepository = new SpecialtyRepository();
    }

    public LiveData<List<SpecialtyDto>> listAllSpecialties() {
        return specialtyRepository.listAllSpecialties();
    }

    public LiveData<SpecialtyDto> getSpecialtyById(Long specialtyId) {
        return specialtyRepository.getSpecialtyById(specialtyId);
    }
}
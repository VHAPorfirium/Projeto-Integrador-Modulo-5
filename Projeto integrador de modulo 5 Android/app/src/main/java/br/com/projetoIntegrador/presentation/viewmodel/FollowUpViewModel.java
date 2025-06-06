package br.com.projetoIntegrador.presentation.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import br.com.projetoIntegrador.network.FollowUpDto;
import br.com.projetoIntegrador.repository.FollowUpRepository;

public class FollowUpViewModel extends AndroidViewModel { // Era RetornoViewModel
    private FollowUpRepository followUpRepository;

    public FollowUpViewModel(@NonNull Application application) {
        super(application);
        this.followUpRepository = new FollowUpRepository();
    }

    public LiveData<List<FollowUpDto>> listAllFollowUps() {
        return followUpRepository.listAllFollowUps();
    }

    public LiveData<List<FollowUpDto>> listFollowUpsByPaciente(Long pacienteId) {
        return followUpRepository.listFollowUpsByPaciente(pacienteId);
    }

    public LiveData<FollowUpDto> createFollowUp(FollowUpDto followUpDto) {
        return followUpRepository.createFollowUp(followUpDto);
    }

    public LiveData<FollowUpDto> updateFollowUp(Long followUpId, FollowUpDto followUpDto) {
        return followUpRepository.updateFollowUp(followUpId, followUpDto);
    }

    public LiveData<Boolean> deleteFollowUp(Long followUpId) {
        return followUpRepository.deleteFollowUp(followUpId);
    }

    public LiveData<List<String>> getAvailableFollowUpSlots(String date) {
        return followUpRepository.getAvailableFollowUpSlots(date);
    }
}
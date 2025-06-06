package br.com.projetoIntegrador.presentation.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import br.com.projetoIntegrador.network.PacienteDto;
import br.com.projetoIntegrador.repository.PacienteRepository;

public class PacienteViewModel extends AndroidViewModel {
    private PacienteRepository pacienteRepository;

    public PacienteViewModel(@NonNull Application application) {
        super(application);
        this.pacienteRepository = new PacienteRepository();
    }

    public LiveData<PacienteDto> createPaciente(PacienteDto pacienteDto) {
        return pacienteRepository.createPaciente(pacienteDto);
    }

    public LiveData<List<PacienteDto>> listAllPacientes() {
        return pacienteRepository.listAllPacientes();
    }

    public LiveData<PacienteDto> getPacienteById(Long pacienteId) {
        return pacienteRepository.getPacienteById(pacienteId);
    }

    public LiveData<PacienteDto> updatePaciente(Long pacienteId, PacienteDto pacienteDto) {
        return pacienteRepository.updatePaciente(pacienteId, pacienteDto);
    }

    public LiveData<Boolean> deletePaciente(Long pacienteId) {
        return pacienteRepository.deletePaciente(pacienteId);
    }
}
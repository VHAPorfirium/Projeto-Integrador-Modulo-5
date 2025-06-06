// Arquivo: app/src/main/java/br/com/projetoIntegrador/presentation/viewmodel/AuthViewModel.java

package br.com.projetoIntegrador.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import br.com.projetoIntegrador.network.LoginResponseDto;
import br.com.projetoIntegrador.repository.PacienteRepository;

public class AuthViewModel extends ViewModel {

    private final PacienteRepository repository;

    // CORREÇÃO: O MutableLiveData interno foi removido para evitar memory leaks com observeForever.

    public AuthViewModel() {
        this.repository = new PacienteRepository();
    }

    // CORREÇÃO: A Activity/Fragment irá observar o LiveData retornado diretamente por este método.
    public LiveData<LoginResponseDto> loginFuncionario(String matricula, String senha) {
        return repository.loginFuncionario(matricula, senha);
    }

    // CORREÇÃO: Mesma lógica para o login do paciente.
    public LiveData<LoginResponseDto> loginPaciente(String cpf, String senha) {
        return repository.loginPaciente(cpf, senha);
    }
}
package br.com.projetoIntegrador.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import br.com.projetoIntegrador.network.LoginResponseDto; // Importa o DTO correto
import br.com.projetoIntegrador.repository.PacienteRepository;

public class AuthViewModel extends ViewModel {

    private final PacienteRepository repository;

    public AuthViewModel() {
        this.repository = new PacienteRepository();
    }

    /**
     * CORREÇÃO:
     * O método agora retorna diretamente o LiveData<LoginResponseDto> do repositório.
     * A classe interna duplicada que existia aqui foi removida para evitar conflitos.
     */
    public LiveData<LoginResponseDto> loginFuncionario(String matricula, String senha) {
        return repository.loginFuncionario(matricula, senha);
    }

    public LiveData<LoginResponseDto> loginPaciente(String cpf, String senha) {
        return repository.loginPaciente(cpf, senha);
    }
}
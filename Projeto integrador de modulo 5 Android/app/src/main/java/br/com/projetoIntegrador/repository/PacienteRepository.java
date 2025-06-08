package br.com.projetoIntegrador.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import br.com.projetoIntegrador.network.ApiClient;
import br.com.projetoIntegrador.network.ApiService;
import br.com.projetoIntegrador.network.PacienteDto;
import br.com.projetoIntegrador.network.LoginRequestDto;
import br.com.projetoIntegrador.network.LoginResponseDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.annotation.NonNull;
import com.google.gson.Gson; // Importar Gson
import java.io.IOException; // Importar IOException

public class PacienteRepository {

    private final ApiService apiService;
    private final Gson gson = new Gson(); // Instanciar Gson para parsing de erro

    public PacienteRepository() {
        this.apiService = ApiClient.get();
    }

    public LiveData<LoginResponseDto> loginFuncionario(String matricula, String senha) {
        MutableLiveData<LoginResponseDto> result = new MutableLiveData<>();
        LoginRequestDto loginRequest = new LoginRequestDto(matricula, senha);
        apiService.loginFuncionario(loginRequest).enqueue(new Callback<LoginResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseDto> call, @NonNull Response<LoginResponseDto> response) {
                // CORREÇÃO PRINCIPAL: A lógica de sucesso/falha agora é tratada aqui.
                if (response.isSuccessful() && response.body() != null) {
                    // Se a API respondeu com sucesso (HTTP 200), nós usamos o corpo da resposta.
                    // O próprio DTO nos dirá se o login foi um 'success: true' ou 'success: false'.
                    result.setValue(response.body());
                } else {
                    // Se a resposta não foi bem-sucedida (ex: erro 404, 500), tratamos como falha.
                    LoginResponseDto error = new LoginResponseDto();
                    error.setSuccess(false);
                    error.setMessage("Erro de comunicação com o servidor: " + response.code());
                    result.setValue(error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseDto> call, @NonNull Throwable t) {
                // Erro de rede (sem conexão, timeout, etc.)
                LoginResponseDto error = new LoginResponseDto();
                error.setSuccess(false);
                error.setMessage("Falha na comunicação: " + t.getMessage());
                result.setValue(error);
            }
        });
        return result;
    }

    public LiveData<LoginResponseDto> loginPaciente(String cpf, String senha) {
        MutableLiveData<LoginResponseDto> result = new MutableLiveData<>();
        LoginRequestDto loginRequest = new LoginRequestDto(cpf, senha);
        apiService.loginPaciente(loginRequest).enqueue(new Callback<LoginResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseDto> call, @NonNull Response<LoginResponseDto> response) {
                // Lógica corrigida, igual ao login de funcionário
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    LoginResponseDto error = new LoginResponseDto();
                    error.setSuccess(false);
                    error.setMessage("Erro de comunicação com o servidor: " + response.code());
                    result.setValue(error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseDto> call, @NonNull Throwable t) {
                LoginResponseDto error = new LoginResponseDto();
                error.setSuccess(false);
                error.setMessage("Falha na comunicação: " + t.getMessage());
                result.setValue(error);
            }
        });
        return result;
    }

    // --- Outros métodos do repositório permanecem os mesmos ---

    public LiveData<PacienteDto> createPaciente(PacienteDto pacienteDto) {
        MutableLiveData<PacienteDto> result = new MutableLiveData<>();
        apiService.createPaciente(pacienteDto).enqueue(new Callback<PacienteDto>() {
            @Override
            public void onResponse(@NonNull Call<PacienteDto> call, @NonNull Response<PacienteDto> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<PacienteDto> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<List<PacienteDto>> listAllPacientes() {
        MutableLiveData<List<PacienteDto>> result = new MutableLiveData<>();
        apiService.listAllPacientes().enqueue(new Callback<List<PacienteDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<PacienteDto>> call, @NonNull Response<List<PacienteDto>> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<List<PacienteDto>> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<PacienteDto> getPacienteById(Long pacienteId) {
        MutableLiveData<PacienteDto> result = new MutableLiveData<>();
        apiService.getPacienteById(pacienteId).enqueue(new Callback<PacienteDto>() {
            @Override
            public void onResponse(@NonNull Call<PacienteDto> call, @NonNull Response<PacienteDto> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<PacienteDto> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<PacienteDto> updatePaciente(Long pacienteId, PacienteDto pacienteDto) {
        MutableLiveData<PacienteDto> result = new MutableLiveData<>();
        apiService.updatePaciente(pacienteId, pacienteDto).enqueue(new Callback<PacienteDto>() {
            @Override
            public void onResponse(@NonNull Call<PacienteDto> call, @NonNull Response<PacienteDto> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<PacienteDto> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<Boolean> deletePaciente(Long pacienteId) {
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        apiService.deletePaciente(pacienteId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                success.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                success.setValue(false);
            }
        });
        return success;
    }
}

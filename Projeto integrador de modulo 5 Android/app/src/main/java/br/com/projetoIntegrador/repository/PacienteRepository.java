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

public class PacienteRepository {

    private ApiService apiService;

    public PacienteRepository() {
        this.apiService = ApiClient.get();
    }

    public LiveData<LoginResponseDto> loginPaciente(String cpf, String senha) {
        MutableLiveData<LoginResponseDto> result = new MutableLiveData<>();
        LoginRequestDto loginRequest = new LoginRequestDto(cpf, senha);
        apiService.loginPaciente(loginRequest).enqueue(new Callback<LoginResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseDto> call, @NonNull Response<LoginResponseDto> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                } else {
                    if (response.errorBody() != null) {
                        try {
                            // LINHA CORRIGIDA
                            LoginResponseDto errorResponse = (LoginResponseDto) ApiClient.getRetrofit().responseBodyConverter(LoginResponseDto.class, new java.lang.annotation.Annotation[0]).convert(response.errorBody());
                            result.setValue(errorResponse);
                        } catch (Exception e) {
                            LoginResponseDto error = new LoginResponseDto();
                            error.setSuccess(false);
                            error.setMessage("Erro ao processar login: " + response.code());
                            result.setValue(error);
                        }
                    } else {
                        LoginResponseDto error = new LoginResponseDto();
                        error.setSuccess(false);
                        error.setMessage("Erro de login: " + response.code());
                        result.setValue(error);
                    }
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

    public LiveData<LoginResponseDto> loginFuncionario(String matricula, String senha) {
        MutableLiveData<LoginResponseDto> result = new MutableLiveData<>();
        LoginRequestDto loginRequest = new LoginRequestDto(matricula, senha);
        apiService.loginFuncionario(loginRequest).enqueue(new Callback<LoginResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseDto> call, @NonNull Response<LoginResponseDto> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                } else {
                    if (response.errorBody() != null) {
                        try {
                            // LINHA CORRIGIDA
                            LoginResponseDto errorResponse = (LoginResponseDto) ApiClient.getRetrofit().responseBodyConverter(LoginResponseDto.class, new java.lang.annotation.Annotation[0]).convert(response.errorBody());
                            result.setValue(errorResponse);
                        } catch (Exception e) {
                            LoginResponseDto error = new LoginResponseDto();
                            error.setSuccess(false);
                            error.setMessage("Erro ao processar login: " + response.code());
                            result.setValue(error);
                        }
                    } else {
                        LoginResponseDto error = new LoginResponseDto();
                        error.setSuccess(false);
                        error.setMessage("Erro de login: " + response.code());
                        result.setValue(error);
                    }
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
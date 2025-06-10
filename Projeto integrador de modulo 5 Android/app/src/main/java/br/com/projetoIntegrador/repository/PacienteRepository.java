package br.com.projetoIntegrador.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList; // Importar ArrayList
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
import com.google.gson.Gson;
import java.io.IOException;
import android.util.Log; // Importar Log

public class PacienteRepository {

    private static final String TAG = "PacienteRepository"; // TAG para logs
    private final ApiService apiService;
    private final Gson gson = new Gson();

    public PacienteRepository() {
        this.apiService = ApiClient.get();
    }

    public LiveData<LoginResponseDto> loginFuncionario(String matricula, String senha) {
        MutableLiveData<LoginResponseDto> result = new MutableLiveData<>();
        LoginRequestDto loginRequest = new LoginRequestDto(matricula, senha);
        apiService.loginFuncionario(loginRequest).enqueue(new Callback<LoginResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseDto> call, @NonNull Response<LoginResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "loginFuncionario: Resposta bem-sucedida: " + response.body().getMessage());
                    result.setValue(response.body());
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "loginFuncionario: Resposta não bem-sucedida. Código: " + response.code() + ", Mensagem: " + response.message() + ", Body: " + errorBody);
                    LoginResponseDto error = new LoginResponseDto();
                    error.setSuccess(false);
                    error.setMessage("Erro de comunicação com o servidor: " + response.code() + " - " + response.message());
                    result.setValue(error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseDto> call, @NonNull Throwable t) {
                Log.e(TAG, "loginFuncionario: Falha na comunicação de rede: " + t.getMessage(), t);
                LoginResponseDto error = new LoginResponseDto();
                error.setSuccess(false);
                error.setMessage("Falha na comunicação de rede: " + t.getMessage());
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
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "loginPaciente: Resposta bem-sucedida: " + response.body().getMessage());
                    result.setValue(response.body());
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "loginPaciente: Resposta não bem-sucedida. Código: " + response.code() + ", Mensagem: " + response.message() + ", Body: " + errorBody);
                    LoginResponseDto error = new LoginResponseDto();
                    error.setSuccess(false);
                    error.setMessage("Erro de comunicação com o servidor: " + response.code() + " - " + response.message());
                    result.setValue(error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseDto> call, @NonNull Throwable t) {
                Log.e(TAG, "loginPaciente: Falha na comunicação de rede: " + t.getMessage(), t);
                LoginResponseDto error = new LoginResponseDto();
                error.setSuccess(false);
                error.setMessage("Falha na comunicação de rede: " + t.getMessage());
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
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "createPaciente: Sucesso! Paciente criado: " + response.body().getFullName());
                    result.setValue(response.body());
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "createPaciente: Falha. Código: " + response.code() + ", Mensagem: " + response.message() + ", Body: " + errorBody);
                    result.setValue(null); // Ainda pode ser null se a criação falhar
                }
            }

            @Override
            public void onFailure(@NonNull Call<PacienteDto> call, @NonNull Throwable t) {
                Log.e(TAG, "createPaciente: Falha na rede: " + t.getMessage(), t);
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
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "listAllPacientes: Sucesso! Tamanho da lista: " + response.body().size());
                    result.setValue(response.body());
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "listAllPacientes: Resposta não bem-sucedida. Código: " + response.code() + ", Mensagem: " + response.message() + ", Body: " + errorBody);
                    // IMPORTANTE: Retorna uma lista vazia, não null, para não quebrar a lógica de UI
                    result.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PacienteDto>> call, @NonNull Throwable t) {
                Log.e(TAG, "listAllPacientes: Falha na conexão de rede: " + t.getMessage(), t);
                // IMPORTANTE: Retorna uma lista vazia, não null, para não quebrar a lógica de UI
                result.setValue(new ArrayList<>());
            }
        });
        return result;
    }

    public LiveData<PacienteDto> getPacienteById(Long pacienteId) {
        MutableLiveData<PacienteDto> result = new MutableLiveData<>();
        apiService.getPacienteById(pacienteId).enqueue(new Callback<PacienteDto>() {
            @Override
            public void onResponse(@NonNull Call<PacienteDto> call, @NonNull Response<PacienteDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "getPacienteById: Sucesso para ID " + pacienteId + ": " + response.body().getFullName());
                    result.setValue(response.body());
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "getPacienteById: Falha para ID " + pacienteId + ". Código: " + response.code() + ", Mensagem: " + response.message() + ", Body: " + errorBody);
                    result.setValue(null); // Pode ser null se o paciente não for encontrado
                }
            }

            @Override
            public void onFailure(@NonNull Call<PacienteDto> call, @NonNull Throwable t) {
                Log.e(TAG, "getPacienteById: Falha na rede para ID " + pacienteId + ": " + t.getMessage(), t);
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
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "updatePaciente: Sucesso para ID " + pacienteId + ": " + response.body().getFullName());
                    result.setValue(response.body());
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "updatePaciente: Falha para ID " + pacienteId + ". Código: " + response.code() + ", Mensagem: " + response.message() + ", Body: " + errorBody);
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PacienteDto> call, @NonNull Throwable t) {
                Log.e(TAG, "updatePaciente: Falha na rede para ID " + pacienteId + ": " + t.getMessage(), t);
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
                if (response.isSuccessful()) {
                    Log.d(TAG, "deletePaciente: Sucesso para ID " + pacienteId);
                    success.setValue(true);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "deletePaciente: Falha para ID " + pacienteId + ". Código: " + response.code() + ", Mensagem: " + response.message() + ", Body: " + errorBody);
                    success.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "deletePaciente: Falha na rede para ID " + pacienteId + ": " + t.getMessage(), t);
                success.setValue(false);
            }
        });
        return success;
    }
}
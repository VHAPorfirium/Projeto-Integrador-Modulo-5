package br.com.projetoIntegrador.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import br.com.projetoIntegrador.network.ApiClient;
import br.com.projetoIntegrador.network.ApiService;
import br.com.projetoIntegrador.network.FollowUpDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.annotation.NonNull;

public class FollowUpRepository {

    private ApiService apiService;

    public FollowUpRepository() {
        this.apiService = ApiClient.get();
    }

    public LiveData<List<FollowUpDto>> listAllFollowUps() {
        MutableLiveData<List<FollowUpDto>> result = new MutableLiveData<>();
        apiService.listAllFollowUps().enqueue(new Callback<List<FollowUpDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<FollowUpDto>> call, @NonNull Response<List<FollowUpDto>> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<List<FollowUpDto>> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<List<FollowUpDto>> listFollowUpsByPaciente(Long pacienteId) {
        MutableLiveData<List<FollowUpDto>> result = new MutableLiveData<>();
        apiService.listFollowUpsByPaciente(pacienteId).enqueue(new Callback<List<FollowUpDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<FollowUpDto>> call, @NonNull Response<List<FollowUpDto>> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<List<FollowUpDto>> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<FollowUpDto> createFollowUp(FollowUpDto followUpDto) {
        MutableLiveData<FollowUpDto> result = new MutableLiveData<>();
        apiService.createFollowUp(followUpDto).enqueue(new Callback<FollowUpDto>() {
            @Override
            public void onResponse(@NonNull Call<FollowUpDto> call, @NonNull Response<FollowUpDto> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<FollowUpDto> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<FollowUpDto> updateFollowUp(Long followUpId, FollowUpDto followUpDto) {
        MutableLiveData<FollowUpDto> result = new MutableLiveData<>();
        apiService.updateFollowUp(followUpId, followUpDto).enqueue(new Callback<FollowUpDto>() {
            @Override
            public void onResponse(@NonNull Call<FollowUpDto> call, @NonNull Response<FollowUpDto> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<FollowUpDto> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<Boolean> deleteFollowUp(Long followUpId) {
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        apiService.deleteFollowUp(followUpId).enqueue(new Callback<Void>() {
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

    public LiveData<List<String>> getAvailableFollowUpSlots(String date) {
        MutableLiveData<List<String>> result = new MutableLiveData<>();
        apiService.getAvailableFollowUpSlots(date).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }
            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }
}
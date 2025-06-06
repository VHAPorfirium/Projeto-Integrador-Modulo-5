package br.com.projetoIntegrador.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import br.com.projetoIntegrador.network.ApiClient;
import br.com.projetoIntegrador.network.ApiService;
import br.com.projetoIntegrador.network.SpecialtyDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.annotation.NonNull;

public class SpecialtyRepository {

    private ApiService apiService;

    public SpecialtyRepository() {
        this.apiService = ApiClient.get();
    }

    public LiveData<List<SpecialtyDto>> listAllSpecialties() {
        MutableLiveData<List<SpecialtyDto>> result = new MutableLiveData<>();
        apiService.listAllSpecialties().enqueue(new Callback<List<SpecialtyDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<SpecialtyDto>> call, @NonNull Response<List<SpecialtyDto>> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<List<SpecialtyDto>> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<SpecialtyDto> getSpecialtyById(Long specialtyId) {
        MutableLiveData<SpecialtyDto> result = new MutableLiveData<>();
        apiService.getSpecialtyById(specialtyId).enqueue(new Callback<SpecialtyDto>() {
            @Override
            public void onResponse(@NonNull Call<SpecialtyDto> call, @NonNull Response<SpecialtyDto> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<SpecialtyDto> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }
}
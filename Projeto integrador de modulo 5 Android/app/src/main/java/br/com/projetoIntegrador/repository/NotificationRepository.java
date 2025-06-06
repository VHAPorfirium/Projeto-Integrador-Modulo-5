package br.com.projetoIntegrador.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import br.com.projetoIntegrador.network.ApiClient;
import br.com.projetoIntegrador.network.ApiService;
import br.com.projetoIntegrador.network.NotificationRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.annotation.NonNull;

public class NotificationRepository {
    private ApiService apiService;

    public NotificationRepository() {
        this.apiService = ApiClient.get();
    }

    public LiveData<String> sendNotification(NotificationRequest request) {
        MutableLiveData<String> resultLiveData = new MutableLiveData<>();
        apiService.sendNotification(request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    resultLiveData.setValue(response.body());
                } else {
                    resultLiveData.setValue("Erro: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                resultLiveData.setValue("Falha: " + t.getMessage());
            }
        });
        return resultLiveData;
    }
}
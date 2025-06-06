package br.com.projetoIntegrador.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import br.com.projetoIntegrador.network.ApiClient;
import br.com.projetoIntegrador.network.ApiService;
import br.com.projetoIntegrador.network.DeviceToken;
import br.com.projetoIntegrador.network.TokenRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.annotation.NonNull;

public class TokenRepository {

    private ApiService apiService;

    public TokenRepository() {
        this.apiService = ApiClient.get();
    }

    public LiveData<DeviceToken> registerToken(Long pacienteId, String token) {
        MutableLiveData<DeviceToken> deviceTokenLiveData = new MutableLiveData<>();
        TokenRequest request = new TokenRequest(pacienteId, token);
        apiService.registerToken(request).enqueue(new Callback<DeviceToken>() {
            @Override
            public void onResponse(@NonNull Call<DeviceToken> call, @NonNull Response<DeviceToken> response) {
                if (response.isSuccessful()) {
                    deviceTokenLiveData.setValue(response.body());
                } else {
                    deviceTokenLiveData.setValue(null); // Indica erro
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeviceToken> call, @NonNull Throwable t) {
                deviceTokenLiveData.setValue(null); // Indica erro
            }
        });
        return deviceTokenLiveData;
    }

    public LiveData<List<DeviceToken>> listTokensByPaciente(Long pacienteId) {
        MutableLiveData<List<DeviceToken>> tokensLiveData = new MutableLiveData<>();
        apiService.listTokensByPaciente(pacienteId).enqueue(new Callback<List<DeviceToken>>() {
            @Override
            public void onResponse(@NonNull Call<List<DeviceToken>> call, @NonNull Response<List<DeviceToken>> response) {
                if (response.isSuccessful()) {
                    tokensLiveData.setValue(response.body());
                } else {
                    tokensLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<DeviceToken>> call, @NonNull Throwable t) {
                tokensLiveData.setValue(null);
            }
        });
        return tokensLiveData;
    }

    public LiveData<Boolean> deleteToken(Long tokenId) {
        MutableLiveData<Boolean> successLiveData = new MutableLiveData<>();
        apiService.deleteToken(tokenId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                successLiveData.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                successLiveData.setValue(false);
            }
        });
        return successLiveData;
    }
}
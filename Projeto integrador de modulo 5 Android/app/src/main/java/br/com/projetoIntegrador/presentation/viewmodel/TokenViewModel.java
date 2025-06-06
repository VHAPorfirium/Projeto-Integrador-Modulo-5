package br.com.projetoIntegrador.presentation.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import br.com.projetoIntegrador.network.DeviceToken;
import br.com.projetoIntegrador.repository.TokenRepository;

public class TokenViewModel extends AndroidViewModel {
    private TokenRepository tokenRepository;

    public TokenViewModel(@NonNull Application application) {
        super(application);
        this.tokenRepository = new TokenRepository();
    }

    public LiveData<DeviceToken> registerToken(Long pacienteId, String token) {
        return tokenRepository.registerToken(pacienteId, token);
    }

    public LiveData<List<DeviceToken>> listTokensByPaciente(Long pacienteId) {
        return tokenRepository.listTokensByPaciente(pacienteId);
    }

    public LiveData<Boolean> deleteToken(Long tokenId) {
        return tokenRepository.deleteToken(tokenId);
    }
}
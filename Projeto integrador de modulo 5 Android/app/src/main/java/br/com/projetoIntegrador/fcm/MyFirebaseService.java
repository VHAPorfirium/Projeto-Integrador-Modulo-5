package br.com.projetoIntegrador.fcm;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import br.com.projetoIntegrador.network.ApiClient;
import br.com.projetoIntegrador.network.ApiService;
import br.com.projetoIntegrador.network.TokenRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseService";
    // substitua pelo ID real do usuário, ou recupere do seu SharedPrefs
    private static final String USER_ID = "ID_DO_USUARIO";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Novo token: " + token);

        // envia o token ao back-end
        ApiService api = ApiClient.get();
        api.registerToken(new TokenRequest(USER_ID, token))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d(TAG, "Token registrado com sucesso");
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Falha ao registrar token", t);
                    }
                });
    }

    @Override
    public void onMessageReceived(RemoteMessage msg) {
        super.onMessageReceived(msg);
        String title = msg.getNotification() != null
                ? msg.getNotification().getTitle()
                : "Notificação";
        String body  = msg.getNotification() != null
                ? msg.getNotification().getBody()
                : "";

        showSystemNotification(title, body);
    }

    private void showSystemNotification(String title, String body) {
    }
}

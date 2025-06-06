package br.com.projetoIntegrador.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

// Adapte o R para o seu pacote real
import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.ApiClient;
import br.com.projetoIntegrador.network.ApiService;
import br.com.projetoIntegrador.network.DeviceToken;
import br.com.projetoIntegrador.network.TokenRequest;

// Importe sua MainActivity ou a Activity que deve abrir ao clicar na notificação
import br.com.projetoIntegrador.presentation.ui.activity.MainActivity;

import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseService";

    // Chave para recuperar o ID do Paciente das SharedPreferences
    // É uma boa prática definir essas constantes em um arquivo/classe separada de Constantes.
    public static final String SHARED_PREFS_NAME = "ProjetoIntegradorPrefs";
    public static final String PACIENTE_ID_KEY = "pacienteId"; // Corrigido para "pacienteId" (camelCase)
    public static final Long DEFAULT_PACIENTE_ID = -1L; // Valor padrão se não encontrado


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Novo token FCM: " + token);
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        Long pacienteId = getPacienteIdFromPrefs();

        if (pacienteId.equals(DEFAULT_PACIENTE_ID)) {
            Log.w(TAG, "ID do Paciente não encontrado nas SharedPreferences. Não é possível registrar o token.");
            return;
        }

        ApiService apiService = ApiClient.get();
        TokenRequest tokenRequest = new TokenRequest(pacienteId, token); // Usa Long para pacienteId

        apiService.registerToken(tokenRequest).enqueue(new Callback<DeviceToken>() { // Espera DeviceToken
            @Override
            public void onResponse(@NonNull Call<DeviceToken> call, @NonNull Response<DeviceToken> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Token registrado com sucesso no servidor. Token ID: " + response.body().getId());
                } else {
                    Log.e(TAG, "Falha ao registrar token no servidor. Código: " + response.code() + " Mensagem: " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Corpo do erro: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao analisar o corpo do erro", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeviceToken> call, @NonNull Throwable t) {
                Log.e(TAG, "Falha na chamada de registrar token", t);
            }
        });
    }

    private Long getPacienteIdFromPrefs() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        return prefs.getLong(PACIENTE_ID_KEY, DEFAULT_PACIENTE_ID);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "De: " + remoteMessage.getFrom());

        String title = "Nova Notificação"; // Título padrão
        String body = ""; // Corpo padrão

        // Verifica se a mensagem contém um payload de notificação.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Título da Notificação: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Corpo da Notificação: " + remoteMessage.getNotification().getBody());
            if (remoteMessage.getNotification().getTitle() != null) {
                title = remoteMessage.getNotification().getTitle();
            }
            if (remoteMessage.getNotification().getBody() != null) {
                body = remoteMessage.getNotification().getBody();
            }
        }

        // Verifica se a mensagem contém um payload de dados.
        Map<String, String> dataPayload = remoteMessage.getData();
        if (dataPayload.size() > 0) {
            Log.d(TAG, "Payload de dados da mensagem: " + dataPayload);
            // Você pode extrair dados personalizados aqui, se necessário para a notificação
            // Por exemplo, se título/corpo estiverem no payload de dados:
            // title = dataPayload.getOrDefault("custom_title", title);
            // body = dataPayload.getOrDefault("custom_body", body);

            // Exemplo: verificar se é uma chamada para o paciente
            if ("CHAMADA_PACIENTE".equals(dataPayload.get("action"))) {
                // Lógica específica para quando o paciente é chamado
                // Pode ser útil para abrir diretamente o ConfirmacaoFragment
                // ou atualizar a UI do FilaFragment.
                // Você pode usar um LocalBroadcastManager para comunicar com a Activity/Fragment ativa.
            }
        }

        showSystemNotification(title, body, dataPayload);
    }

    private void showSystemNotification(String title, String body, Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class); // Mude para a Activity de destino desejada
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Passa dados para a intent, se necessário
        for (Map.Entry<String, String> entry : data.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id); // Defina isso em strings.xml
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher) // Substitua pelo ícone do seu app
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Desde o Android Oreo (API 26), o canal de notificação é obrigatório.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Notificações Gerais", // Nome do canal legível por humanos
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID da notificação */, notificationBuilder.build());
    }
}
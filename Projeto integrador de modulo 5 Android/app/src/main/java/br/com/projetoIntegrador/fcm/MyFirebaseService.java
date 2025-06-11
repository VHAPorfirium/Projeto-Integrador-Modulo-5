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
import androidx.localbroadcastmanager.content.LocalBroadcastManager; // Adicionado para comunicação local

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import br.com.projetoIntegrador.R;
import br.com.projetoIntegrador.network.ApiClient;
import br.com.projetoIntegrador.network.ApiService;
import br.com.projetoIntegrador.network.DeviceToken;
import br.com.projetoIntegrador.network.TokenRequest;

import br.com.projetoIntegrador.presentation.ui.activity.MainActivity;
import br.com.projetoIntegrador.presentation.ui.activity.PacienteDashboardActivity; // Exemplo de Activity do paciente
import br.com.projetoIntegrador.presentation.ui.fragment.ConfirmacaoFragment; // Se você tiver um fragment de confirmação

import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseService";

    public static final String SHARED_PREFS_NAME = "ProjetoIntegradorPrefs";
    public static final String PACIENTE_ID_KEY = "pacienteId";
    public static final Long DEFAULT_PACIENTE_ID = -1L;
    public static final String ACTION_PATIENT_CALLED = "br.com.projetoIntegrador.ACTION_PATIENT_CALLED"; // Nova constante para Broadcast
    public static final String EXTRA_ATTENDANCE_ID = "attendanceEntryId"; // Para passar o ID da entrada

    // Chave para recuperar o ID do funcionário logado
    public static final String FUNCIONARIO_ID_KEY = "FUNCIONARIO_ID_KEY"; // Adicionada esta constante

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Novo token FCM: " + token);
        // Só envia o token se um paciente estiver logado
        Long pacienteId = getPacienteIdFromPrefs();
        if (!pacienteId.equals(DEFAULT_PACIENTE_ID)) {
            sendTokenToServer(token);
        } else {
            Log.w(TAG, "onNewToken: Paciente não logado, não registrando token no servidor.");
        }
    }

    private void sendTokenToServer(String token) {
        Long pacienteId = getPacienteIdFromPrefs();

        if (pacienteId.equals(DEFAULT_PACIENTE_ID)) {
            Log.w(TAG, "sendTokenToServer: ID do Paciente não encontrado nas SharedPreferences. Não é possível registrar o token.");
            return;
        }

        ApiService apiService = ApiClient.get();
        TokenRequest tokenRequest = new TokenRequest(pacienteId, token);

        apiService.registerToken(tokenRequest).enqueue(new Callback<DeviceToken>() {
            @Override
            public void onResponse(@NonNull Call<DeviceToken> call, @NonNull Response<DeviceToken> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "sendTokenToServer: Token registrado com sucesso no servidor. Token ID: " + response.body().getId());
                } else {
                    Log.e(TAG, "sendTokenToServer: Falha ao registrar token no servidor. Código: " + response.code() + " Mensagem: " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "sendTokenToServer: Corpo do erro: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "sendTokenToServer: Erro ao analisar o corpo do erro", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeviceToken> call, @NonNull Throwable t) {
                Log.e(TAG, "sendTokenToServer: Falha na chamada de registrar token", t);
            }
        });
    }

    private Long getPacienteIdFromPrefs() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        return prefs.getLong(PACIENTE_ID_KEY, DEFAULT_PACIENTE_ID);
    }

    // Adicionado método para obter o ID do funcionário logado (para uso em outras partes, se necessário)
    public static Long getLoggedFuncionaroId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        return prefs.getLong(FUNCIONARIO_ID_KEY, -1L);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "onMessageReceived: De: " + remoteMessage.getFrom());

        String title = "Nova Notificação";
        String body = "";
        Map<String, String> dataPayload = remoteMessage.getData();

        // Prefere dados do payload de dados se existirem, caso contrário usa o de notificação
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle() != null ? remoteMessage.getNotification().getTitle() : title;
            body = remoteMessage.getNotification().getBody() != null ? remoteMessage.getNotification().getBody() : body;
            Log.d(TAG, "onMessageReceived: Notificação recebida - Título: " + title + ", Corpo: " + body);
        }

        if (dataPayload.size() > 0) {
            Log.d(TAG, "onMessageReceived: Payload de dados da mensagem: " + dataPayload);
            // Sobrescreve título e corpo se presentes no data payload
            title = dataPayload.getOrDefault("title", title);
            body = dataPayload.getOrDefault("body", body);

            // Lógica específica para a chamada do paciente
            if ("CHAMADA_PACIENTE".equals(dataPayload.get("action"))) {
                String attendanceEntryId = dataPayload.get(EXTRA_ATTENDANCE_ID); // Obtém o ID da entrada de atendimento
                String messageFromDoctor = dataPayload.get("message"); // A mensagem personalizada do médico

                Log.i(TAG, "onMessageReceived: Ação CHAMADA_PACIENTE recebida. Entrada ID: " + attendanceEntryId + ", Mensagem: " + messageFromDoctor);

                // Prepara a notificação do sistema com a mensagem do médico
                showSystemNotification(title, messageFromDoctor, dataPayload);

                // Opcional: Enviar um Broadcast local para a UI do paciente
                // Se o PacienteDashboardActivity ou FilaFragment estiverem abertos,
                // eles podem receber este broadcast e atualizar a UI.
                Intent broadcastIntent = new Intent(ACTION_PATIENT_CALLED);
                broadcastIntent.putExtra(EXTRA_ATTENDANCE_ID, attendanceEntryId);
                broadcastIntent.putExtra("notification_message", messageFromDoctor);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                // Você pode querer abrir uma tela de confirmação específica aqui,
                // ou direcionar para o fragment de confirmação no PacienteDashboardActivity.
                // Exemplo: Se quiser ir direto para a tela de confirmação (ajuste o nome da sua Activity/Fragment)
                // if (getPacienteIdFromPrefs() != DEFAULT_PACIENTE_ID) { // Apenas se o paciente estiver logado
                //    Intent confirmationIntent = new Intent(this, PacienteDashboardActivity.class); // Ou ConfirmacaoActivity
                //    confirmationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //    confirmationIntent.putExtra("destination_fragment", "ConfirmacaoFragment"); // Um extra para indicar o fragment
                //    confirmationIntent.putExtra(EXTRA_ATTENDANCE_ID, attendanceEntryId);
                //    startActivity(confirmationIntent);
                // }

            } else {
                // Notificação genérica, exibe como tal
                showSystemNotification(title, body, dataPayload);
            }
        } else {
            // Se não há data payload, apenas exibe a notificação padrão
            showSystemNotification(title, body, dataPayload);
        }
    }

    private void showSystemNotification(String title, String body, Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class); // Por padrão, abre a MainActivity
        // Se a notificação for de chamada, pode direcionar para uma Activity/Fragment específica do paciente
        if ("CHAMADA_PACIENTE".equals(data.get("action"))) {
            intent = new Intent(this, PacienteDashboardActivity.class); // Por exemplo, a tela principal do paciente
            intent.putExtra("open_fragment", "ConfirmacaoFragment"); // Se ConfirmacaoFragment for um fragmento
            intent.putExtra(EXTRA_ATTENDANCE_ID, data.get(EXTRA_ATTENDANCE_ID)); // Passa o ID da entrada
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Notificações da Clínica", // Nome do canal mais específico
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID da notificação, use um ID único se quiser várias notificações */, notificationBuilder.build());
        Log.d(TAG, "showSystemNotification: Notificação do sistema exibida. Título: " + title + ", Corpo: " + body);
    }
}
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

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map; // Pessoal, adicionem este import para o remoteMessage.getData()

import br.com.projetoIntegrador.R; // Equipe, assumindo que a classe R está neste caminho
import br.com.projetoIntegrador.network.ApiClient;
import br.com.projetoIntegrador.network.ApiService;
import br.com.projetoIntegrador.network.DeviceToken; // Para a resposta do registerToken
import br.com.projetoIntegrador.network.TokenRequest;
import br.com.projetoIntegrador.presentation.ui.activity.MainActivity; // Ou a Activity que vocês querem abrir ao tocar na notificação

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseService extends FirebaseMessagingService {

    // Pessoal, usem esta TAG para os logs do nosso Firebase Service.
    private static final String TAG = "MyFirebaseService";

    // Equipe, estas são as chaves que vamos usar para o SharedPreferences.
    // `PACIENTE_ID_KEY` é para o ID do paciente, que é um Long.
    private static final String SHARED_PREFS_NAME = "ProjetoIntegradorPrefs"; // Escolham um nome único para o arquivo de preferências
    private static final String PACIENTE_ID_KEY = "pacienteId"; // Chave para armazenar o pacienteId (Long)
    private static final Long DEFAULT_PACIENTE_ID = -1L; // Valor padrão se o ID não for encontrado ou o usuário não estiver logado

    /**
     * Pessoal, este método é chamado quando um novo token FCM é gerado ou o token existente é atualizado.
     * Precisamos garantir que este novo token seja enviado e registrado no nosso backend.
     * @param token O novo token FCM.
     */
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Novo token FCM recebido: " + token);
        // Chamem o método para enviar este token para o servidor.
        sendTokenToServer(token);
    }

    /**
     * Equipe, este método é responsável por enviar o token FCM para o nosso servidor backend.
     * Primeiro, precisamos obter o `pacienteId` que deve estar salvo nas SharedPreferences após o login.
     * Se o `pacienteId` não for encontrado (usuário não logado), apenas registramos um aviso e não fazemos nada.
     * Se tivermos o `pacienteId`, criamos um objeto `TokenRequest` (lembrem-se que o `pacienteId` é do tipo Long para a API [cite: 230])
     * e fazemos a chamada assíncrona para o endpoint `registerToken` do nosso `ApiService`.
     * No callback, precisamos tratar a resposta: se for sucesso (código 2xx) e tiver corpo, logamos que o token foi registrado.
     * Se a resposta não for bem-sucedida, logamos o código de erro e, se possível, a mensagem de erro do corpo da resposta para ajudar no debug.
     * Também precisamos tratar falhas na própria chamada de rede (ex: sem conexão).
     * @param token O token FCM a ser enviado.
     */
    private void sendTokenToServer(String token) {
        // Peguem o pacienteId das SharedPreferences.
        Long pacienteId = getPacienteIdFromPrefs();

        // Se não encontrarmos o ID do paciente (ex: usuário não logado), não podemos registrar o token.
        // Apenas loguem um aviso. O token será registrado na próxima vez que o app iniciar com um usuário logado, ou no login.
        if (pacienteId.equals(DEFAULT_PACIENTE_ID)) {
            Log.w(TAG, "ID do Paciente não encontrado nas SharedPreferences. O token não pode ser registrado no servidor até o login.");
            return;
        }

        // Obtenham a instância do nosso ApiService.
        ApiService apiService = ApiClient.get();
        // Montem o objeto TokenRequest. Lembrem-se que a API espera 'pacienteId' como Long. [cite: 230]
        TokenRequest tokenRequest = new TokenRequest(pacienteId, token);

        // Façam a chamada para registrar o token.
        // A API /tokens (POST) retorna o DeviceToken criado. [cite: 115, 116]
        apiService.registerToken(tokenRequest).enqueue(new Callback<DeviceToken>() {
            @Override
            public void onResponse(Call<DeviceToken> call, Response<DeviceToken> response) {
                // Verifiquem se a resposta foi bem-sucedida e se há um corpo na resposta.
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Token registrado com sucesso no servidor. ID do Token do Dispositivo: " + response.body().getId());
                } else {
                    // Se falhar, loguem o código de erro.
                    Log.e(TAG, "Falha ao registrar token no servidor. Código: " + response.code());
                    // Tentem logar o corpo do erro, pode ajudar a debugar.
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Corpo do erro: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Erro ao fazer parse do corpo do erro", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DeviceToken> call, Throwable t) {
                // Se houver uma falha na comunicação (ex: sem internet), loguem o erro.
                Log.e(TAG, "Falha na chamada para registrar token", t);
            }
        });
    }

    /**
     * Equipe, este método auxiliar busca o `pacienteId` salvo nas SharedPreferences.
     * Usem o nome do arquivo de preferências e a chave que definimos.
     * Se a chave não existir, ele retorna o `DEFAULT_PACIENTE_ID`.
     * @return O ID do paciente salvo, ou o valor padrão se não encontrado.
     */
    private Long getPacienteIdFromPrefs() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        return prefs.getLong(PACIENTE_ID_KEY, DEFAULT_PACIENTE_ID);
    }

    /**
     * Pessoal, este método é chamado quando o dispositivo recebe uma mensagem FCM enquanto o app está
     * em primeiro plano ou em segundo plano (dependendo de como a notificação é enviada do backend).
     * @param remoteMessage O objeto contendo os dados da mensagem recebida.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Mensagem recebida de: " + remoteMessage.getFrom());

        String title = "Nova Notificação"; // Título padrão
        String body = ""; // Corpo padrão

        // Primeiro, verifiquem se a mensagem contém um 'notification payload'.
        // O Firebase Console geralmente envia por aqui.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Título da Notificação: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Corpo da Notificação: " + remoteMessage.getNotification().getBody());
            // Atribuam o título e corpo da notificação, se existirem.
            if (remoteMessage.getNotification().getTitle() != null) {
                title = remoteMessage.getNotification().getTitle();
            }
            if (remoteMessage.getNotification().getBody() != null) {
                body = remoteMessage.getNotification().getBody();
            }
        }

        // Depois, verifiquem se a mensagem contém um 'data payload'.
        // É comum enviarmos dados customizados por aqui do nosso backend.
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            Log.d(TAG, "Payload de dados da mensagem: " + data);
            // Aqui vocês podem extrair dados customizados do mapa 'data'.
            // Por exemplo, se o backend enviar 'custom_title' e 'custom_body' no data payload:
            // title = data.getOrDefault("custom_title", title);
            // body = data.getOrDefault("custom_body", body);
        }

        // Chamem o método para construir e exibir a notificação no sistema.
        showSystemNotification(title, body, data);
    }

    /**
     * Equipe, este método é responsável por criar e exibir a notificação na barra de status do Android.
     * @param title O título da notificação.
     * @param body O corpo da mensagem da notificação.
     * @param data Um mapa contendo dados adicionais que podem ser usados para navegação ou lógica no app.
     */
    private void showSystemNotification(String title, String body, Map<String, String> data) {
        // Definam o Intent que será disparado quando o usuário tocar na notificação.
        // Geralmente, abre uma Activity específica. Ajustem para a MainActivity ou a tela de destino.
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Limpa a pilha de activities

        // Passem os dados do 'data payload' da FCM para o Intent, se necessário.
        // A Activity que for aberta poderá então acessar esses extras.
        for (Map.Entry<String, String> entry : data.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }

        // Criem o PendingIntent. É importante usar FLAG_IMMUTABLE ou FLAG_MUTABLE conforme necessário.
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Definam um ID para o canal de notificação (obrigatório a partir do Android Oreo).
        // Coloquem este ID como um recurso string em strings.xml.
        String channelId = getString(R.string.default_notification_channel_id);
        // Peguem o som padrão de notificação.
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Construam a notificação usando NotificationCompat.Builder.
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher) // IMPORTANTE: Substituam pelo ícone de notificação do app!
                        .setContentTitle(title) // Título que aparece na notificação.
                        .setContentText(body) // Texto principal da notificação.
                        .setAutoCancel(true) // A notificação some quando o usuário toca nela.
                        .setSound(defaultSoundUri) // Som da notificação.
                        .setContentIntent(pendingIntent) // Ação ao tocar na notificação.
                        .setPriority(NotificationCompat.PRIORITY_HIGH); // Prioridade para heads-up notification.

        // Obtenham o NotificationManager do sistema.
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // A partir do Android Oreo (API 26), é OBRIGATÓRIO criar um NotificationChannel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Notificações Gerais", // Nome do canal visível para o usuário nas configurações do app.
                    NotificationManager.IMPORTANCE_HIGH); // Importância do canal.
            notificationManager.createNotificationChannel(channel);
        }

        // Exibam a notificação. O ID da notificação (primeiro parâmetro) permite atualizar ou cancelar esta notificação depois, se necessário.
        notificationManager.notify(0 /* ID da notificação */, notificationBuilder.build());
    }
}
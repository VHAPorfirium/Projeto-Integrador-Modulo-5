package br.com.projetoIntegrador.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import br.com.projetoIntegrador.dto.NotificationRequest;
import br.com.projetoIntegrador.model.DeviceToken;
import br.com.projetoIntegrador.service.TokenService;

@Service
public class NotificationService {

        private final TokenService tokenService;
        private final FirebaseMessaging messaging;

        @Autowired
        public NotificationService(TokenService tokenService,
                        FirebaseMessaging messaging) {
                this.tokenService = tokenService;
                this.messaging = messaging;
        }

        public int send(NotificationRequest req) throws Exception {
                System.out.println("NotificationService: Recebido pacienteId: " + req.getPacienteId());
                System.out.println("NotificationService: Recebido title: " + req.getTitle());
                System.out.println("NotificationService: Recebido body: " + req.getBody());
                System.out.println("NotificationService: Recebido data: " + req.getData());

                long pacienteId = Long.parseLong(req.getPacienteId());
                List<DeviceToken> tokens = tokenService.findByPaciente(pacienteId);

                if (tokens.isEmpty()) {
                        throw new IllegalArgumentException("Nenhum token registrado para paciente " + pacienteId);
                }

                // Prepara título e corpo (com fallback no data.message)
                String notificationTitle = (req.getTitle() != null && !req.getTitle().isBlank())
                                ? req.getTitle().trim()
                                : "Notificação Importante";
                String notificationBody = (req.getBody() != null && !req.getBody().isBlank())
                                ? req.getBody().trim()
                                : "Você tem uma nova notificação.";
                if (notificationBody.equals("Você tem uma nova notificação.")
                                && req.getData() != null
                                && req.getData().getOrDefault("message", "").isBlank() == false) {
                        notificationBody = req.getData().get("message").trim();
                }

                // Monta a notificação reutilizável
                Notification note = Notification.builder()
                                .setTitle(notificationTitle)
                                .setBody(notificationBody)
                                .build();

                // Envia uma mensagem por token (usa o v1 endpoint:
                // /v1/projects/{projectId}/messages:send)
                int successCount = 0;
                for (DeviceToken dt : tokens) {
                        Message msg = Message.builder()
                                        .setToken(dt.getToken())
                                        .setNotification(note)
                                        .putAllData(req.getData() != null ? req.getData() : Map.of())
                                        .build();
                        try {
                                String response = messaging.send(msg);
                                System.out.println("Mensagem enviada para token [" + dt.getToken() + "]: " + response);
                                successCount++;
                        } catch (FirebaseMessagingException e) {
                                System.err.println("Falha ao enviar para token [" + dt.getToken() + "]: "
                                                + e.getMessage());
                        }
                }

                System.out.println("Total de mensagens enviadas com sucesso: " + successCount);
                return successCount;
        }
}

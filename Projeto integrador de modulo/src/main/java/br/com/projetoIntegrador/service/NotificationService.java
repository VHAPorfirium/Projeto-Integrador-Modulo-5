package br.com.projetoIntegrador.service;

import br.com.projetoIntegrador.dto.NotificationRequest;
import br.com.projetoIntegrador.model.DeviceToken;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final TokenService tokenService;
    private final FirebaseMessaging messaging;

    public NotificationService(TokenService tokenService, FirebaseMessaging messaging) {
        this.tokenService = tokenService;
        this.messaging = messaging;
    }

    public int send(NotificationRequest req) throws Exception {
        List<DeviceToken> tokens = tokenService.findByUser(req.getUserId());
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Nenhum token registrado para o usuário");
        }

        List<String> registrationTokens = tokens.stream()
                .map(DeviceToken::getToken)
                .collect(Collectors.toList());

        Notification fcmNotification = Notification.builder()
                .setTitle(req.getTitle())
                .setBody(req.getBody())
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(registrationTokens)
                .setNotification(fcmNotification)
                .putAllData(req.getData() != null ? req.getData() : Map.of())
                .build();


        BatchResponse response = messaging.sendMulticast(message);
        return response.getSuccessCount();
    }
}

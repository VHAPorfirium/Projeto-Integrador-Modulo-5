package br.com.projetoIntegrador.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;

import br.com.projetoIntegrador.dto.NotificationRequest;
import br.com.projetoIntegrador.model.DeviceToken;

// Essa classe tem a funcionalidade de gerenciar o envio de notificações para dispositivos de pacientes.
@Service
public class NotificationService {

    private final TokenService tokenService;
    private final FirebaseMessaging messaging;

    public NotificationService(
            TokenService tokenService,
            FirebaseMessaging messaging) {
        this.tokenService = tokenService;
        this.messaging = messaging;
    }

    // Envia notificações usando Firebase para os tokens do paciente e retorna a quantidade enviada.
    public int send(NotificationRequest req) throws Exception {
        List<DeviceToken> tokens = tokenService.findByPaciente(Long.valueOf(req.getPacienteId()));
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Nenhum token registrado");
        }
        List<String> reg = tokens.stream()
                .map(DeviceToken::getToken)
                .collect(Collectors.toList());
        Notification note = Notification.builder()
                .setTitle(req.getTitle())
                .setBody(req.getBody())
                .build();
        MulticastMessage msg = MulticastMessage.builder()
                .addAllTokens(reg)
                .setNotification(note)
                .putAllData(req.getData() != null ? req.getData() : Map.of())
                .build();
        return messaging.sendEachForMulticast(msg).getSuccessCount();
    }

}

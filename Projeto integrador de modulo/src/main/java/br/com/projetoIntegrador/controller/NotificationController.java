package br.com.projetoIntegrador.controller;

import br.com.projetoIntegrador.dto.NotificationRequest;
import br.com.projetoIntegrador.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Essa classe tem a funcionalidade de expor endpoint para envio de notificações via Firebase.
@RestController
@RequestMapping("/notificações")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // Recebe a requisição de notificação, dispara o envio e retorna a quantidade enviada.
    @PostMapping
    public ResponseEntity<String> send(
            @RequestBody @Valid NotificationRequest req) throws Exception {
        int count = service.send(req);
        return ResponseEntity.ok("Notificações enviadas: " + count);
    }
}

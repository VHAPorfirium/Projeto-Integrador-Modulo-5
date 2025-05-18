package br.com.projetoIntegrador.controller;

import br.com.projetoIntegrador.dto.NotificationRequest;
import br.com.projetoIntegrador.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> send(@RequestBody @Valid NotificationRequest req) throws Exception {
        int successCount = service.send(req);
        return ResponseEntity.ok("Notificações enviadas com sucesso: " + successCount);
    }
}

package br.com.projetoIntegrador.controller;

import br.com.projetoIntegrador.dto.TokenRequest;
import br.com.projetoIntegrador.model.DeviceToken;
import br.com.projetoIntegrador.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Essa classe tem a funcionalidade de expor endpoints para gerenciamento de tokens de dispositivo.
@RestController
@RequestMapping("/tokens")
public class TokenController {

    private final TokenService service;

    public TokenController(TokenService service) {
        this.service = service;
    }

    // Registra um novo token de dispositivo para notificações.
    @PostMapping
    public ResponseEntity<DeviceToken> create(
            @RequestBody @Valid TokenRequest req) {
        return ResponseEntity.ok(service.save(req));
    }

    // Retorna todos os tokens de dispositivo associados a um paciente.
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<DeviceToken>> listByPatient(
            @PathVariable Long pacienteId) {
        return ResponseEntity.ok(service.findByPaciente(pacienteId));
    }

    // Remove um token de dispositivo pelo seu ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}

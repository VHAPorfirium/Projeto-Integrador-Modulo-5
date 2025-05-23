package br.com.projetoIntegrador.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.projetoIntegrador.dto.TokenRequest;
import br.com.projetoIntegrador.model.DeviceToken;
import br.com.projetoIntegrador.service.TokenService;
import jakarta.validation.Valid;

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
    @GetMapping("/pacientes/{pacienteId}")
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

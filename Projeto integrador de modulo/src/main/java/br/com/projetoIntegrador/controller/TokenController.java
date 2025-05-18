package br.com.projetoIntegrador.controller;

import br.com.projetoIntegrador.dto.TokenRequest;
import br.com.projetoIntegrador.model.DeviceToken;
import br.com.projetoIntegrador.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    private final TokenService service;

    public TokenController(TokenService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DeviceToken> create(@RequestBody @Valid TokenRequest req) {
        return ResponseEntity.ok(service.save(req));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<DeviceToken>> listByUser(@PathVariable String userId) {
        return ResponseEntity.ok(service.findByUser(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

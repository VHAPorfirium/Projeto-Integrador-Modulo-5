package br.com.projetoIntegrador.controller;

import br.com.projetoIntegrador.dto.FollowUpDto;
import br.com.projetoIntegrador.service.FollowUpService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Essa classe tem a funcionalidade de expor endpoints para CRUD de follow-ups de pacientes.
@RestController
@RequestMapping("/retornos")
public class FollowUpController {

    private final FollowUpService service;

    public FollowUpController(FollowUpService service) {
        this.service = service;
    }

    // Retorna todos os follow-ups cadastrados.
    @GetMapping
    public ResponseEntity<List<FollowUpDto>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Retorna os follow-ups de um paciente específico.
    @GetMapping("/pacientes/{pacienteId}")
    public ResponseEntity<List<FollowUpDto>> listByPatient(
            @PathVariable Long pacienteId) {
        return ResponseEntity.ok(service.findByPaciente(pacienteId));
    }

    // Cria um novo follow-up a partir dos dados enviados.
    @PostMapping
    public ResponseEntity<FollowUpDto> create(
            @RequestBody @Valid FollowUpDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    // Atualiza um follow-up existente pelo ID.
    @PutMapping("/{id}")
    public ResponseEntity<FollowUpDto> update(
            @PathVariable Long id,
            @RequestBody @Valid FollowUpDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // Remove um follow-up pelo seu ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}

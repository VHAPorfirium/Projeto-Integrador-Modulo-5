package br.com.projetoIntegrador.controller;

import br.com.projetoIntegrador.dto.PacienteDto;
import br.com.projetoIntegrador.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Essa classe tem a funcionalidade de expor endpoints para CRUD de pacientes.
@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    // Cria um novo paciente com os dados fornecidos.
    @PostMapping
    public ResponseEntity<PacienteDto> create(
            @RequestBody @Valid PacienteDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    // Retorna a lista de todos os pacientes cadastrados.
    @GetMapping
    public ResponseEntity<List<PacienteDto>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Retorna os dados de um paciente pelo seu ID.
    @GetMapping("/{id}")
    public ResponseEntity<PacienteDto> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Atualiza os dados de um paciente existente pelo ID.
    @PutMapping("/{id}")
    public ResponseEntity<PacienteDto> update(
            @PathVariable Long id,
            @RequestBody @Valid PacienteDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // Remove um paciente pelo seu ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}

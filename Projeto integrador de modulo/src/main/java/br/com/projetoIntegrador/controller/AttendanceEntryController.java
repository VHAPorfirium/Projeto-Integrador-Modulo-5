package br.com.projetoIntegrador.controller;

import br.com.projetoIntegrador.dto.AttendanceEntryDto;
import br.com.projetoIntegrador.service.AttendanceEntryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Essa classe tem a funcionalidade de expor endpoints para CRUD de lançamentos de atendimento.
@RestController
@RequestMapping("/entradasAtendimento")
public class AttendanceEntryController {

    private final AttendanceEntryService service;

    public AttendanceEntryController(AttendanceEntryService service) {
        this.service = service;
    }

    // Retorna a lista de todos os lançamentos de atendimento.
    @GetMapping
    public ResponseEntity<List<AttendanceEntryDto>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Retorna um lançamento de atendimento pelo seu ID.
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceEntryDto> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Cria um novo lançamento de atendimento a partir dos dados recebidos.
    @PostMapping
    public ResponseEntity<AttendanceEntryDto> create(
            @RequestBody @Valid AttendanceEntryDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    // Atualiza um lançamento de atendimento existente pelo ID.
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceEntryDto> update(
            @PathVariable Long id,
            @RequestBody @Valid AttendanceEntryDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // Remove um lançamento de atendimento pelo seu ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}

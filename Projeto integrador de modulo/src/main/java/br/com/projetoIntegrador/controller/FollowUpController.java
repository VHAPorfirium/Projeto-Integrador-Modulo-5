package br.com.projetoIntegrador.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.projetoIntegrador.dto.FollowUpDto;
import br.com.projetoIntegrador.service.FollowUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// Essa classe tem a funcionalidade de expor endpoints para CRUD de follow-ups de pacientes.
@RestController
@RequestMapping("/retornos")
@Tag(name = "Follow-ups de Pacientes", description = "Endpoints para gerenciar os follow-ups (retornos) dos pacientes.")
public class FollowUpController {

    private final FollowUpService service;

    public FollowUpController(FollowUpService service) {
        this.service = service;
    }

    // Retorna todos os follow-ups cadastrados.
    @Operation(summary = "Lista todos os follow-ups",
               description = "Retorna uma lista de todos os follow-ups cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de follow-ups retornada com sucesso",
                         content = @Content(mediaType = "application/json",
                                 array = @ArraySchema(schema = @Schema(implementation = FollowUpDto.class))))
    })
    @GetMapping
    public ResponseEntity<List<FollowUpDto>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Retorna os follow-ups de um paciente específico.
    @Operation(summary = "Lista os follow-ups de um paciente específico",
               description = "Retorna uma lista de follow-ups associados a um ID de paciente fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de follow-ups do paciente retornada com sucesso",
                         content = @Content(mediaType = "application/json",
                                 array = @ArraySchema(schema = @Schema(implementation = FollowUpDto.class)))),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado ou sem follow-ups")
    })
    @GetMapping("/pacientes/{pacienteId}")
    public ResponseEntity<List<FollowUpDto>> listByPatient(
            @Parameter(description = "ID do paciente para buscar os follow-ups", required = true)
            @PathVariable Long pacienteId) {
        return ResponseEntity.ok(service.findByPaciente(pacienteId));
    }

    // Cria um novo follow-up a partir dos dados enviados.
    @Operation(summary = "Cria um novo follow-up",
               description = "Registra um novo follow-up para um paciente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Follow-up criado com sucesso", 
                         content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = FollowUpDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<FollowUpDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do follow-up a ser criado", required = true,
                    content = @Content(schema = @Schema(implementation = FollowUpDto.class)))
            @RequestBody @Valid FollowUpDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    // Atualiza um follow-up existente pelo ID.
    @Operation(summary = "Atualiza um follow-up existente",
               description = "Modifica os dados de um follow-up já cadastrado, com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Follow-up atualizado com sucesso",
                         content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = FollowUpDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Follow-up não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FollowUpDto> update(
            @Parameter(description = "ID do follow-up a ser atualizado", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados para o follow-up", required = true,
                    content = @Content(schema = @Schema(implementation = FollowUpDto.class)))
            @RequestBody @Valid FollowUpDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // Remove um follow-up pelo seu ID.
    @Operation(summary = "Remove um follow-up pelo ID",
               description = "Exclui um follow-up do sistema com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Follow-up removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Follow-up não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do follow-up a ser removido", required = true)
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
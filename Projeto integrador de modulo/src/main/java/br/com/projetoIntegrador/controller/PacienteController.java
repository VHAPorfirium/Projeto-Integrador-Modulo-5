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

import br.com.projetoIntegrador.dto.PacienteDto;
import br.com.projetoIntegrador.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// Essa classe tem a funcionalidade de expor endpoints para CRUD de pacientes.
@RestController
@RequestMapping("/pacientes")
@Tag(name = "Pacientes", description = "Endpoints para gerenciar os dados dos pacientes.")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    // Cria um novo paciente com os dados fornecidos.
    @Operation(summary = "Cria um novo paciente",
               description = "Registra um novo paciente no sistema com base nos dados fornecidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente criado com sucesso",
                         content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = PacienteDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<PacienteDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do paciente a ser criado", required = true,
                    content = @Content(schema = @Schema(implementation = PacienteDto.class)))
            @RequestBody @Valid PacienteDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    // Retorna la lista de todos os pacientes cadastrados.
    @Operation(summary = "Lista todos os pacientes",
               description = "Retorna uma lista completa de todos os pacientes cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pacientes retornada com sucesso",
                         content = @Content(mediaType = "application/json",
                                 array = @ArraySchema(schema = @Schema(implementation = PacienteDto.class))))
    })
    @GetMapping
    public ResponseEntity<List<PacienteDto>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Retorna os dados de um paciente pelo seu ID.
    @Operation(summary = "Busca um paciente pelo ID",
               description = "Retorna os dados de um paciente específico com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente encontrado",
                         content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = PacienteDto.class))),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PacienteDto> getById(
            @Parameter(description = "ID do paciente a ser buscado", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Atualiza os dados de um paciente existente pelo ID.
    @Operation(summary = "Atualiza os dados de um paciente existente",
               description = "Modifica os dados de um paciente já cadastrado, com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente atualizado com sucesso",
                         content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = PacienteDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PacienteDto> update(
            @Parameter(description = "ID do paciente a ser atualizado", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados para o paciente", required = true,
                    content = @Content(schema = @Schema(implementation = PacienteDto.class)))
            @RequestBody @Valid PacienteDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // Remove um paciente pelo seu ID.
    @Operation(summary = "Remove um paciente pelo ID",
               description = "Exclui um paciente do sistema com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Paciente removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do paciente a ser removido", required = true)
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
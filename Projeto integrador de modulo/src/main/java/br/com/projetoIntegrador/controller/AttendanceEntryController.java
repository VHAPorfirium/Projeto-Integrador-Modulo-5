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

import br.com.projetoIntegrador.dto.AttendanceEntryDto;
import br.com.projetoIntegrador.service.AttendanceEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// Essa classe tem a funcionalidade de expor endpoints para CRUD de lançamentos de atendimento.
@RestController
@RequestMapping("/entradasAtendimento")
@Tag(name = "Lançamentos de Atendimento", description = "Endpoints para gerenciar os lançamentos de atendimento.")
public class AttendanceEntryController {

    private final AttendanceEntryService service;

    public AttendanceEntryController(AttendanceEntryService service) {
        this.service = service;
    }

    // Retorna a lista de todos os lançamentos de atendimento.
    @Operation(summary = "Lista todos os lançamentos de atendimento",
               description = "Retorna uma lista completa de todos os lançamentos de atendimento cadastrados no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de lançamentos retornada com sucesso",
                         content = @Content(mediaType = "application/json",
                                 array = @ArraySchema(schema = @Schema(implementation = AttendanceEntryDto.class))))
    })
    @GetMapping
    public ResponseEntity<List<AttendanceEntryDto>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Retorna um lançamento de atendimento pelo seu ID.
    @Operation(summary = "Busca um lançamento de atendimento pelo ID",
               description = "Retorna um lançamento de atendimento específico com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lançamento de atendimento encontrado",
                         content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = AttendanceEntryDto.class))),
            @ApiResponse(responseCode = "404", description = "Lançamento de atendimento não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceEntryDto> getById(
            @Parameter(description = "ID do lançamento de atendimento a ser buscado", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Cria um novo lançamento de atendimento a partir dos dados recebidos.
    @Operation(summary = "Cria um novo lançamento de atendimento",
               description = "Registra um novo lançamento de atendimento no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lançamento de atendimento criado com sucesso", 
                         content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = AttendanceEntryDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<AttendanceEntryDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do lançamento de atendimento a ser criado", required = true,
                    content = @Content(schema = @Schema(implementation = AttendanceEntryDto.class)))
            @RequestBody @Valid AttendanceEntryDto dto) {
        return ResponseEntity.ok(service.create(dto)); 
    }

    // Atualiza um lançamento de atendimento existente pelo ID.
    @Operation(summary = "Atualiza um lançamento de atendimento existente",
               description = "Modifica os dados de um lançamento de atendimento já cadastrado, com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lançamento de atendimento atualizado com sucesso",
                         content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = AttendanceEntryDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Lançamento de atendimento não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceEntryDto> update(
            @Parameter(description = "ID do lançamento de atendimento a ser atualizado", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados para o lançamento de atendimento", required = true,
                    content = @Content(schema = @Schema(implementation = AttendanceEntryDto.class)))
            @RequestBody @Valid AttendanceEntryDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // Remove um lançamento de atendimento pelo seu ID.
    @Operation(summary = "Remove um lançamento de atendimento pelo ID",
               description = "Exclui um lançamento de atendimento do sistema com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lançamento de atendimento removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Lançamento de atendimento não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do lançamento de atendimento a ser removido", required = true)
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
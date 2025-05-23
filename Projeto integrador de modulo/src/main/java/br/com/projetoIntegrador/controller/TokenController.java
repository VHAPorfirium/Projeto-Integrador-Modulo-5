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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// Essa classe tem a funcionalidade de expor endpoints para gerenciamento de tokens de dispositivo.
@RestController
@RequestMapping("/tokens")
@Tag(name = "Tokens de Dispositivo", description = "Endpoints para gerenciar os tokens de dispositivo para notificações.")
public class TokenController {

    private final TokenService service;

    public TokenController(TokenService service) {
        this.service = service;
    }

    // Registra um novo token de dispositivo para notificações.
    @Operation(summary = "Registra um novo token de dispositivo",
               description = "Salva um novo token de dispositivo associado a um paciente para permitir o envio de notificações push.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token de dispositivo registrado com sucesso",
                         content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = DeviceToken.class))), 
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<DeviceToken> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados da requisição do token (token e ID do paciente)", required = true,
                    content = @Content(schema = @Schema(implementation = TokenRequest.class)))
            @RequestBody @Valid TokenRequest req) {
        return ResponseEntity.ok(service.save(req));
    }

    // Retorna todos os tokens de dispositivo associados a um paciente.
    @Operation(summary = "Lista tokens de dispositivo por paciente",
               description = "Retorna uma lista de todos os tokens de dispositivo registrados para um paciente específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tokens retornada com sucesso",
                         content = @Content(mediaType = "application/json",
                                 array = @ArraySchema(schema = @Schema(implementation = DeviceToken.class)))),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado ou sem tokens registrados")
    })
    @GetMapping("/pacientes/{pacienteId}")
    public ResponseEntity<List<DeviceToken>> listByPatient(
            @Parameter(description = "ID do paciente para buscar os tokens", required = true)
            @PathVariable Long pacienteId) {
        return ResponseEntity.ok(service.findByPaciente(pacienteId));
    }

    // Remove um token de dispositivo pelo seu ID.
    @Operation(summary = "Remove um token de dispositivo pelo seu ID",
               description = "Exclui um token de dispositivo específico do sistema, geralmente para desabilitar notificações para aquele dispositivo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Token de dispositivo removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Token de dispositivo não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do token de dispositivo a ser removido", required = true)
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
package br.com.projetoIntegrador.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.projetoIntegrador.dto.NotificationRequest;
import br.com.projetoIntegrador.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// Essa classe tem a funcionalidade de expor endpoint para envio de notificações via Firebase.
@RestController
@RequestMapping("/notificacoes")
@Tag(name = "Notificações", description = "Endpoint para envio de notificações via Firebase.")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // Recebe a requisição de notificação, dispara o envio e retorna a quantidade
    // enviada.
    @Operation(summary = "Envia notificações", description = "Recebe uma requisição de notificação, processa o envio através do Firebase e retorna a contagem de notificações enviadas com sucesso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificações enviadas com sucesso", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Notificações enviadas: 5"))),
            @ApiResponse(responseCode = "400", description = "Requisição de notificação inválida"),
            @ApiResponse(responseCode = "500", description = "Erro ao enviar notificações")
    })
    @PostMapping
    public ResponseEntity<String> send(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados da requisição de notificação", required = true, content = @Content(schema = @Schema(implementation = NotificationRequest.class))) @RequestBody @Valid NotificationRequest req)
            throws Exception {
        System.out.println("Backend recebeu Notificacao: PacienteId=" + req.getPacienteId());
        System.out.println("Backend recebeu Notificacao: Title=" + req.getTitle());
        System.out.println("Backend recebeu Notificacao: Body=" + req.getBody());
        System.out.println("Backend recebeu Notificacao: Data=" + req.getData());
        int count = service.send(req);
        return ResponseEntity.ok("Notificações enviadas com sucesso!");
    }
}
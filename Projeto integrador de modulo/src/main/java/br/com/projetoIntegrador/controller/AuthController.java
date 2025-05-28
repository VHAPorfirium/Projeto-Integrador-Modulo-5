package br.com.projetoIntegrador.controller;

import br.com.projetoIntegrador.dto.LoginRequestDto;
import br.com.projetoIntegrador.dto.LoginResponseDto;
import br.com.projetoIntegrador.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") 
@Tag(name = "Autenticação Simplificada", description = "Endpoints para login de pacientes e funcionários.")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login de Paciente",
               description = "Autentica um paciente utilizando CPF e senha.")
    @ApiResponse(responseCode = "200", description = "Resultado da tentativa de login",
                 content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = LoginResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @PostMapping("/login/paciente")
    public ResponseEntity<LoginResponseDto> loginPaciente(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = authService.loginPaciente(loginRequestDto);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response); 
        }
    }

    @Operation(summary = "Login de Funcionário",
               description = "Autentica um funcionário utilizando matrícula e senha.")
    @ApiResponse(responseCode = "200", description = "Resultado da tentativa de login",
                 content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = LoginResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @PostMapping("/login/funcionario")
    public ResponseEntity<LoginResponseDto> loginFuncionario(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = authService.loginFuncionario(loginRequestDto);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response); // Ou ResponseEntity.ok(response)
        }
    }
}

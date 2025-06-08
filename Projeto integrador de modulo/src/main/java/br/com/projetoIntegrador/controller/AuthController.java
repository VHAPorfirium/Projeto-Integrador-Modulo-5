package br.com.projetoIntegrador.controller;

import br.com.projetoIntegrador.dto.LoginRequestDto;
import br.com.projetoIntegrador.dto.LoginResponseDto;
import br.com.projetoIntegrador.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login/funcionario")
    public ResponseEntity<LoginResponseDto> loginFuncionario(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = authService.loginFuncionario(loginRequestDto);

        // ===== A MUDANÇA ESTÁ AQUI =====
        // Agora, nós sempre retornamos um status 200 (OK).
        // O aplicativo Android vai ler o campo "success" dentro do JSON
        // para saber se o login deu certo ou não.
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/paciente")
    public ResponseEntity<LoginResponseDto> loginPaciente(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = authService.loginPaciente(loginRequestDto);

        // Aplicando a mesma lógica para o paciente para manter a consistência.
        return ResponseEntity.ok(response);
    }
}

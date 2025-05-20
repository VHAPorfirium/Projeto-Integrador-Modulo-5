package br.com.projetoIntegrador.dto;

import jakarta.validation.constraints.NotNull;

// Essa classe tem a funcionalidade de representar a requisição de registro de token de dispositivo de um paciente.
public class TokenRequest {

    @NotNull
    private Long pacienteId;

    @NotNull
    private String token;

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

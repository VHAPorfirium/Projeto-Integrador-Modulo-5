package br.com.projetoIntegrador.dto;

import jakarta.validation.constraints.NotBlank;

// Usando uma classe para ter getters e setters que seu código já usa
public class LoginRequestDto {

    @NotBlank(message = "O identificador (CPF/Matrícula) é obrigatório.")
    private String identifier;

    // A senha não é mais obrigatória para o funcionário, mas é para o paciente.
    // Deixamos o campo existir, mas não validamos aqui para ser flexível.
    private String password;

    // Getters e Setters
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
package br.com.projetoIntegrador.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDto {
    @NotBlank(message = "O identificador (CPF/Matrícula) não pode estar em branco.")
    private String identifier;

    @NotBlank(message = "A senha não pode estar em branco.")
    private String password;

    // Construtor padrão
    public LoginRequestDto() {
    }

    // Construtor com todos os campos
    public LoginRequestDto(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

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
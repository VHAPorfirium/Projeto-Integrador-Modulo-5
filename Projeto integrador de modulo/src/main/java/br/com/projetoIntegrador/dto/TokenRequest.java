package br.com.projetoIntegrador.dto;

import jakarta.validation.constraints.NotBlank;

public class TokenRequest {
    @NotBlank
    private String userId;
    @NotBlank
    private String token;

    // getters e setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}

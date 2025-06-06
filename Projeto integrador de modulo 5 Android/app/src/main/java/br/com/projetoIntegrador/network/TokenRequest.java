package br.com.projetoIntegrador.network;

import com.google.gson.annotations.SerializedName;

// Corresponde a br.com.projetoIntegrador.dto.TokenRequest na API
public class TokenRequest {

    @SerializedName("pacienteId") // No backend é pacienteId, mas o frontend.pdf cita pacienteld para o DTO Android em TokenRequest.java (Corrigido)
    private Long pacienteId; // API espera Long

    @SerializedName("token")
    private String token;

    public TokenRequest(Long pacienteId, String token) {
        this.pacienteId = pacienteId;
        this.token = token;
    }

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
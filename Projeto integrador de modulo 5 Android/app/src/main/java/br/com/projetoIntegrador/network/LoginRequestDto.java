package br.com.projetoIntegrador.network;

import com.google.gson.annotations.SerializedName;

// Corresponde a br.com.projetoIntegrador.dto.LoginRequestDto na API
public class LoginRequestDto {

    @SerializedName("identifier")
    private String identifier; // CPF para paciente, Matrícula para funcionário

    @SerializedName("password")
    private String password;

    public LoginRequestDto(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

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
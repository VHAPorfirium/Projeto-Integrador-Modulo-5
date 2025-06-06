package br.com.projetoIntegrador.network;

import com.google.gson.annotations.SerializedName;

// Corresponde a br.com.projetoIntegrador.dto.LoginResponseDto na API
public class LoginResponseDto {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("userType")
    private String userType; // "paciente" ou "funcionario"

    @SerializedName("userId")
    private Long userId;

    @SerializedName("userName")
    private String userName;

    // Getters e Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
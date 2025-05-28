package br.com.projetoIntegrador.dto;

public class LoginResponseDto {
    private boolean success;
    private String message;
    private String userType;
    private Long userId;
    private String userName;

    // Construtor padrão
    public LoginResponseDto() {
    }

    // Construtor com todos os argumentos
    public LoginResponseDto(boolean success, String message, String userType, Long userId, String userName) {
        this.success = success;
        this.message = message;
        this.userType = userType;
        this.userId = userId;
        this.userName = userName;
    }

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
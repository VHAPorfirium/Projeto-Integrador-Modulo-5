package br.com.projetoIntegrador.network;

public class DeviceToken {
    private String userId;
    private String token;

    // O JSON vindo do back-end tem { id, userId, token, createdAt },
    // mas aqui só precisamos de userId e token para exibirmos ou reenviarmos.
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}

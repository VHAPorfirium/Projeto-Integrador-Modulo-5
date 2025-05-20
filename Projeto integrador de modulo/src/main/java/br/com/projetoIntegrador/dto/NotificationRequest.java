package br.com.projetoIntegrador.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

// Essa classe tem a funcionalidade de representar a requisição de notificação com destinatário, título, corpo e dados adicionais.
public class NotificationRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    private Map<String, String> data;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}

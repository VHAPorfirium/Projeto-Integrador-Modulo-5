package br.com.projetoIntegrador.dto;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;

// Essa classe tem a funcionalidade de representar a requisição de notificação com destinatário, título, corpo e dados adicionais.
public class NotificationRequest {

    @NotBlank
    private String pacienteId;

    private String title;

    private String body;

    private Map<String, String> data;

    public String getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(String pacienteId) {
        this.pacienteId = pacienteId;
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

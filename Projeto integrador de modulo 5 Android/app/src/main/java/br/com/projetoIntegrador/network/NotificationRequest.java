package br.com.projetoIntegrador.network;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

// Corresponde a br.com.projetolntegrador.dto.NotificationRequest na API
public class NotificationRequest {

    @SerializedName("pacienteId")
    private String pacienteId; // API usa String para pacienteId aqui

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String body;

    @SerializedName("data")
    private Map<String, String> data;

    public NotificationRequest(String pacienteId, String title, String body, Map<String, String> data) {
        this.pacienteId = pacienteId;
        this.title = title;
        this.body = body;
        this.data = data;
    }

    public NotificationRequest() {
    }

    // Getters e Setters
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
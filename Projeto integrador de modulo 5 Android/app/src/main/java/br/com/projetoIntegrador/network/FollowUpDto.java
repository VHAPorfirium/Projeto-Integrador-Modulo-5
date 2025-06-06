package br.com.projetoIntegrador.network;

import com.google.gson.annotations.SerializedName;

// Corresponde a br.com.projetoIntegrador.dto.FollowUpDto na API
// Anteriormente nomeado RetornoDto na estrutura Android
public class FollowUpDto {

    @SerializedName("id")
    private Long id;

    @SerializedName("pacienteId")
    private Long pacienteId;

    @SerializedName("scheduledTime")
    private String scheduledTime; // API usa Instant

    @SerializedName("status")
    private String status; // API usa FollowupStatus enum, mapear para String ou enum FollowUpStatus no Android

    @SerializedName("createdAt")
    private String createdAt; // API usa Instant

    @SerializedName("updatedAt")
    private String updatedAt; // API usa Instant

    @SerializedName("canceledAt")
    private String canceledAt; // API usa Instant

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; } // Ou usar o enum FollowUpStatus

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getCanceledAt() { return canceledAt; }
    public void setCanceledAt(String canceledAt) { this.canceledAt = canceledAt; }
}
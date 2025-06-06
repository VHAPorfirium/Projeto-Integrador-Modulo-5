package br.com.projetoIntegrador.network;

import com.google.gson.annotations.SerializedName;

// Corresponde a br.com.projetoIntegrador.dto.AttendanceEntryDto na API
// Anteriormente nomeado FilaDto na estrutura Android
public class AttendanceEntryDto {

    @SerializedName("id")
    private Long id;

    @SerializedName("pacienteId")
    private Long pacienteId;

    @SerializedName("specialtyId")
    private Long specialtyId;

    @SerializedName("checkInTime")
    private String checkInTime; // API usa Instant

    @SerializedName("status")
    private String status; // API usa AttendanceStatus enum, mapear para String ou enum AttendanceStatus no Android

    @SerializedName("callTime")
    private String callTime; // API usa Instant

    @SerializedName("confirmationDeadline")
    private String confirmationDeadline; // API usa Instant

    @SerializedName("confirmationTime")
    private String confirmationTime; // API usa Instant

    @SerializedName("attempts")
    private short attempts;

    @SerializedName("startServiceTime")
    private String startServiceTime; // API usa Instant

    @SerializedName("endServiceTime")
    private String endServiceTime; // API usa Instant

    @SerializedName("createdAt")
    private String createdAt; // API usa Instant

    @SerializedName("updatedAt")
    private String updatedAt; // API usa Instant

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public Long getSpecialtyId() { return specialtyId; }
    public void setSpecialtyId(Long specialtyId) { this.specialtyId = specialtyId; }

    public String getCheckInTime() { return checkInTime; }
    public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; } // Ou usar o enum AttendanceStatus

    public String getCallTime() { return callTime; }
    public void setCallTime(String callTime) { this.callTime = callTime; }

    public String getConfirmationDeadline() { return confirmationDeadline; }
    public void setConfirmationDeadline(String confirmationDeadline) { this.confirmationDeadline = confirmationDeadline; }

    public String getConfirmationTime() { return confirmationTime; }
    public void setConfirmationTime(String confirmationTime) { this.confirmationTime = confirmationTime; }

    public short getAttempts() { return attempts; }
    public void setAttempts(short attempts) { this.attempts = attempts; }

    public String getStartServiceTime() { return startServiceTime; }
    public void setStartServiceTime(String startServiceTime) { this.startServiceTime = startServiceTime; }

    public String getEndServiceTime() { return endServiceTime; }
    public void setEndServiceTime(String endServiceTime) { this.endServiceTime = endServiceTime; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
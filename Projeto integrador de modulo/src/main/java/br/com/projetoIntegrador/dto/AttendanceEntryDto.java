package br.com.projetoIntegrador.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

// Essa classe tem a funcionalidade de representar os dados de um registro de atendimento.
public class AttendanceEntryDto {

    private Long id;

    @NotNull
    private Long pacienteId;

    @NotNull
    private Long specialtyId;

    private Instant checkInTime;

    @NotNull
    private String status;

    private Instant callTime;
    private Instant confirmationDeadline;
    private Instant confirmationTime;
    private short attempts;
    private Instant startServiceTime;
    private Instant endServiceTime;
    private Instant createdAt;
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Long getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(Long specialtyId) {
        this.specialtyId = specialtyId;
    }

    public Instant getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Instant checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCallTime() {
        return callTime;
    }

    public void setCallTime(Instant callTime) {
        this.callTime = callTime;
    }

    public Instant getConfirmationDeadline() {
        return confirmationDeadline;
    }

    public void setConfirmationDeadline(Instant confirmationDeadline) {
        this.confirmationDeadline = confirmationDeadline;
    }

    public Instant getConfirmationTime() {
        return confirmationTime;
    }

    public void setConfirmationTime(Instant confirmationTime) {
        this.confirmationTime = confirmationTime;
    }

    public short getAttempts() {
        return attempts;
    }

    public void setAttempts(short attempts) {
        this.attempts = attempts;
    }

    public Instant getStartServiceTime() {
        return startServiceTime;
    }

    public void setStartServiceTime(Instant startServiceTime) {
        this.startServiceTime = startServiceTime;
    }

    public Instant getEndServiceTime() {
        return endServiceTime;
    }

    public void setEndServiceTime(Instant endServiceTime) {
        this.endServiceTime = endServiceTime;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

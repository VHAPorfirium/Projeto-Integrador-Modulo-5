package br.com.projetoIntegrador.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

// Essa classe tem a funcionalidade de representar a entidade de um registro de atendimento no banco de dados.
@Entity
@Table(name = "attendance_entries")
public class AttendanceEntry {

    // Identificador único do registro de atendimento.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Paciente associado ao atendimento.
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Paciente paciente;

    // Especialidade médica do atendimento.
    @ManyToOne
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    // Data e hora do check-in, gerada automaticamente no momento da inserção.
    @CreationTimestamp
    @Column(name = "check_in_time", updatable = false)
    private Instant checkInTime;

    // Status atual do atendimento
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status = AttendanceStatus.AGUARDANDO;

    // Data e hora em que o paciente foi chamado.
    @Column(name = "call_time")
    private Instant callTime;

    // Prazo máximo para confirmação após a chamada.
    @Column(name = "confirmation_deadline")
    private Instant confirmationDeadline;

    // Data e hora em que o paciente confirmou presença.
    @Column(name = "confirmation_time")
    private Instant confirmationTime;

    // Número de tentativas de chamada.
    @Column(nullable = false)
    private short attempts = 0;

    // Data e hora de início do atendimento.
    @Column(name = "start_service_time")
    private Instant startServiceTime;

    // Data e hora de término do atendimento.
    @Column(name = "end_service_time")
    private Instant endServiceTime;

    // Data e hora de criação do registro.
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    // Data e hora da última atualização do registro.
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public Instant getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Instant checkInTime) {
        this.checkInTime = checkInTime;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
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

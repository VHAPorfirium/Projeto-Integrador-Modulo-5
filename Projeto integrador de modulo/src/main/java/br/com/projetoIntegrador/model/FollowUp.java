package br.com.projetoIntegrador.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

// Essa classe tem a funcionalidade de representar agendamentos de follow-up de pacientes no banco de dados.
@Entity
@Table(name = "follow_ups")
public class FollowUp {

    // Identificador único do follow-up.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Paciente associado ao follow-up.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Paciente paciente;

    // Data e hora agendada para o follow-up.
    @Column(name = "scheduled_time", nullable = false)
    private Instant scheduledTime;

    // Status atual do follow-up
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FollowupStatus status = FollowupStatus.AGENDADO;

    // Timestamp de criação do registro.
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    // Timestamp de última atualização do registro.
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Data e hora de cancelamento do follow-up.
    @Column(name = "canceled_at")
    private Instant canceledAt;

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

    public Instant getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Instant scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public FollowupStatus getStatus() {
        return status;
    }

    public void setStatus(FollowupStatus status) {
        this.status = status;
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

    public Instant getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(Instant canceledAt) {
        this.canceledAt = canceledAt;
    }
}

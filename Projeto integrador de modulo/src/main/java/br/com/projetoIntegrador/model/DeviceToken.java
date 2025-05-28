package br.com.projetoIntegrador.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

// Essa classe tem a funcionalidade de representar tokens de dispositivo de pacientes.
@Entity
@Table(
        name = "device_tokens",
        uniqueConstraints = @UniqueConstraint(columnNames = {"patient_id", "token"})
)
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false, length = 512)
    private String token;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    // Construtor padrão.
    public DeviceToken() {}

    // Construtor que inicializa paciente e token.
    public DeviceToken(Paciente paciente, String token) {
        this.paciente = paciente;
        this.token = token;
    }

    // Retorna o ID do token.
    public Long getId() {
        return id;
    }

    // Define o ID do token.
    public void setId(Long id) {
        this.id = id;
    }

    // Retorna o paciente associado ao token.
    public Paciente getPaciente() {
        return paciente;
    }

    // Define o paciente associado ao token.
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    // Retorna o valor do token.
    public String getToken() {
        return token;
    }

    // Define o valor do token.
    public void setToken(String token) {
        this.token = token;
    }

    // Retorna a data de criação do registro.
    public Instant getCreatedAt() {
        return createdAt;
    }

    // Define a data de criação do registro.
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

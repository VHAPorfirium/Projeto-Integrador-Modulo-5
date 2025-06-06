package br.com.projetoIntegrador.network;

import com.google.gson.annotations.SerializedName;

// Representa br.com.projetolntegrador.model.DeviceToken da API
public class DeviceToken {

    @SerializedName("id")
    private Long id;

    // No backend, DeviceToken.java (modelo) tem um objeto Paciente.
    // DeviceToken.java (DTO implícito no TokenController) retorna o DeviceToken completo.
    // Para simplificar no Android, podemos esperar 'pacienteId' ou um objeto PacienteDto simplificado.
    // O frontend.pdf optou por uma referência aninhada.
    @SerializedName("paciente") // Isso corresponderia a um objeto Paciente no JSON da API.
    private PacienteReferencia paciente;

    @SerializedName("token")
    private String token;

    @SerializedName("createdAt")
    private String createdAt; // API usa Instant

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PacienteReferencia getPaciente() {
        return paciente;
    }

    public void setPaciente(PacienteReferencia paciente) {
        this.paciente = paciente;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Método auxiliar se você precisar apenas do pacienteId
    public Long getPacienteId() {
        return (paciente != null) ? paciente.getId() : null;
    }

    // Classe interna para representar a referência ao Paciente, se for um objeto com ID
    public static class PacienteReferencia {
        @SerializedName("id")
        private Long id;

        // Outros campos do Paciente que possam vir serializados dentro de DeviceToken, se houver.
        // Ex: @SerializedName("fullName") private String fullName;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
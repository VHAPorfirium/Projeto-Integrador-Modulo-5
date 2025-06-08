package br.com.projetoIntegrador.dto;

import br.com.projetoIntegrador.model.EmployeeRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Set;

/**
 * DTO para transferir dados de Funcionário entre cliente e servidor.
 */
public class FuncionarioDto {

    private Long id;

    @NotBlank(message = "O nome completo é obrigatório.")
    private String fullName;

    @NotBlank(message = "A matrícula é obrigatória.")
    private String matricula;

    // Na criação, a senha é obrigatória. Na atualização, pode ser opcional.
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String password;

    @NotNull(message = "O cargo (role) é obrigatório.")
    private EmployeeRole role;

    private Boolean isActive;

    // IDs das especialidades (caso exista essa relação)
    private Set<Long> specialtyIds;

    private Instant createdAt;
    private Instant updatedAt;

    // --- Getters e Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EmployeeRole getRole() {
        return role;
    }

    public void setRole(EmployeeRole role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Set<Long> getSpecialtyIds() {
        return specialtyIds;
    }

    public void setSpecialtyIds(Set<Long> specialtyIds) {
        this.specialtyIds = specialtyIds;
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

package br.com.projetoIntegrador.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

// Essa classe tem a funcionalidade de representar a entidade de um paciente no banco de dados.
@Entity
@Table(name = "patients")
public class Paciente {

    // Identificador único do paciente.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome completo do paciente.
    @Column(name = "full_name", nullable = false)
    private String fullName;

    // Data de nascimento do paciente.
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    // CPF do paciente, deve ser único.
    @Column(nullable = false, unique = true)
    private String cpf;

    // Senha
    @Column(name = "password_hash", nullable = false)
    private String passwordHash; // Campo para a senha

    // RG do paciente.
    private String rg;

    // E-mail de contato do paciente.
    private String email;

    // Telefone de contato do paciente.
    private String phone;

    // Logradouro do endereço do paciente.
    @Column(name = "address_street")
    private String addressStreet;

    // Cidade do endereço do paciente.
    @Column(name = "address_city")
    private String addressCity;

    // Estado do endereço do paciente.
    @Column(name = "address_state")
    private String addressState;

    // CEP do endereço do paciente.
    @Column(name = "address_zip")
    private String addressZip;

    // Lista de alergias do paciente.
    @Column(columnDefinition = "TEXT[]")
    private String[] allergies;

    // Lista de medicamentos em uso pelo paciente.
    @Column(columnDefinition = "TEXT[]")
    private String[] medications;

    // Foto de perfil do paciente armazenada como array de bytes.
    @Column(name = "profile_photo")
    private byte[] profilePhoto;

    // Indicador se o paciente está ativo no sistema.
    @Column(name = "is_active")
    private Boolean isActive = true;

    // Timestamp de criação do registro.
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    // Timestamp da última atualização do registro.
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public String getAddressZip() {
        return addressZip;
    }

    public void setAddressZip(String addressZip) {
        this.addressZip = addressZip;
    }

    public String[] getAllergies() {
        return allergies;
    }

    public void setAllergies(String[] allergies) {
        this.allergies = allergies;
    }

    public String[] getMedications() {
        return medications;
    }

    public void setMedications(String[] medications) {
        this.medications = medications;
    }

    public byte[] getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(byte[] profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // equals, hashCode, toString 
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paciente paciente = (Paciente) o;
        return Objects.equals(id, paciente.id) && Objects.equals(cpf, paciente.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cpf);
    }

    @Override
    public String toString() {
        return "Paciente{" +
               "id=" + id +
               ", fullName='" + fullName + '\'' +
               ", cpf='" + cpf + '\'' +
               '}';
    }
}

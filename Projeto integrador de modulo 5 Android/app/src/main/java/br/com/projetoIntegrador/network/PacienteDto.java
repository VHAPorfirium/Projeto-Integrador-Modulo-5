package br.com.projetoIntegrador.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// Corresponde a br.com.projetoIntegrador.dto.PacienteDto na API
public class PacienteDto {

    @SerializedName("id")
    private Long id;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("birthDate")
    private List<Integer> birthDate; // API usa LocalDate, mas o JSON mostra um array [ano, mes, dia]

    @SerializedName("cpf")
    private String cpf;

    @SerializedName("rg")
    private String rg;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("addressStreet")
    private String addressStreet;

    @SerializedName("addressCity")
    private String addressCity;

    @SerializedName("addressState")
    private String addressState;

    @SerializedName("addressZip")
    private String addressZip;

    @SerializedName("allergies")
    private List<String> allergies; // API usa String[]

    @SerializedName("medications")
    private List<String> medications; // API usa String[]

    @SerializedName("isActive")
    private Boolean isActive;

    @SerializedName("createdAt")
    private String createdAt; // API usa Instant

    @SerializedName("updatedAt")
    private String updatedAt; // API usa Instant

    // Campo de senha para envio durante o cadastro ou atualização.
    // NÃO deve ser usado para armazenar ou exibir senhas/hashes recebidos.
    // A API backend (dto.PacienteDto) inclui este campo.
    @SerializedName("password")
    private String password;


    // Construtor Vazio (necessário para algumas bibliotecas de serialização/deserialização)
    public PacienteDto() {
    }

    // Construtor para criação/atualização (sem ID, createdAt, updatedAt e profilePhoto)
    public PacienteDto(String fullName, List<Integer> birthDate, String cpf, String password,
                       String rg, String email, String phone, String addressStreet,
                       String addressCity, String addressState, String addressZip,
                       List<String> allergies, List<String> medications, Boolean isActive) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.password = password;
        this.rg = rg;
        this.email = email;
        this.phone = phone;
        this.addressStreet = addressStreet;
        this.addressCity = addressCity;
        this.addressState = addressState;
        this.addressZip = addressZip;
        this.allergies = allergies;
        this.medications = medications;
        this.isActive = isActive;
    }


    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    // CORREÇÃO AQUI: Retorno e parâmetro do setter para List<Integer>
    public List<Integer> getBirthDate() { return birthDate; }
    public void setBirthDate(List<Integer> birthDate) { this.birthDate = birthDate; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddressStreet() { return addressStreet; }
    public void setAddressStreet(String addressStreet) { this.addressStreet = addressStreet; }

    public String getAddressCity() { return addressCity; }
    public void setAddressCity(String addressCity) { this.addressCity = addressCity; }

    public String getAddressState() { return addressState; }
    public void setAddressState(String addressState) { this.addressState = addressState; }

    public String getAddressZip() { return addressZip; }
    public void setAddressZip(String addressZip) { this.addressZip = addressZip; }

    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }

    public List<String> getMedications() { return medications; }
    public void setMedications(List<String> medications) { this.medications = medications; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
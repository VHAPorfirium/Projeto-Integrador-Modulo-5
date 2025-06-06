package br.com.projetoIntegrador.network;

import com.google.gson.annotations.SerializedName;

// Corresponde a br.com.projetoIntegrador.dto.SpecialtyDto na API
public class SpecialtyDto {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
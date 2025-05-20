package br.com.projetoIntegrador.model;

import jakarta.persistence.*;

// Essa classe tem a funcionalidade de representar uma especialidade médica no banco de dados.
@Entity
@Table(name = "specialties")
public class Specialty {

    // Identificador único da especialidade.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome da especialidade, deve ser único e não nulo.
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // Descrição detalhada da especialidade.
    @Column(columnDefinition = "TEXT")
    private String description;

    // Construtor padrão.
    public Specialty() {}

    // Construtor que inicializa nome e descrição da especialidade.
    public Specialty(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

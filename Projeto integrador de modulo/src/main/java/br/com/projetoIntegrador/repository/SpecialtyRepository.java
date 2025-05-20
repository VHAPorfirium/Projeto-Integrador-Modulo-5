package br.com.projetoIntegrador.repository;

import br.com.projetoIntegrador.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

// Essa interface tem a funcionalidade de fornecer operações de CRUD para especialidades médicas.
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
}

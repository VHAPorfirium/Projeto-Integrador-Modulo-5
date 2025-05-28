package br.com.projetoIntegrador.repository;

import br.com.projetoIntegrador.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Essa interface tem a funcionalidade de fornecer operações de CRUD para pacientes.
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByCpf(String cpf);
}

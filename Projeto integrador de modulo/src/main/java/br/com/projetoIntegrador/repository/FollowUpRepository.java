package br.com.projetoIntegrador.repository;

import br.com.projetoIntegrador.model.FollowUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Essa interface tem a funcionalidade de fornecer operações de CRUD para agendamentos de follow-up.
public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {

    // Retorna todos os follow-ups associados a um paciente pelo seu ID.
    List<FollowUp> findAllByPacienteId(Long pacienteId);

}

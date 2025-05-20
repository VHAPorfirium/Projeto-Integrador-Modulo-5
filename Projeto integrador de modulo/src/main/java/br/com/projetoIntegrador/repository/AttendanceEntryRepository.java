package br.com.projetoIntegrador.repository;

import br.com.projetoIntegrador.model.AttendanceEntry;
import org.springframework.data.jpa.repository.JpaRepository;

// Essa interface tem a funcionalidade de fornecer operações de CRUD para registros de atendimento.
public interface AttendanceEntryRepository extends JpaRepository<AttendanceEntry, Long> {
}

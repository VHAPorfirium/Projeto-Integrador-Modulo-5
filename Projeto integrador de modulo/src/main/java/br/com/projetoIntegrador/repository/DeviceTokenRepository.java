package br.com.projetoIntegrador.repository;

import br.com.projetoIntegrador.model.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Essa interface tem a funcionalidade de fornecer operações de CRUD para tokens de dispositivo.
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    // Retorna todos os tokens de dispositivo associados a um paciente pelo seu ID.
    List<DeviceToken> findAllByPacienteId(Long pacienteId);

}

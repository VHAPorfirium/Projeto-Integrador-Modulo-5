package br.com.projetoIntegrador.repository;

import br.com.projetoIntegrador.model.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    List<DeviceToken> findAllByUserId(String userId);
}

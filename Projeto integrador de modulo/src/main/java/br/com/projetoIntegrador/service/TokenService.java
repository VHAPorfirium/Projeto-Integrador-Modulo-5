package br.com.projetoIntegrador.service;

import br.com.projetoIntegrador.dto.TokenRequest;
import br.com.projetoIntegrador.model.DeviceToken;
import br.com.projetoIntegrador.repository.DeviceTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenService {

    private final DeviceTokenRepository repo;

    public TokenService(DeviceTokenRepository repo) {
        this.repo = repo;
    }

    public DeviceToken save(TokenRequest req) {
        DeviceToken entity = new DeviceToken(req.getUserId(), req.getToken());
        return repo.save(entity);
    }

    public List<DeviceToken> findByUser(String userId) {
        return repo.findAllByUserId(userId);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}

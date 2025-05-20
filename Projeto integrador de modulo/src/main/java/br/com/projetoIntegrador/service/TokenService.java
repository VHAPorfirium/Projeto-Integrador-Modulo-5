package br.com.projetoIntegrador.service;

import br.com.projetoIntegrador.dto.TokenRequest;
import br.com.projetoIntegrador.model.DeviceToken;
import br.com.projetoIntegrador.model.Paciente;
import br.com.projetoIntegrador.repository.DeviceTokenRepository;
import br.com.projetoIntegrador.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Essa classe tem a funcionalidade de gerenciar tokens de dispositivo de pacientes.
@Service
public class TokenService {

    private final DeviceTokenRepository tokenRepo;
    private final PacienteRepository pacienteRepo;

    public TokenService(
            DeviceTokenRepository tokenRepo,
            PacienteRepository pacienteRepo) {
        this.tokenRepo = tokenRepo;
        this.pacienteRepo = pacienteRepo;
    }

    // Registra um novo token de dispositivo para um paciente.
    public DeviceToken save(TokenRequest req) {
        Paciente p = pacienteRepo.findById(req.getPacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
        DeviceToken entity = new DeviceToken(p, req.getToken());
        return tokenRepo.save(entity);
    }

    // Retorna todos os tokens de dispositivo associados a um paciente pelo seu ID.
    public List<DeviceToken> findByPaciente(Long pacienteId) {
        return tokenRepo.findAllByPacienteId(pacienteId);
    }

    // Remove um token de dispositivo pelo seu ID.
    public void delete(Long id) {
        tokenRepo.deleteById(id);
    }

}

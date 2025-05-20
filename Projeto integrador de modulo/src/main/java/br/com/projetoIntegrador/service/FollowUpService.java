package br.com.projetoIntegrador.service;

import br.com.projetoIntegrador.dto.FollowUpDto;
import br.com.projetoIntegrador.model.FollowUp;
import br.com.projetoIntegrador.model.Paciente;
import br.com.projetoIntegrador.model.FollowupStatus;
import br.com.projetoIntegrador.repository.FollowUpRepository;
import br.com.projetoIntegrador.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// Essa classe tem a funcionalidade de gerenciar a lógica de negócio para agendamentos de follow-up.
@Service
public class FollowUpService {

    private final FollowUpRepository repo;
    private final PacienteRepository pacienteRepo;

    public FollowUpService(
            FollowUpRepository repo,
            PacienteRepository pacienteRepo) {
        this.repo = repo;
        this.pacienteRepo = pacienteRepo;
    }

    // Retorna todos os follow-ups como DTOs.
    public List<FollowUpDto> findAll() {
        return repo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Retorna todos os follow-ups de um paciente específico.
    public List<FollowUpDto> findByPaciente(Long pacienteId) {
        return repo.findAllByPacienteId(pacienteId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Cria um novo follow-up a partir dos dados do DTO.
    public FollowUpDto create(FollowUpDto dto) {
        FollowUp f = new FollowUp();
        Paciente p = pacienteRepo.findById(dto.getPacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
        f.setPaciente(p);
        f.setScheduledTime(dto.getScheduledTime());
        f.setStatus(FollowupStatus.valueOf(dto.getStatus()));
        f = repo.save(f);
        return toDto(f);
    }

    // Atualiza o status de um follow-up existente.
    public FollowUpDto update(Long id, FollowUpDto dto) {
        FollowUp f = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FollowUp não encontrado"));
        f.setStatus(FollowupStatus.valueOf(dto.getStatus()));
        repo.save(f);
        return toDto(f);
    }

    // Remove um follow-up pelo seu ID.
    public void delete(Long id) {
        repo.deleteById(id);
    }

    // Converte uma entidade FollowUp em FollowUpDto.
    private FollowUpDto toDto(FollowUp f) {
        FollowUpDto dto = new FollowUpDto();
        dto.setId(f.getId());
        dto.setPacienteId(f.getPaciente().getId());
        dto.setScheduledTime(f.getScheduledTime());
        dto.setStatus(f.getStatus().name());
        dto.setCreatedAt(f.getCreatedAt());
        dto.setUpdatedAt(f.getUpdatedAt());
        dto.setCanceledAt(f.getCanceledAt());
        return dto;
    }

}

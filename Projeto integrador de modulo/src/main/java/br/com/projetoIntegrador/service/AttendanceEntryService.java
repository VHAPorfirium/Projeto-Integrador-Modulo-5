package br.com.projetoIntegrador.service;

import br.com.projetoIntegrador.dto.AttendanceEntryDto;
import br.com.projetoIntegrador.model.AttendanceEntry;
import br.com.projetoIntegrador.model.Paciente;
import br.com.projetoIntegrador.model.Specialty;
import br.com.projetoIntegrador.model.AttendanceStatus;
import br.com.projetoIntegrador.repository.AttendanceEntryRepository;
import br.com.projetoIntegrador.repository.PacienteRepository;
import br.com.projetoIntegrador.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// Essa classe tem a funcionalidade de gerenciar a lógica de negócio para registros de atendimento.
@Service
public class AttendanceEntryService {

    private final AttendanceEntryRepository repo;
    private final PacienteRepository patRepo;
    private final SpecialtyRepository specRepo;

    public AttendanceEntryService(
            AttendanceEntryRepository repo,
            PacienteRepository patRepo,
            SpecialtyRepository specRepo) {
        this.repo = repo;
        this.patRepo = patRepo;
        this.specRepo = specRepo;
    }

    // Retorna todos os registros de atendimento como DTOs.
    public List<AttendanceEntryDto> findAll() {
        return repo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Retorna um registro de atendimento pelo ID, lançando exceção se não encontrado.
    public AttendanceEntryDto findById(Long id) {
        return repo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Entry não encontrado"));
    }

    // Cria um novo registro de atendimento a partir de um DTO.
    public AttendanceEntryDto create(AttendanceEntryDto dto) {
        AttendanceEntry entry = new AttendanceEntry();
        Paciente p = patRepo.findById(dto.getPacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
        Specialty s = specRepo.findById(dto.getSpecialtyId())
                .orElseThrow(() -> new IllegalArgumentException("Specialty não encontrado"));
        entry.setPaciente(p);
        entry.setSpecialty(s);
        entry.setStatus(AttendanceStatus.valueOf(dto.getStatus()));
        entry = repo.save(entry);
        return toDto(entry);
    }

    // Atualiza o status de um registro de atendimento existente pelo ID.
    public AttendanceEntryDto update(Long id, AttendanceEntryDto dto) {
        AttendanceEntry entry = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entry não encontrado"));
        entry.setStatus(AttendanceStatus.valueOf(dto.getStatus()));
        repo.save(entry);
        return toDto(entry);
    }

    // Remove um registro de atendimento pelo ID.
    public void delete(Long id) {
        repo.deleteById(id);
    }

    // Converte uma entidade AttendanceEntry em AttendanceEntryDto.
    private AttendanceEntryDto toDto(AttendanceEntry e) {
        AttendanceEntryDto dto = new AttendanceEntryDto();
        dto.setId(e.getId());
        dto.setPacienteId(e.getPaciente().getId());
        dto.setSpecialtyId(e.getSpecialty().getId());
        dto.setCheckInTime(e.getCheckInTime());
        dto.setStatus(e.getStatus().name());
        dto.setCallTime(e.getCallTime());
        dto.setConfirmationDeadline(e.getConfirmationDeadline());
        dto.setConfirmationTime(e.getConfirmationTime());
        dto.setAttempts(e.getAttempts());
        dto.setStartServiceTime(e.getStartServiceTime());
        dto.setEndServiceTime(e.getEndServiceTime());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

}

package br.com.projetoIntegrador.service;

import br.com.projetoIntegrador.dto.PacienteDto;
import br.com.projetoIntegrador.model.Paciente;
import br.com.projetoIntegrador.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// Essa classe tem a funcionalidade de gerenciar a lógica de negócio para pacientes.
@Service
public class PacienteService {

    private final PacienteRepository repo;

    public PacienteService(PacienteRepository repo) {
        this.repo = repo;
    }

    // Cria um novo paciente com base nos dados do DTO.
    public PacienteDto create(PacienteDto dto) {
        Paciente p = toEntity(dto);
        p = repo.save(p);
        return toDto(p);
    }

    // Retorna todos os pacientes como lista de DTOs.
    public List<PacienteDto> findAll() {
        return repo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Retorna um paciente pelo ID ou lança exceção se não encontrado.
    public PacienteDto findById(Long id) {
        return repo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
    }

    // Atualiza os dados de um paciente existente pelo ID.
    public PacienteDto update(Long id, PacienteDto dto) {
        Paciente p = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
        p.setFullName(dto.getFullName());
        p.setBirthDate(dto.getBirthDate());
        p.setCpf(dto.getCpf());
        p.setRg(dto.getRg());
        p.setEmail(dto.getEmail());
        p.setPhone(dto.getPhone());
        p.setAddressStreet(dto.getAddressStreet());
        p.setAddressCity(dto.getAddressCity());
        p.setAddressState(dto.getAddressState());
        p.setAddressZip(dto.getAddressZip());
        p.setAllergies(dto.getAllergies());
        p.setMedications(dto.getMedications());
        p.setIsActive(dto.getIsActive());
        p = repo.save(p);
        return toDto(p);
    }

    // Remove um paciente pelo seu ID.
    public void delete(Long id) {
        repo.deleteById(id);
    }

    // Converte um PacienteDto em entidade Paciente.
    private Paciente toEntity(PacienteDto dto) {
        Paciente p = new Paciente();
        p.setFullName(dto.getFullName());
        p.setBirthDate(dto.getBirthDate());
        p.setCpf(dto.getCpf());
        p.setRg(dto.getRg());
        p.setEmail(dto.getEmail());
        p.setPhone(dto.getPhone());
        p.setAddressStreet(dto.getAddressStreet());
        p.setAddressCity(dto.getAddressCity());
        p.setAddressState(dto.getAddressState());
        p.setAddressZip(dto.getAddressZip());
        p.setAllergies(dto.getAllergies());
        p.setMedications(dto.getMedications());
        p.setIsActive(dto.getIsActive());
        return p;
    }

    // Converte uma entidade Paciente em PacienteDto.
    private PacienteDto toDto(Paciente p) {
        PacienteDto dto = new PacienteDto();
        dto.setId(p.getId());
        dto.setFullName(p.getFullName());
        dto.setBirthDate(p.getBirthDate());
        dto.setCpf(p.getCpf());
        dto.setRg(p.getRg());
        dto.setEmail(p.getEmail());
        dto.setPhone(p.getPhone());
        dto.setAddressStreet(p.getAddressStreet());
        dto.setAddressCity(p.getAddressCity());
        dto.setAddressState(p.getAddressState());
        dto.setAddressZip(p.getAddressZip());
        dto.setAllergies(p.getAllergies());
        dto.setMedications(p.getMedications());
        dto.setIsActive(p.getIsActive());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    }

}

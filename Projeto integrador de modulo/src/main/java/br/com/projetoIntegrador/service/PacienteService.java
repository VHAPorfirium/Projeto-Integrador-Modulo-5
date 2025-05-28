package br.com.projetoIntegrador.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.projetoIntegrador.dto.PacienteDto;
import br.com.projetoIntegrador.model.Paciente;
import br.com.projetoIntegrador.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    private final PacienteRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Autowired 
    public PacienteService(PacienteRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    // Cria um novo paciente com base nos dados do DTO.
    public PacienteDto create(PacienteDto dto) {
        Paciente p = toEntity(dto); 
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            p.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        } else {
            throw new IllegalArgumentException("A senha não pode ser nula ou vazia no cadastro.");
        }
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
        
        // Atualizar os campos do paciente p com os dados de dto
        p.setFullName(dto.getFullName());
        p.setBirthDate(dto.getBirthDate());
        p.setCpf(dto.getCpf()); // Cuidado ao atualizar CPF se for usado como identificador único e imutável.
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

        // Se a senha for atualizada, ela também precisa ser "hasheada"
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            p.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        // Não atualize profilePhoto aqui a menos que o DTO também o carregue

        p = repo.save(p);
        return toDto(p);
    }

    // Remove um paciente pelo seu ID.
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Paciente não encontrado para exclusão com ID: " + id);
        }
        repo.deleteById(id);
    }

    // Converte um PacienteDto em entidade Paciente.
    private Paciente toEntity(PacienteDto dto) {
        Paciente p = new Paciente();
        // Não sete o ID aqui se for uma nova entidade, deixe o banco gerar
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
        // O isActive pode ter um valor padrão na entidade ou ser definido aqui
        p.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true); 
        // A senha (passwordHash) é tratada no método create/update
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
        // IMPORTANTE: NÃO inclua o passwordHash no DTO que vai para o cliente.
        return dto;
    }
}
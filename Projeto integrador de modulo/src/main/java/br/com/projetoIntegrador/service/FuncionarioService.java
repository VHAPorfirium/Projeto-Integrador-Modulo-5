package br.com.projetoIntegrador.service;

import br.com.projetoIntegrador.dto.FuncionarioDto;
import br.com.projetoIntegrador.model.Funcionario;
import br.com.projetoIntegrador.model.Specialty;
import br.com.projetoIntegrador.repository.FuncionarioRepository;
import br.com.projetoIntegrador.repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciar a lógica de negócio dos funcionários.
 */
@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepo;
    private final SpecialtyRepository specialtyRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public FuncionarioService(FuncionarioRepository funcionarioRepo,
            SpecialtyRepository specialtyRepo,
            PasswordEncoder passwordEncoder) {
        this.funcionarioRepo = funcionarioRepo;
        this.specialtyRepo = specialtyRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cria um novo funcionário.
     *
     * @param dto DTO com os dados do funcionário.
     * @return DTO do funcionário criado.
     */
    @Transactional
    public FuncionarioDto create(FuncionarioDto dto) {
        // Valida se a matrícula já existe
        funcionarioRepo.findByMatricula(dto.getMatricula()).ifPresent(f -> {
            throw new IllegalArgumentException("Matrícula já cadastrada.");
        });

        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória para criar um funcionário.");
        }

        Funcionario funcionario = toEntity(dto);
        funcionario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        Funcionario savedFuncionario = funcionarioRepo.save(funcionario);
        return toDto(savedFuncionario);
    }

    /**
     * Retorna todos os funcionários.
     *
     * @return Lista de DTOs de funcionários.
     */
    @Transactional(readOnly = true)
    public List<FuncionarioDto> findAll() {
        return funcionarioRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca um funcionário pelo ID.
     *
     * @param id ID do funcionário.
     * @return DTO do funcionário encontrado.
     */
    @Transactional(readOnly = true)
    public FuncionarioDto findById(Long id) {
        return funcionarioRepo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com o ID: " + id));
    }

    /**
     * Atualiza um funcionário existente.
     *
     * @param id  ID do funcionário a ser atualizado.
     * @param dto DTO com os novos dados.
     * @return DTO do funcionário atualizado.
     */
    @Transactional
    public FuncionarioDto update(Long id, FuncionarioDto dto) {
        Funcionario funcionario = funcionarioRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com o ID: " + id));

        // Atualiza os campos básicos
        funcionario.setFullName(dto.getFullName());
        funcionario.setMatricula(dto.getMatricula());
        funcionario.setRole(dto.getRole()); // EmployeeRole vindo do DTO
        funcionario.setIsActive(dto.getIsActive());

        // Atualiza a senha se uma nova for fornecida
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            funcionario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        // Atualiza as especialidades, caso tenha sido enviado um conjunto de IDs
        if (dto.getSpecialtyIds() != null) {
            Set<Specialty> specialties = new HashSet<>(specialtyRepo.findAllById(dto.getSpecialtyIds()));
            funcionario.setSpecialties(specialties);
        }

        Funcionario updatedFuncionario = funcionarioRepo.save(funcionario);
        return toDto(updatedFuncionario);
    }

    /**
     * Deleta um funcionário pelo ID.
     *
     * @param id ID do funcionário a ser deletado.
     */
    @Transactional
    public void delete(Long id) {
        if (!funcionarioRepo.existsById(id)) {
            throw new IllegalArgumentException("Funcionário não encontrado com o ID: " + id);
        }
        funcionarioRepo.deleteById(id);
    }

    // --- MÉTODOS DE CONVERSÃO ---

    /**
     * Converte um FuncionarioDto para uma entidade Funcionario.
     */
    private Funcionario toEntity(FuncionarioDto dto) {
        Funcionario entity = new Funcionario();
        entity.setId(dto.getId());
        entity.setFullName(dto.getFullName());
        entity.setMatricula(dto.getMatricula());
        entity.setRole(dto.getRole()); // EmployeeRole
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        if (dto.getSpecialtyIds() != null && !dto.getSpecialtyIds().isEmpty()) {
            Set<Specialty> specialties = new HashSet<>(specialtyRepo.findAllById(dto.getSpecialtyIds()));
            entity.setSpecialties(specialties);
        }

        return entity;
    }

    /**
     * Converte uma entidade Funcionario para um FuncionarioDto.
     * Aqui ajustamos para converter OffsetDateTime → Instant.
     */
    private FuncionarioDto toDto(Funcionario entity) {
        FuncionarioDto dto = new FuncionarioDto();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setMatricula(entity.getMatricula());
        dto.setRole(entity.getRole());
        dto.setIsActive(entity.isActive());

        // CONVERSÃO: OffsetDateTime → Instant
        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(entity.getCreatedAt().toInstant());
        }
        if (entity.getUpdatedAt() != null) {
            dto.setUpdatedAt(entity.getUpdatedAt().toInstant());
        }

        if (entity.getSpecialties() != null) {
            dto.setSpecialtyIds(entity.getSpecialties().stream()
                    .map(Specialty::getId)
                    .collect(Collectors.toSet()));
        }

        // IMPORTANTE: Nunca retorne a senha ou o hash da senha no DTO.
        return dto;
    }
}

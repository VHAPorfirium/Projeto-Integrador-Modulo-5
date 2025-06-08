package br.com.projetoIntegrador.service;

import br.com.projetoIntegrador.dto.LoginRequestDto;
import br.com.projetoIntegrador.dto.LoginResponseDto;
import br.com.projetoIntegrador.model.Funcionario;
import br.com.projetoIntegrador.model.Paciente;
import br.com.projetoIntegrador.repository.FuncionarioRepository;
import br.com.projetoIntegrador.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final PacienteRepository pacienteRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(PacienteRepository pacienteRepository,
            FuncionarioRepository funcionarioRepository,
            PasswordEncoder passwordEncoder) {
        this.pacienteRepository = pacienteRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDto loginPaciente(LoginRequestDto loginRequestDto) {
        // A lógica do paciente permanece a mesma, com senha
        Optional<Paciente> pacienteOptional = pacienteRepository.findByCpf(loginRequestDto.getIdentifier());

        if (pacienteOptional.isEmpty()) {
            return new LoginResponseDto(false, "Paciente não encontrado com o CPF fornecido.", null, null, null);
        }

        Paciente paciente = pacienteOptional.get();
        // CORRIGIDO: Chamando o método correto para o campo booleano
        if (!paciente.getIsActive()) {
            return new LoginResponseDto(false, "Este cadastro de paciente está inativo.", "paciente", paciente.getId(),
                    paciente.getFullName());
        }

        if (passwordEncoder.matches(loginRequestDto.getPassword(), paciente.getPasswordHash())) {
            return new LoginResponseDto(true, "Login de paciente bem-sucedido!", "paciente", paciente.getId(),
                    paciente.getFullName());
        } else {
            return new LoginResponseDto(false, "Senha incorreta para o paciente.", "paciente", paciente.getId(), null);
        }
    }

    /**
     * MÉTODO MODIFICADO PARA NÃO USAR SENHA
     * Agora, o login é bem-sucedido se a matrícula for encontrada.
     */
    public LoginResponseDto loginFuncionario(LoginRequestDto loginRequestDto) {
        // 1. Busca o funcionário pela matrícula
        Optional<Funcionario> funcionarioOptional = funcionarioRepository
                .findByMatricula(loginRequestDto.getIdentifier());

        if (funcionarioOptional.isEmpty()) {
            // 2. Se não encontrou a matrícula, retorna erro.
            return new LoginResponseDto(false, "Funcionário não encontrado com a matrícula fornecida.", null, null,
                    null);
        }

        // 3. Se encontrou, já consideramos o login um sucesso!
        Funcionario funcionario = funcionarioOptional.get();
        // CORRIGIDO: Chamando o método correto para o campo booleano
        if (!funcionario.isActive()) {
            return new LoginResponseDto(false, "Este cadastro de funcionário está inativo.", "funcionario",
                    funcionario.getId(), funcionario.getFullName());
        }

        // A senha não é mais verificada. Se o usuário existe e está ativo, o login é
        // aprovado.
        return new LoginResponseDto(true, "Login de funcionário bem-sucedido!", "funcionario", funcionario.getId(),
                funcionario.getFullName());
    }

    // Métodos para registrar/atualizar senha (podem ser mantidos para uso futuro)
    public Paciente registrarOuAtualizarSenhaPaciente(String cpf, String novaSenha) {
        Paciente paciente = pacienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        paciente.setPasswordHash(passwordEncoder.encode(novaSenha));
        return pacienteRepository.save(paciente);
    }

    public Funcionario registrarOuAtualizarSenhaFuncionario(String matricula, String novaSenha) {
        Funcionario funcionario = funcionarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
        funcionario.setPasswordHash(passwordEncoder.encode(novaSenha));
        return funcionarioRepository.save(funcionario);
    }
}

package br.com.projetoIntegrador.controller;

import br.com.projetoIntegrador.dto.FuncionarioDto;
import br.com.projetoIntegrador.service.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gerenciar as operações CRUD de Funcionários.
 */
@RestController
@RequestMapping("/funcionarios")
@Tag(name = "Funcionários", description = "Endpoints para gerenciar os dados dos funcionários.")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    @Autowired
    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @Operation(summary = "Cria um novo funcionário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<FuncionarioDto> create(@Valid @RequestBody FuncionarioDto dto) {
        FuncionarioDto createdDto = funcionarioService.create(dto);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Lista todos os funcionários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de funcionários retornada com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FuncionarioDto.class))))
    })
    @GetMapping
    public ResponseEntity<List<FuncionarioDto>> listAll() {
        return ResponseEntity.ok(funcionarioService.findAll());
    }

    @Operation(summary = "Busca um funcionário pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Funcionário encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioDto.class))),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioDto> getById(
            @Parameter(description = "ID do funcionário a ser buscado", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(funcionarioService.findById(id));
    }

    @Operation(summary = "Atualiza os dados de um funcionário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioDto> update(
            @Parameter(description = "ID do funcionário a ser atualizado", required = true) @PathVariable Long id,
            @Valid @RequestBody FuncionarioDto dto) {
        return ResponseEntity.ok(funcionarioService.update(id, dto));
    }

    @Operation(summary = "Remove um funcionário pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Funcionário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do funcionário a ser removido", required = true) @PathVariable Long id) {
        funcionarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

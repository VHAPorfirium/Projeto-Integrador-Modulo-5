package br.com.projetoIntegrador.controller;

import br.com.projetoIntegrador.model.Specialty;
import br.com.projetoIntegrador.repository.SpecialtyRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/specialties")
@Tag(name = "Especialidades", description = "Endpoints para gerenciar as especialidades médicas.")
public class SpecialtyController {

    private final SpecialtyRepository specialtyRepository;

    @Autowired
    public SpecialtyController(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @GetMapping
    public ResponseEntity<List<Specialty>> listAllSpecialties() {
        List<Specialty> specialties = specialtyRepository.findAll();
        return ResponseEntity.ok(specialties);
    }
}
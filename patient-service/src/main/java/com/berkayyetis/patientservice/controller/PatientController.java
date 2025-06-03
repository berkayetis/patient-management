package com.berkayyetis.patientservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.berkayyetis.patientservice.dto.PatientRequestDTO;
import com.berkayyetis.patientservice.dto.PatientResponseDTO;
import com.berkayyetis.patientservice.dto.validators.CreatePatientValidationGroup;
import com.berkayyetis.patientservice.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description = "API for managing patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Get all patients", description = "Retrieve a list of all patients")
    public ResponseEntity<List<PatientResponseDTO>> getPatients(@PageableDefault(
            page = 0,
            size = 10,
            sort = "id",
            direction = Sort.Direction.ASC) Pageable pageable) {
        var allPatients = patientService.getPatients(pageable);
        return ResponseEntity.ok().body(allPatients);
    }

    @PostMapping
    @Operation(summary = "Create a new patient", description = "Create a new patient with the provided details")
    public ResponseEntity<PatientResponseDTO> createPatient(
        @Validated({Default.class, CreatePatientValidationGroup.class}) 
        @RequestBody PatientRequestDTO patientRequestDTO) {
        var createdPatient = patientService.createPatient(patientRequestDTO);
        return ResponseEntity.ok().body(createdPatient);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Patient", description = "Update an existing patient")
    public ResponseEntity<PatientResponseDTO> updatePatient(
        @PathVariable UUID id, 
        @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO) {
        var updatedPatient = patientService.updatePatient(id, patientRequestDTO);
        return ResponseEntity.ok().body(updatedPatient);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient", description = "Delete a patient by ID")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}

package com.berkayyetis.patientservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.berkayyetis.patientservice.dto.PagedPatientResponseDTO;
import com.berkayyetis.patientservice.grpc.BillingServiceGrpcClient;
import com.berkayyetis.patientservice.kafka.KafkaProducer;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.berkayyetis.patientservice.dto.PatientRequestDTO;
import com.berkayyetis.patientservice.dto.PatientResponseDTO;
import com.berkayyetis.patientservice.exception.EmailAlreadyExistsException;
import com.berkayyetis.patientservice.exception.PatientNotFoundException;
import com.berkayyetis.patientservice.mapper.PatientMapper;
import com.berkayyetis.patientservice.model.Patient;
import com.berkayyetis.patientservice.repository.PatientRepository;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository,
                          BillingServiceGrpcClient billingServiceGrpcClient,
                          KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    @Cacheable(value = "patients", key = "#size + '-' + #page")
    public PagedPatientResponseDTO getPatients(int page, int size, String sort, String sortField, String searchValue){

        Pageable pageable = PageRequest.of(page, size,
                sort.equalsIgnoreCase("desc")
                        ? Sort.by(sortField).descending()
                        : Sort.by(sortField).ascending());

        Page<Patient> patientPage;
        if(searchValue == null || searchValue.isBlank()){
            patientPage = patientRepository.findAll(pageable);
        }
        else{
            patientPage = patientRepository.findByNameContainingIgnoreCase(searchValue, pageable);
        }

        List<PatientResponseDTO> patientResponseDtos = patientPage.getContent().stream().map(PatientMapper::toDTO).toList();

        return new PagedPatientResponseDTO(
                patientResponseDtos,
                patientPage.getNumber(),
                patientPage.getSize(),
                patientPage.getTotalPages(),
                (int)patientPage.getTotalElements());
    }

    @CachePut(value = "PATIENT", key = "#result.id")
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists:" + patientRequestDTO.getEmail());
        }

        Patient patient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(patient.getId().toString(),
                patient.getName(),
                patient.getEmail());

        kafkaProducer.sendEvent(patient);
        return PatientMapper.toDTO(patient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));

        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Email or id already exists:" + patientRequestDTO.getEmail());
        }

        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        patientRepository.delete(patient);
    }
}

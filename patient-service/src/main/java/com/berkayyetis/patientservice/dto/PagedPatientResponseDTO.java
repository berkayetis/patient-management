package com.berkayyetis.patientservice.dto;

import java.util.List;

public class PagedPatientResponseDTO {
    private List<PatientResponseDTO> patients;
    private int page;
    private int pageSize;
    private int totalPages;
    private int totalElements;
    public PagedPatientResponseDTO() {}

    public PagedPatientResponseDTO(List<PatientResponseDTO> patients,
                                   int page,
                                   int pageSize,
                                   int totalPages,
                                   int totalElements) {
            this.patients = patients;
            this.page = page;
            this.pageSize = pageSize;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
    }

    public List<PatientResponseDTO> getPatients() {
        return patients;
    }

    public void setPatients(List<PatientResponseDTO> patients) {
        this.patients = patients;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }
}

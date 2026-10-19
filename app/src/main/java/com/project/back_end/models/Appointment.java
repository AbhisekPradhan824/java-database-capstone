package com.project.back_end.models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(mappedBy = "doctor", fetch = FetchType.EAGER)
    @NotNull(message = "Doctor cannot be null")
    private Doctor doctor;

    @ManyToOne(mappedBy = "patient", fetch = FetchType.EAGER)
    @NotNull(message = "Patient cannot be null")
    private Patient patient;

    @Future(message = "Appointment time must be in the future")
    private LocalDateTime appointmentTime;
    
    @Transient
    public LocalDateTime getEndTime() {
    	return this.appointmentTime != null ? this.appointmentTime.plusHours(1) : null;
    }

    @NotNull(message = "Status cannot be null")
    private int status; // 0 for Scheduled, 1 for Completed

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    // Helper methods marked with @Transient so they aren't persisted in the database
    
    
    @Transient
    public LocalDate getAppointmentDate() {
    	return this.appointmentTime != null ? this.appointmentTime.toLocalDate() : null;
    }
    
    @Transient
    public LocalTime getAppointmentTimeOnly() {
    	return this.appointmentTime != null ? this.appointmentTime.toLocalTime() : null;
    }
}

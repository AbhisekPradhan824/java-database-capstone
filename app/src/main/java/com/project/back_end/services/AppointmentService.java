package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class AppointmentService {
private final AppointmentRepository appointmentRepository;
private final PatientRepository patientRepository;
private final DoctorRepository doctorRepository;
private final TokenService tokenService;
private final com.project.back_end.services.Service service;
	
	@Autowired
    AppointmentService(AppointmentRepository appointment, com.project.back_end.services.Service servicea, PatientRepository patientRepo, TokenService tokenservice, DoctorRepository doctorRepo) {
        this.appointmentRepository = appointment;
        this.service = servicea;
        this.patientRepository = patientRepo;
        this.tokenService = tokenservice;
        this.doctorRepository = doctorRepo;
    }
	
	/*
	 * Book Appointment Method
	 * Responsible for saving the new appointment to the database
	 */
	
	public int bookAppointment(Appointment appointment) {
		try {
			if(appointment==null) {
				return 0;
			}
			appointmentRepository.save(appointment);
			return 1;
		} catch (Exception e) {
			return 0;
		}
	}

	/*
	 * Update Appointment Method
	 * This method is used to update an existing appointment based on its ID
	 */
	
	public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment){
	    try {
	        if(appointment == null) {
	            return new ResponseEntity<>(Map.of("message", "Invalid User Input"), HttpStatus.BAD_REQUEST);
	        }
	        
	        Optional<Appointment> opt = appointmentRepository.findById(appointment.getId());
	        
	        if(opt.isPresent()) {
	            // Note: Since this is inside your DoctorService, you can call validateAppointment(appointment) directly
	            int validate = service.validateAppointment(appointment); 
	            
	            if(validate == 1) {
	                appointmentRepository.save(appointment);
	                return new ResponseEntity<>(Map.of("message", "Update Successful"), HttpStatus.OK);
	            }
	            
	            return new ResponseEntity<>(Map.of("message", "Failed: Time slot unavailable"), HttpStatus.BAD_REQUEST);
	        } else {
	            return new ResponseEntity<>(Map.of("message", "Failed: Appointment not found"), HttpStatus.NOT_FOUND);
	        }
	        
	    } catch (Exception e) {
	        return new ResponseEntity<>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	/*
	 * cancelAppointment
	 * This method cancels an existing appointment.
	 */

	public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token){
		try {
			if(token==null) {
	            return new ResponseEntity<>(Map.of("message", "Invalid User Input"), HttpStatus.BAD_REQUEST);
			}
			
			boolean valid = tokenService.validateToken(token, "patient");
			
			if(valid) {
				String email = tokenService.extractIdentifier(token);
				Long patientId=patientRepository.findByEmail(email).getId();
				Optional<Appointment> opt =appointmentRepository.findById(id);
				if(opt.isPresent() && opt.get().getPatient().getId().equals(patientId)) {
					appointmentRepository.deleteById(id);
					return new ResponseEntity<>(Map.of("message", "Appointment Deleted"), HttpStatus.OK);
				}
			}
			return new ResponseEntity<>(Map.of("message", "Unauthorized Access"), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
	        return new ResponseEntity<>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	@Transactional
	public Map<String, Object> getAppointment(String pname, LocalDate date, String token){
	    try {
	        if(date == null || token == null) {
	            return Map.of("message", "Invalid User Input");
	        }
	        
	        // 1. Extract email and fetch the doctor's ID from the token
	        String email = tokenService.extractIdentifier(token);
	        Doctor doctor = doctorRepository.findByEmail(email);
	        
	        if (doctor == null) {
	            return Map.of("message", "Doctor not found or Unauthorized");
	        }
	        
	        Long doctorId = doctor.getId();
	        
	        // 2. Define the start and end of the day for the repository query
	        LocalDateTime startOfDay = date.atStartOfDay();
	        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
	        
	        // 3. Fetch appointments for the given doctor and date range[cite: 1, 2]
	        List<Appointment> list = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);
	        
	        // 4. Filter by patient name if provided (and not blank)
	        if (pname != null && !pname.trim().isEmpty()) {
	            list = list.stream()
	                    .filter(app -> app.getPatient() != null && 
	                                   app.getPatient().getName().toLowerCase().contains(pname.toLowerCase()))
	                    .collect(Collectors.toList());
	        }
	        
	        return Map.of("data", list);
	        
	    } catch (Exception e) {
	        return Map.of("message", "Internal Server Error");
	    }
	}


}

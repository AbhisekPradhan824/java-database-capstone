package com.project.back_end.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class PatientService {
	
	private final PatientRepository patientRepository;
	private final AppointmentRepository appointmentRepository;
	private final TokenService tokenService;

	PatientService(PatientRepository patientRepository, TokenService tokenService, AppointmentRepository appointmentRepository) {
		this.patientRepository = patientRepository;
		this.tokenService = tokenService;
		this.appointmentRepository = appointmentRepository;
	}
	
	
	/*
	 * createPatient(Patient patient) Method
	 * Saves a new patient to the database
	 */
	
	public int createPatient(Patient patient) {
		try {
			patientRepository.save(patient);
			return 1;
		} catch (Exception e) {
			return 0;
		}
	}
	
	/*
	 * getPatientAppointment(Long id, String token) Method
		Retrieves a list of appointments for a specific patient
	 */
	
	@Transactional
	public ResponseEntity<Map<String,Object>> getPatientAppointment(Long id, String token){
		try {
			String email = tokenService.extractIdentifier(token);
			
			Patient patient = patientRepository.findByEmail(email);
			if(patient != null && patient.getId().equals(id)) {
				List<Appointment> list = appointmentRepository.findByPatientId(id);
				
				List<AppointmentDTO> appointmentList=new ArrayList<AppointmentDTO>();
				
				list.forEach(li->{
					AppointmentDTO dto = new AppointmentDTO(li.getId(), 
							li.getDoctor() != null?li.getDoctor().getId() : null,
									li.getDoctor() != null ? li.getDoctor().getName() : null, 
											li.getPatient() != null ? li.getPatient().getId() : null, 
													li.getPatient() != null ? li.getPatient().getName() : null, 
															li.getPatient() != null ? li.getPatient().getEmail() : null,
																	li.getPatient() != null ? li.getPatient().getPhone() : null,
																			li.getPatient() != null ? li.getPatient().getAddress() : null,
																					li.getAppointmentTime(), li.getStatus());
					appointmentList.add(dto);
				});
				return new ResponseEntity<Map<String, Object>>(Map.of("data", appointmentList), HttpStatus.OK);
			}else {
				return new ResponseEntity<Map<String, Object>>(Map.of("message", "Unauthorized Access"), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<Map<String, Object>>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/*
	 * filterByCondition(String condition, Long id) Method
		Filters appointments by condition (past or future) for a specific patient
	 */

	public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id){
try {
	if (condition == null || id==null||  (!condition.equalsIgnoreCase("past") && !condition.equalsIgnoreCase("future"))) {
        return new ResponseEntity<>(Map.of("message", "Invalid input parameters"), HttpStatus.BAD_REQUEST);
    }
	
	List<Appointment> appointmentList=	appointmentRepository.findByPatientId(id);
	List<Appointment> filteredAppointment = appointmentList.stream().filter(appointment->{
		if(condition.equalsIgnoreCase("past")) {
			return appointment.getStatus()==1;
		}else if(condition.equalsIgnoreCase("future")){
			return appointment.getStatus()==0;
		}
		return false;
	}).collect(Collectors.toList());
	
	return new ResponseEntity<Map<String, Object>>(Map.of("data", filteredAppointment), HttpStatus.OK);
} catch (Exception e) {
	return new ResponseEntity<Map<String, Object>>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
}
	}
	
	/*
	 * filterByDoctor(String name, Long patientId) Method
		Filters the patient's appointments by doctor's name
	 */
	
	public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId){
		try {
			
			if (name == null || patientId==null) {
		        return new ResponseEntity<>(Map.of("message", "Invalid input parameters"), HttpStatus.BAD_REQUEST);
		    }
			
			List<Appointment> appointmentList = appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);
			return new ResponseEntity<Map<String, Object>>(Map.of("data", appointmentList), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Map<String, Object>>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	/*
	 * filterByDoctorAndCondition(String condition, String name, long patientId)
		Filters the patient's appointments by doctor's name 
		and appointment condition (past or future)
	 */
	
	public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId){
	    try {
	        // Safeguard against null or invalid inputs
	        if (condition == null || name == null || (!condition.equalsIgnoreCase("past") && !condition.equalsIgnoreCase("future"))) {
	            return new ResponseEntity<>(Map.of("message", "Invalid input parameters"), HttpStatus.BAD_REQUEST);
	        }

	        // Fetch appointments from repository
	        List<Appointment> appointmentList = appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);
	        
	        // If no appointments found, you can return early or let the filter return an empty list
	        if (appointmentList == null) {
	            appointmentList = new ArrayList<>();
	        }

	        // Filter by condition
	        List<Appointment> filteredAppointment = appointmentList.stream().filter(appointment -> {
	            if (condition.equalsIgnoreCase("past")) {
	                return appointment.getStatus() == 1;
	            } else if (condition.equalsIgnoreCase("future")) {
	                return appointment.getStatus() == 0;
	            }
	            return false;
	        }).collect(Collectors.toList());
	        
	        return new ResponseEntity<>(Map.of("data", filteredAppointment), HttpStatus.OK);

	    } catch (Exception e) {
	        return new ResponseEntity<>(Map.of("message", "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	
	/*
	 * getPatientDetails(String token)
		Fetches the patient's details based on the provided JWT token
	 */
	
	
	public ResponseEntity<Map<String, Object>> getPatientDetails(String token){
		try {
			//Checking for input
			if(token==null) {
				return new ResponseEntity<>(Map.of("message", "Invalid input parameters"), HttpStatus.BAD_REQUEST);
			}
			
			String email = tokenService.extractIdentifier(token);
			Patient patient = patientRepository.findByEmail(email);
			
			if(patient==null) {
				return new ResponseEntity<>(Map.of("message", "No Data found"), HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(Map.of("data", patient), HttpStatus.OK);
			
			
		} catch (Exception e) {
			return new ResponseEntity<>(Map.of("message", "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

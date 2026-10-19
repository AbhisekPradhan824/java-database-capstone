package com.project.back_end.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@org.springframework.stereotype.Service("myService")
public class Service {
	private final TokenService tokenService;
	private final AdminRepository adminRepository;
	private final DoctorRepository doctorRepository;
	private final PatientRepository patientRepository;
	private final DoctorService doctorService;
	private final PatientService patientService;
	
	@Autowired
	public Service(TokenService tokenService, AdminRepository adminRepository, DoctorRepository doctorRepository,
			PatientRepository patientRepository, DoctorService doctorService, PatientService patientService) {
		this.tokenService = tokenService;
		this.adminRepository = adminRepository;
		this.doctorRepository = doctorRepository;
		this.patientRepository = patientRepository;
		this.doctorService = doctorService;
		this.patientService = patientService;
	}
	
	
	/*
	 * validateToken: 
	 * This method checks the validity of a token for a given user
	 */
	
	public ResponseEntity<Map<String, String>> validateToken(String token, String user){
		try {
			if(token==null || user==null) {
				return new ResponseEntity<Map<String, String>>(Map.of("message", "Invalid user input"), HttpStatus.BAD_REQUEST);
			}
			
			boolean validate = tokenService.validateToken(token, user);
			if(validate) {
				
				return new ResponseEntity<Map<String,String>>(Map.of("validate", "Authorized"), HttpStatus.OK);
			}else {
				return new ResponseEntity<Map<String,String>>(Map.of("validate", "Unauthorized"), HttpStatus.UNAUTHORIZED);
			}
			
		} catch (Exception e) {
			return new ResponseEntity<Map<String, String>>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
	
	
	/*
	 * validateAdmin: 
	 * This method validates the login credentials of an admin.
	 */
	
	public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin){
		try {
			if(receivedAdmin==null) {
				return new ResponseEntity<Map<String, String>>(Map.of("message", "Invalid User Input"), HttpStatus.BAD_REQUEST);
			}
	
			Admin admin =adminRepository.findByUsername(receivedAdmin.getUsername());
			if(admin!=null && receivedAdmin.getPassword().equals(admin.getPassword())) {
				String token = tokenService.generateToken(admin.getUsername());
				return new ResponseEntity<Map<String,String>>(Map.of("token", token), HttpStatus.OK);
			} 
				return new ResponseEntity<Map<String,String>>(Map.of("message", "Invalid username or Password"), HttpStatus.UNAUTHORIZED);
			
		} catch (Exception e) {
			return new ResponseEntity<Map<String, String>>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	/*
	 * filterDoctor
	 * This method filters doctors based on name, 
	 * specialty, and available time.
	 */
	
	public 	Map<String, Object> filterDoctor(String name, String specialty, String time){
		try {
			if(name==null || specialty==null || time==null) {
				return Map.of("message", "Invalid User Input");
			}
			Map<String, Object> doctorList = doctorService.filterDoctorsByNameSpecialityandTime(name, specialty,time);
			return doctorList;
		} catch (Exception e) {
			return Map.of("message", "Internal Server Error");
			
		}
	}
	
	/*
	 * validateAppointment
	 * This method validates whether an appointment is available based on 
	 * the doctor's schedule.
	 */
	
	public int validateAppointment(Appointment appointment) {
		try {
			if (appointment == null || appointment.getDoctor() == null || appointment.getAppointmentTime() == null) {
	            return 0;
	        }
			Long doctorId = appointment.getDoctor().getId();
			Optional<Doctor> opt = doctorRepository.findById(doctorId);
			if(opt.isEmpty()) {
				return -1;
			}
			LocalDate date=appointment.getAppointmentDate();
			List<String> availableSlots = doctorService.getDoctorAvailability(doctorId, date);
			
			if(availableSlots==null || availableSlots.isEmpty()) {
				return 0;
			}
			
			String requestedTimeStr = appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"));

	        boolean isValid = availableSlots.stream().anyMatch(slot -> {
	            String startTime = slot.contains("-") ? slot.split("-")[0] : slot;
	            return startTime.equals(requestedTimeStr);
	        });

	        return isValid ? 1 : 0;
			
		} catch (Exception e) {
			return 0;
		}
	}
	
	
	/*
	 * validatePatient
	 * This method checks whether a patient exists 
	 * based on their email or phone number.
	 */
	
	public boolean validatePatient(Patient patient) {
		try {
			if(patient==null) {
				return true;
			}
		Patient patientFetched =	patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
		if(patientFetched!=null) {
			return false;
		}
		return true;
		} catch (Exception e) {
			return true;
		}
	}
	
	
	/*
	 * validatePatientLogin
	 * This method validates a patient's 
	 * login credentials (email and password)
	 */
	
	public ResponseEntity<Map<String, String>> validatePatientLogin(Login login){
		try {
			if(login==null) {
				return new ResponseEntity<Map<String, String>>(Map.of("message", "Invalid User Input"), HttpStatus.BAD_REQUEST);
			}
			String email = login.getEmail();
			Patient patient = patientRepository.findByEmail(email);
			if(patient!=null && login.getPassword().equals(patient.getPassword())) {
				String token = tokenService.generateToken(email);
				return new ResponseEntity<Map<String, String>>(Map.of("token", token), HttpStatus.OK);
			}
			return new ResponseEntity<Map<String, String>>(Map.of("message", "Unauthorized Login"), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			return new ResponseEntity<Map<String, String>>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	/*
	 * filterPatient Method
	 * This method filters a patient's appointment history based on 
	 * condition and doctor name.
	 */

	public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token){
		try {
			if(condition==null || name==null || token==null) {
				return new ResponseEntity<Map<String, Object>>(Map.of("message", "Invalid User Input"), HttpStatus.BAD_REQUEST);
			}
			
		String email = 	tokenService.extractIdentifier(token);
		Patient patient = patientRepository.findByEmail(email);
		
		if(patient==null) {
			return new ResponseEntity<Map<String, Object>>(Map.of("message", "Patient Does not exist"), HttpStatus.BAD_REQUEST);
		}
			
		ResponseEntity<Map<String, Object>> response = 	patientService.filterByDoctorAndCondition(condition, name, patient.getId());
		return response;
		} catch (Exception e) {
			return new ResponseEntity<Map<String, Object>>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

}

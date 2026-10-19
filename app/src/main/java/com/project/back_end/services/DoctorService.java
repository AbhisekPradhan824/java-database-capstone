package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;


@Service
public class DoctorService {
	private final DoctorRepository doctorRepository;
	private final AppointmentRepository appointmentRepository;
	private final TokenService tokenService;

	DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, TokenService tokenService) {
		this.doctorRepository = doctorRepository;
		this.appointmentRepository = appointmentRepository;
		this.tokenService = tokenService;
	}
	
	/*
	 * getDoctorAvailability
	 * This method fetches the available slots for a 
	 * specific doctor on a given date.
	 */
	
	public List<String> getDoctorAvailability(Long doctorId, LocalDate date){
		List<Appointment> allAppointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, 
														date.atStartOfDay(), date.atTime(LocalTime.MAX));
		
	
		List<String> availableSlots = allAppointments.stream().filter(appointment->appointment.getStatus()==0).map(appointment->appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"))).collect(Collectors.toList());
		
		return availableSlots;
	}
	
	/*
	 * saveDoctor: This method saves a new doctor to the database.
	 */
	@Transactional(readOnly = true)
	public int saveDoctor(Doctor doctor) {
		int flag=-1;
		try {
			Doctor doc = doctorRepository.findByEmail(doctor.getEmail());
			
			if(doc==null) {
				doctorRepository.save(doctor);
				flag=1;
			}
		}catch(Exception e) {
			flag=0;
		}
		return flag;	
	}
	
	/*
	 * updateDoctor: This method updates the details of an existing doctor.
	 */
	
	public int updateDoctor(Doctor doctor) {
		int flag=-1;
		try {
			Optional<Doctor> opt = doctorRepository.findById(doctor.getId());
			
			if(opt.isPresent()) {
				doctorRepository.save(doctor);
				flag=1;
			}
		}catch(Exception e) {
			flag=0;
		}
		return flag;
	}
	
	/*
	 * getDoctors: This method retrieves a list of all doctors.
	 */
	
	@Transactional(readOnly = true)
	public List<Doctor> getDoctors(){
		List<Doctor> allDoctors = doctorRepository.findAll();
		return allDoctors;
	}
	
	
	/*
	 * deleteDoctor: This method deletes a doctor by ID.
	 */
	
	@Transactional(readOnly = true)
	public int deleteDoctor(long id) {
		int flag=-1;
		try {
			Optional<Doctor> opt = doctorRepository.findById(id);
			
			if(opt.isPresent()) {
				appointmentRepository.deleteAllByDoctorId(id);
				doctorRepository.deleteById(id);
				flag=1;
			}
		}catch(Exception e) {
			flag=0;
		}
		return flag;
	}
	
	
	/*
	 * validateDoctor: This method validates a doctor's login credentials.
	 */
	
	public ResponseEntity<Map<String, String>> validateDoctor(Login login){
		try {
			if(login == null) {
				return new ResponseEntity<Map<String, String>>(Map.of("message", "Invalid User Input"), HttpStatus.BAD_REQUEST);			
			}
			Doctor doctor = doctorRepository.findByEmail(login.getEmail());
			
			if(doctor!=null) {
				if(login.getPassword().equals(doctor.getPassword())) {
					String token = tokenService.generateToken(doctor.getEmail());
					return new ResponseEntity<Map<String, String>>(Map.of("token", token), HttpStatus.OK);
				}
			}
			
			return new ResponseEntity<Map<String, String>>(Map.of("message", "Invalid username or password"), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			return new ResponseEntity<Map<String, String>>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	/*
	 * findDoctorByName: This method finds doctors by their name.
	 */
	
	
	@Transactional(readOnly = true)
	public Map<String, Object> findDoctorByName(String name){
		if(name==null) {
			return Map.of("message", "Invalid User Input");
		}
		List<Doctor> list= doctorRepository.findByNameLike("%"+name+"/%");
		return Map.of("data", list);
	}
	
	
	/*
	 * filterDoctorsByNameSpecilityandTime: 
	 * This method filters doctors by name, specialty, 
	 * and availability during AM/PM.
	 */
	
	
	public Map<String, Object> filterDoctorsByNameSpecialityandTime(String name, String specialty, String amOrPm) {
		if(name==null || specialty==null || amOrPm==null) {
			return Map.of("message", "Invalid User Input");
		}
		List<Doctor> doctors=doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
		
		List<Doctor> filteredDoctors=doctors.stream().filter(doctor->{
			List<String> times = doctor.getAvailableTimes();
            if (times == null || times.isEmpty()) {
                return false;
            }
            
            return times.stream().anyMatch(range -> {
                // range looks like "11:00-14:00"
                String[] parts = range.split("-");
                int startHour = Integer.parseInt(parts[0].split(":")[0]); // e.g., 11
                int endHour = Integer.parseInt(parts[1].split(":")[0]);     // e.g., 14
                
                if ("AM".equalsIgnoreCase(amOrPm)) {
                    // Matches if any part of the slot is in the morning (starts before 12:00)
                    return startHour < 12;
                } else if ("PM".equalsIgnoreCase(amOrPm)) {
                    // Matches if the slot extends into or starts in the afternoon/evening (ends after 12:00 or starts at/after 12:00)
                    return endHour > 12 || startHour >= 12;
                }
                return false;
            });
        }).collect(Collectors.toList());
		
		return Map.of("data", filteredDoctors);
	}

	/*
	 * filterDoctorByNameAndTime
	 * This method filters doctors by name 
	 * and their availability during AM/PM.
	 */
	
	public Map<String, Object> filterDoctorByNameandTime(String name,  String amOrPm) {
		List<Doctor> allDoctors=doctorRepository.findAll();
		
		List<Doctor> filteredDoctorsByName=allDoctors
									.stream()
									.filter(doctor->doctor.getName()
									.toLowerCase().contains(name.toLowerCase()))
									.collect(Collectors.toList());
		
		
		List<Doctor> filteredDoctorsByTime=filteredDoctorsByName.stream().filter(doctor->{
			List<String> times = doctor.getAvailableTimes();
            if (times == null || times.isEmpty()) {
                return false;
            }
            
            return times.stream().anyMatch(range -> {
                // range looks like "11:00-14:00"
                String[] parts = range.split("-");
                int startHour = Integer.parseInt(parts[0].split(":")[0]); // e.g., 11
                int endHour = Integer.parseInt(parts[1].split(":")[0]);     // e.g., 14
                
                if ("AM".equalsIgnoreCase(amOrPm)) {
                    // Matches if any part of the slot is in the morning (starts before 12:00)
                    return startHour < 12;
                } else if ("PM".equalsIgnoreCase(amOrPm)) {
                    // Matches if the slot extends into or starts in the afternoon/evening (ends after 12:00 or starts at/after 12:00)
                    return endHour > 12 || startHour >= 12;
                }
                return false;
            });
        }).collect(Collectors.toList());
		
		return Map.of("data", filteredDoctorsByTime);
	}
	
	
	
	/*
	 * filterDoctorByNameAndSpecility 
	 * This method filters doctors by name and specialty.
	 */
	
	public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specilty){
		List<Doctor> doctorList=doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specilty);
		
		return Map.of("data", doctorList);
	}
	
	/*
	 * filterDoctorByTimeAndSpecility
	 * This method filters doctors by specialty and their availability during AM/PM.
	 */

	public Map<String, Object> filterDoctorByTimeAndSpecility(String specilty, String amOrPm){
		List<Doctor> doctorList = doctorRepository.findBySpecialtyIgnoreCase(specilty);
		
		List<Doctor> filteredDoctors=doctorList.stream().filter(doctor->{
			List<String> times = doctor.getAvailableTimes();
            if (times == null || times.isEmpty()) {
                return false;
            }
            
            return times.stream().anyMatch(range -> {
                // range looks like "11:00-14:00"
                String[] parts = range.split("-");
                int startHour = Integer.parseInt(parts[0].split(":")[0]); // e.g., 11
                int endHour = Integer.parseInt(parts[1].split(":")[0]);     // e.g., 14
                
                if ("AM".equalsIgnoreCase(amOrPm)) {
                    // Matches if any part of the slot is in the morning (starts before 12:00)
                    return startHour < 12;
                } else if ("PM".equalsIgnoreCase(amOrPm)) {
                    // Matches if the slot extends into or starts in the afternoon/evening (ends after 12:00 or starts at/after 12:00)
                    return endHour > 12 || startHour >= 12;
                }
                return false;
            });
        }).collect(Collectors.toList());
		
		return Map.of("data", filteredDoctors);
	}
	
	
	/*
	 * filterDoctorBySpecility: 
	 * This method filters doctors by specialty.
	 */
	
	public Map<String, Object> filterDoctorBySpecility(String specilty){
		List<Doctor> doctors=doctorRepository.findBySpecialtyIgnoreCase(specilty);
		return Map.of("data", doctors);
	}
	
	/*
	 * filterDoctorsByTime: 
	 * This method filters doctors by 
	 * their availability during AM/PM.
	 */
	
	public Map<String, Object> filterDoctorsByTime(String amOrPm){
		List<Doctor> doctorList = doctorRepository.findAll();
		List<Doctor> filteredDoctors=doctorList.stream().filter(doctor->{
			List<String> times = doctor.getAvailableTimes();
            if (times == null || times.isEmpty()) {
                return false;
            }
            
            return times.stream().anyMatch(range -> {
                // range looks like "11:00-14:00"
                String[] parts = range.split("-");
                int startHour = Integer.parseInt(parts[0].split(":")[0]); // e.g., 11
                int endHour = Integer.parseInt(parts[1].split(":")[0]);     // e.g., 14
                
                if ("AM".equalsIgnoreCase(amOrPm)) {
                    // Matches if any part of the slot is in the morning (starts before 12:00)
                    return startHour < 12;
                } else if ("PM".equalsIgnoreCase(amOrPm)) {
                    // Matches if the slot extends into or starts in the afternoon/evening (ends after 12:00 or starts at/after 12:00)
                    return endHour > 12 || startHour >= 12;
                }
                return false;
            });
        }).collect(Collectors.toList());
		
		return Map.of("data", filteredDoctors);
	}
	
	/*
	 * filterDoctorByTime
	 *  This private method filters a list of doctors
	 *   by their available times (AM/PM).
	 */
	
	private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm){
		List<Doctor> filteredDoctors=doctors.stream().filter(doctor->{
			List<String> times = doctor.getAvailableTimes();
            if (times == null || times.isEmpty()) {
                return false;
            }
            
            return times.stream().anyMatch(range -> {
                // range looks like "11:00-14:00"
                String[] parts = range.split("-");
                int startHour = Integer.parseInt(parts[0].split(":")[0]); // e.g., 11
                int endHour = Integer.parseInt(parts[1].split(":")[0]);     // e.g., 14
                
                if ("AM".equalsIgnoreCase(amOrPm)) {
                    // Matches if any part of the slot is in the morning (starts before 12:00)
                    return startHour < 12;
                } else if ("PM".equalsIgnoreCase(amOrPm)) {
                    // Matches if the slot extends into or starts in the afternoon/evening (ends after 12:00 or starts at/after 12:00)
                    return endHour > 12 || startHour >= 12;
                }
                return false;
            });
        }).collect(Collectors.toList());
		
		return filteredDoctors;
	}
   
}

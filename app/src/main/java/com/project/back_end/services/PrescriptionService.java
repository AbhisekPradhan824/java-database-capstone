package com.project.back_end.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;

	public PrescriptionService(PrescriptionRepository prescriptionRepository) {
		this.prescriptionRepository = prescriptionRepository;
	}
   
	
	/*
	 * savePrescription(Prescription prescription) Method
		Saves a prescription to the database
	 */
	
	public ResponseEntity<Map<String, Object>> savePrescription(Prescription prescription){
		try {
			Optional<Prescription> opt = prescriptionRepository.findById(prescription.getId());
			
			if(opt.isEmpty()) {
				prescriptionRepository.save(prescription);
				return new ResponseEntity<Map<String, Object>>(Map.of("message", "Prescription saved"),HttpStatus.CREATED);
			}
			return new ResponseEntity<Map<String, Object>>(Map.of("message", "Prescription already exist"),HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<Map<String, Object>>(Map.of("message", "Internal Server Error"),HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
		/*
		 * getPrescription(Long appointmentId)
		 * Retrieves the prescription associated with a specific appointment ID
		 */
		
		public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId){
			try {
				
				if(appointmentId==null) {
				return new ResponseEntity<Map<String, Object>>(Map.of("data", "Invalid user input"),HttpStatus.BAD_REQUEST);
				}
				
				 List<Prescription> prescription= prescriptionRepository.findByAppointmentId(appointmentId);
				if(prescription != null) {
					return new ResponseEntity<Map<String, Object>>(Map.of("data", prescription),HttpStatus.OK);
				}
				return new ResponseEntity<Map<String, Object>>(Map.of("message", "Prescription not found"),HttpStatus.NOT_FOUND);
			} catch (Exception e) {
				return new ResponseEntity<Map<String, Object>>(Map.of("message", "Internal Server Error"),HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
}

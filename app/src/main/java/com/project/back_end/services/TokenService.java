package com.project.back_end.services;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final long EXPIRATION_TIME = 7L * 24 * 60 * 60 * 1000; // 7 days in milliseconds

    public TokenService(AdminRepository adminRepository, 
                        DoctorRepository doctorRepository, 
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Retrieves the signing key used for JWT token signing.
     */
    public SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for a given user's identifier.
     */
    public String generateToken(String identifier) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(identifier)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the identifier (subject) from a JWT token.
     */
    public String extractIdentifier(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Replaces setSigningKey()
                .build()
                .parseSignedClaims(token)     // Replaces parseClaimsJws()
                .getPayload()                 // Replaces getBody()
                .getSubject();
    }

    /**
     * Validates the JWT token for a given user type (admin, doctor, or patient).
     */
    public boolean validateToken(String token, String userType) {
        try {
            String identifier = extractIdentifier(token);
            if (identifier == null || identifier.isEmpty()) {
                return false;
            }

            // Check existence in the database based on user type
            switch (userType.toLowerCase()) {
                case "admin":
                    // Assuming Admin is identified by username
                    return adminRepository.findByUsername(identifier).getId()!=0;
                case "doctor":
                    // Assuming Doctor is identified by email
                    return doctorRepository.findByEmail(identifier).getId()!=0;
                case "patient":
                    // Assuming Patient is identified by email
                    return patientRepository.findByEmail(identifier).getId()!=0;
                default:
                    return false;
            }
        } catch (JwtException | IllegalArgumentException e) {
            // Token is invalid, expired, or malformed
            return false;
        }
    }
}

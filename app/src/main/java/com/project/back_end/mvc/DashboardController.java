package com.project.back_end.mvc;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.back_end.services.Service;

@Controller
public class DashboardController {

    @Autowired
    private Service service;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token){
        Map<String, String> map = service.validateToken(token, "admin").getBody();
        System.out.println("map: " + map);
        
        // ✅ Check if token is authorized instead of checking if map is empty
        if (map != null && "Authorized".equals(map.get("validate"))) {
            return "admin/adminDashboard";
        }
        return "redirect:http://localhost:8080"; 
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token){
        Map<String, String> map = service.validateToken(token, "doctor").getBody();
        System.out.println("map: " + map);
        
        // ✅ Check if token is authorized
        if (map != null && "Authorized".equals(map.get("validate"))) {
            return "doctor/doctorDashboard";
        }
        return "redirect:http://localhost:8080"; 
    }
}
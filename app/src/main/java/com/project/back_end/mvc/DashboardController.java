package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;
import com.project.back_end.services.Service;

@Controller
public class DashboardController {

    @Autowired
    private Service service;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable("token") String token){
       Map<String, Object> validate= service.validateToken(token,"admin");
       if(validate !=null && validate.isEmpty()){
        return "admin/adminDashboard";
       } else{
        return "redirect:http://localhost:8080";
       }
    }
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@Pathvariable("token") String token){
    Map<String, Object> validate=service.valiateToken(token, "doctor");
    if(validate != null && validate.isEmpty()){
        return "doctor/doctorDashboard";
    }else{
        return "redirect:http://localhost:8080";
    }
}
}

package com.SmartHealthRemoteSystem.SHSR.registerPatient;

import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Service.UserService;
import com.SmartHealthRemoteSystem.SHSR.User.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/registerPatient")
public class registerPatientController {
    private final PatientService patientService;
    private final UserService userService;

    @Autowired
    public registerPatientController(PatientService patientService, UserService userService) {
        this.patientService = patientService;
        this.userService = userService;
    }
    @GetMapping()
    public String registerForm(){
        return "registerPatientForm";
    }

    @PostMapping("/submit")
    public String registerPatient(
            @RequestParam("fullname")String fullName,@RequestParam("id")String id,
            @RequestParam("password")String password, @RequestParam("phone")String phoneNum,
            @RequestParam("address")String address, @RequestParam("emergency")String emergency,
            @RequestParam("role")String role, Model model) throws ExecutionException, InterruptedException {

        String status= "Under Surveillance";
        Patient newPatient=new Patient(id,fullName,password,phoneNum,role,"",address,emergency,"",status);
        patientService.createPatient(newPatient);

        return "login";
    }
}

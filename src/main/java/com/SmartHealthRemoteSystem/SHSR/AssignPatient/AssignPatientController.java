package com.SmartHealthRemoteSystem.SHSR.AssignPatient;

import com.SmartHealthRemoteSystem.SHSR.Mail.MailStructure;
import com.SmartHealthRemoteSystem.SHSR.Service.AssignPatientServices;
import com.SmartHealthRemoteSystem.SHSR.Service.MailService;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/assignpatient")
public class AssignPatientController {
    public final AssignPatientServices assignPatientServices;
    private final MailService mailService;

    public AssignPatientController(AssignPatientServices assignPatientServices, MailService mailService) {
        this.assignPatientServices = assignPatientServices;
        this.mailService = mailService;
    }

    @GetMapping
    public String AssignPatientForm(Model model,  @RequestParam(defaultValue = "0") int pageNo, 
    @RequestParam(defaultValue = "5") int pageSize, @RequestParam(defaultValue = "") String searchQuery) throws ExecutionException, InterruptedException{
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails= (MyUserDetails) auth.getPrincipal();
        Doctor doctor = assignPatientServices.getDoctor(myUserDetails.getUsername());
        // List<Patient> patientList= assignPatientServices.getListPatient();

        List<Patient> allPatients = assignPatientServices.getListPatient();
  
         if (!searchQuery.isEmpty()) {
        allPatients = allPatients.stream()
                                 .filter(p -> p.getName().toLowerCase().contains(searchQuery.toLowerCase()) 
                                           || p.getUserId().toString().contains(searchQuery))
                                 .collect(Collectors.toList());
    }

        int total = allPatients.size();
        int start = Math.min(pageNo * pageSize, total);
        int end = Math.min((pageNo + 1) * pageSize, total);
        int startIndex = pageNo * pageSize;


        List<Patient> patientList = allPatients.subList(start, end);

        model.addAttribute("startIndex", startIndex);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
        model.addAttribute("patientList", patientList);
        model.addAttribute("doctor",doctor);
        model.addAttribute("searchQuery", searchQuery);

        return "assignpatient";
    }

    @PostMapping("/assigntoDoctor")
    public String AssigntoDoctor(Model model, @RequestParam (value= "patientId")String patientID,  @RequestParam(defaultValue = "0") int pageNo, 
    @RequestParam(defaultValue = "5") int pageSize, @RequestParam(defaultValue = "") String searchQuery) throws ExecutionException, InterruptedException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails= (MyUserDetails) auth.getPrincipal();
        Doctor doctor = assignPatientServices.getDoctor(myUserDetails.getUsername());
        Patient patient =assignPatientServices.getPatient(patientID);
        assignPatientServices.AssignPatient(patientID,doctor.getUserId());
        // List<Patient> patientList= assignPatientServices.getListPatient();

        List<Patient> allPatients = assignPatientServices.getListPatient();


        if (!searchQuery.isEmpty()) {
            allPatients = allPatients.stream()
                                     .filter(p -> p.getName().toLowerCase().contains(searchQuery.toLowerCase()) 
                                               || p.getUserId().toString().contains(searchQuery))
                                     .collect(Collectors.toList());
        }
    
            int total = allPatients.size();
            int start = Math.min(pageNo * pageSize, total);
            int end = Math.min((pageNo + 1) * pageSize, total);
            int startIndex = pageNo * pageSize;


            List<Patient> patientList = allPatients.subList(start, end);

        //SMTP - Izzati

        
        var to = patient.getEmail();
        if (to != null && !to.isEmpty()) {
            var subject = "You have been ASSIGNED to a new Doctor";
            var message = "Hello Dear "+patient.getName()+", We are to inform you that you have been ASSIGNED to a Dr. ("+doctor.getName()+"-"+doctor.getUserId()+")\n\n"
            +"For any further information, do not hesitate to email us back.\nThank you.";
            var mailStructure = new MailStructure(to,subject,message);
            mailService.sendAssignedMail(to, subject, message, mailStructure);
        }
        
        model.addAttribute("startIndex", startIndex);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
        model.addAttribute("patientList",patientList);
        model.addAttribute("doctor",doctor);
        model.addAttribute("searchQuery", searchQuery);
        return "assignpatient";
    }

    @PutMapping("/unassignDoctor")
    public String UnassignDoctor(Model model,@RequestParam (value="patientId")String patientID,  @RequestParam(defaultValue = "0") int pageNo, 
    @RequestParam(defaultValue = "5") int pageSize, @RequestParam(defaultValue = "") String searchQuery) throws ExecutionException, InterruptedException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails= (MyUserDetails) auth.getPrincipal();
        Doctor doctor = assignPatientServices.getDoctor(myUserDetails.getUsername());
        Patient patient =assignPatientServices.getPatient(patientID);
        assignPatientServices.UnassignDoctor(patientID,doctor.getUserId());
        // List<Patient> patientList=assignPatientServices.getListPatient();

        List<Patient> allPatients = assignPatientServices.getListPatient();


        if (!searchQuery.isEmpty()) {
            allPatients = allPatients.stream()
                                     .filter(p -> p.getName().toLowerCase().contains(searchQuery.toLowerCase()) 
                                               || p.getUserId().toString().contains(searchQuery))
                                     .collect(Collectors.toList());
        }
    
            int total = allPatients.size();
            int start = Math.min(pageNo * pageSize, total);
            int end = Math.min((pageNo + 1) * pageSize, total);
            int startIndex = pageNo * pageSize;


            List<Patient> patientList = allPatients.subList(start, end);

        // SMTP - Izzati
        var to = patient.getEmail();
        if (to != null && !to.isEmpty()) {
            var subject = "You have been UNASSIGNED from a Doctor";
            var message = "Hello Dear " + patient.getName() + ", We are to inform you that you have been UNASSIGNED from Dr. (" + doctor.getName() + "-" + doctor.getUserId() + ")\n\n"
                    + "For any further information, do not hesitate to email us back.\nThank you.";
            var mailStructure = new MailStructure(to, subject, message);
            mailService.sendUnassignedMail(to, subject, message, mailStructure);
        } 


        
        model.addAttribute("startIndex", startIndex);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
        model.addAttribute("patientList",patientList);
        model.addAttribute("doctor",doctor);
        model.addAttribute("searchQuery", searchQuery);

        return "assignpatient";

    }
    
    @PostMapping("/releasepatient")
    public String ReleasePatient(Model model,@RequestParam (value ="patientId")String patientID, @RequestParam(defaultValue = "0") int pageNo, 
    @RequestParam(defaultValue = "5") int pageSize, @RequestParam(defaultValue = "") String searchQuery) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails= (MyUserDetails) auth.getPrincipal();
        Doctor doctor = assignPatientServices.getDoctor(myUserDetails.getUsername());
        Patient patient =assignPatientServices.getPatient(patientID);
        assignPatientServices.ReleasePatient(patientID,doctor.getUserId());
        // List<Patient> patientList=assignPatientServices.getListPatient();

        List<Patient> allPatients = assignPatientServices.getListPatient();


        if (!searchQuery.isEmpty()) {
            allPatients = allPatients.stream()
                                     .filter(p -> p.getName().toLowerCase().contains(searchQuery.toLowerCase()) 
                                               || p.getUserId().toString().contains(searchQuery))
                                     .collect(Collectors.toList());
        }
    
            int total = allPatients.size();
            int start = Math.min(pageNo * pageSize, total);
            int end = Math.min((pageNo + 1) * pageSize, total);
            int startIndex = pageNo * pageSize;


            List<Patient> patientList = allPatients.subList(start, end);

        //SMTP - Izzati
        var to = patient.getEmail();

        if (to != null && !to.isEmpty()) {
        var subject = "You have been RELEASED from a Doctor";
        var message = "Hello Dear "+patient.getName()+", We are to inform you that you have been RELEASED from  Dr. ("+doctor.getName()+"-"+doctor.getUserId()+")\n\n"
        +"For any further information, do not hesitate to email us back.\nThank you.";
        var mailStructure = new MailStructure(to,subject,message);
        mailService.sendUnassignedMail(to, subject, message, mailStructure);
        }

        model.addAttribute("startIndex", startIndex);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
        model.addAttribute("patientList",patientList);
        model.addAttribute("doctor",doctor);
        model.addAttribute("searchQuery", searchQuery);
        
        return "assignpatient";
    }
}

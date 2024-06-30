package com.SmartHealthRemoteSystem.SHSR.SendPrescriptions;

import com.SmartHealthRemoteSystem.SHSR.Medicine.Medicine;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.MedicineService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Service.PrescriptionService;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription.PrescribeMedicine;
import com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription.Prescription;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.print.attribute.standard.Media;

@Controller
@RequestMapping("/prescription")
public class SendPrescriptionController {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final PrescriptionService prescriptionService;
    private final MedicineService medicineService;

    @Autowired
    public SendPrescriptionController(DoctorService doctorService, PatientService patientService, PrescriptionService prescriptionService, MedicineService medicineService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.prescriptionService = prescriptionService;
        this.medicineService = medicineService;
    }

    @GetMapping("/form")
    public String getPrescriptionForm(@RequestParam String patientId, Model model, HttpServletRequest request) throws ExecutionException, InterruptedException {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails= (MyUserDetails) auth.getPrincipal();
        Doctor doctor = doctorService.getDoctor(myUserDetails.getUsername());

        // Retrieve the list of prescribed medicines from the session or any temporary storage
        // List<Medicine> prescribedMedicines = (List<Medicine>) request.getSession().getAttribute("prescribedMedicines");

        List<Medicine> medicineList = medicineService.getListMedicine(); //getListprecribemedicine

        model.addAttribute("patientName", patientService.getPatient(patientId).getName());
        model.addAttribute("patientId", patientId);
        model.addAttribute("doctor",doctor);
        
        // model.addAttribute("prescribeList", prescribedMedicines);
        model.addAttribute("medicineList", medicineList);

        System.out.println("patient id before"+patientId);
        return "sendPrescriptionForm";
    }
    
    @GetMapping("/add-prescription") //got to medicine list to be assign utk doctor
    public String addMedication(@RequestParam String patientId, Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails= (MyUserDetails) auth.getPrincipal();
        Doctor doctor = doctorService.getDoctor(myUserDetails.getUsername());

        List<Medicine> medicineList = medicineService.getListMedicine(); //new method for patientMedicine
    
        model.addAttribute("patientName", patientService.getPatient(patientId).getName());
        model.addAttribute("patientId", patientId);
        model.addAttribute("doctor",doctor);
        model.addAttribute("medicineList", medicineList);
        return "patientMedicine";
    }

    @PostMapping("/prescribemedicine/submit")  //business logic to create new object of prescribe medicine based on what doctor has select med name and input the prescribe quantity
    public String submitMedicineForm(Model model,
                                 @RequestParam String patientId,
                                 @RequestParam Map<String, String> allParams,
                                 RedirectAttributes redirectAttributes) 
                                 throws ExecutionException, InterruptedException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails= (MyUserDetails) auth.getPrincipal();
        Doctor doctor = doctorService.getDoctor(myUserDetails.getUsername());

        // Extract selected medicines from form medicine list
        Map<String, Integer> selectedMedicinesWithQuantities = allParams.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("quantity_") && !entry.getValue().isEmpty())
                .collect(Collectors.toMap(
                        entry -> entry.getKey().substring("quantity_".length()),
                        entry -> Integer.parseInt(entry.getValue())
                ));

        //get selected medicine id with prescribe quantity
        List<String> selectedMedicineIds = allParams.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("selectedMedicines") && entry.getValue().equals("on"))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Map<String, Integer> prescribedMedicines = selectedMedicinesWithQuantities.entrySet().stream()
                .filter(entry -> selectedMedicineIds.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Call the service to prescribe the medicines
        // Replace "Prescription Description" and "Diagnosis Ailment Description" with actual values or parameters as needed
        prescriptionService.prescribeMedicines(patientId, prescribedMedicines,
                                               "Prescription Description", 
                                               "Diagnosis Ailment Description", 
                                               medicineService);

        System.out.println("patient id before: "+patientId);

        List<Medicine> medicineList = medicineService.getListMedicine();
        
        model.addAttribute("patientName", patientService.getPatient(patientId).getName());
        model.addAttribute("patientId", patientId);
        model.addAttribute("doctor",doctor);
        model.addAttribute("medicineList", medicineList);

        redirectAttributes.addAttribute("patientId", patientId); // Ensure the patientId is included in the redirect
        return "redirect:/prescription/form";
    }

    @PostMapping("/form/submit")
    public String submitPrescriptionForm(Model model,
                                         @RequestParam(value = "patientId") String patientId,
                                         @RequestParam(value = "doctorId") String doctorId,
                                         @RequestParam(value = "prescription") String prescription,
                                         @RequestParam(value = "diagnosisAilment") String diagnosisAilment,
                                         @RequestParam(value = "patientMedList") List<String> patientMedList)
            throws ExecutionException, InterruptedException {

        System.out.println("patient id before"+patientId);
        
        Prescription prescription1 = new Prescription(doctorId,
                patientMedList,
                prescription,
                diagnosisAilment);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails= (MyUserDetails) auth.getPrincipal();
        Doctor doctor = doctorService.getDoctor(myUserDetails.getUsername());
        List<Patient> patientList = doctorService.findAllPatientAssignToDoctor(doctor.getUserId());
        model.addAttribute("patientList", patientList);
        model.addAttribute("doctor",doctor);
        String timeCreated = prescriptionService.createPrescription(prescription1,patientId);
        System.out.println("patient id after"+patientId);
        return "myPatient";
    }

    @GetMapping("/history")
        public String viewPrescriptionHistory(@RequestParam String patientId, Model model, @RequestParam(defaultValue = "0") int pageNo, 
        @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "") String searchQuery) throws ExecutionException, InterruptedException {
            
            List<Prescription> allPrescription = prescriptionService.getListPrescription(patientId);

            if (!searchQuery.isEmpty()) {
            allPrescription = allPrescription.stream()
                                    .filter(p -> p.getPrescriptionDescription().toLowerCase().contains(searchQuery.toLowerCase()) 
                                            || p.getDiagnosisAilmentDescription().toString().contains(searchQuery))
                                    .collect(Collectors.toList());
            }
        
            int total = allPrescription.size();
            int start = Math.min(pageNo * pageSize, total);
            int end = Math.min((pageNo + 1) * pageSize, total);
            int startIndex = pageNo * pageSize;if (!searchQuery.isEmpty());
            
            List<Prescription> prescriptionHistory = allPrescription.subList(start, end);
            model.addAttribute("startIndex", startIndex);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
            model.addAttribute("searchQuery", searchQuery);
            model.addAttribute("prescriptionHistory", prescriptionHistory);

        return "prescriptionHistory";
    }
}

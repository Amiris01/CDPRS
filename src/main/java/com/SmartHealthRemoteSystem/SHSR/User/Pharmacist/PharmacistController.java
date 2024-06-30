package com.SmartHealthRemoteSystem.SHSR.User.Pharmacist;

import com.SmartHealthRemoteSystem.SHSR.Medicine.Medicine;
import com.SmartHealthRemoteSystem.SHSR.Pagination.PaginationInfo;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.MedicineService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Service.PharmacistService;
import com.SmartHealthRemoteSystem.SHSR.Service.SensorDataService;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.Query.Direction;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.print.Doc;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RequestMapping("/pharmacist")
@Controller

public class PharmacistController {
    
    private final PharmacistService pharmacistService;
    private final MedicineService medicineService;

    @Autowired
    public PharmacistController(PharmacistService pharmacistService, MedicineService medicineService) {
        this.pharmacistService = pharmacistService;
        this.medicineService = medicineService;
    }

    @GetMapping
    public String pharmacistDashboard(Model model,  @RequestParam(defaultValue = "0") int pageNo, 
    @RequestParam(defaultValue = "5") int pageSize, @RequestParam(defaultValue = "") String searchQuery) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Pharmacist pharmacist = pharmacistService.getPharmacist(myUserDetails.getUsername());
        
        List<Medicine> allMedicine = pharmacistService.getListMedicine();

        if (!searchQuery.isEmpty()) {
            allMedicine = allMedicine.stream()
                             .filter(p -> p.getMedName().toLowerCase().contains(searchQuery.toLowerCase()) 
                                       || p.getMedId().toString().contains(searchQuery))
                             .collect(Collectors.toList());
        }

        int total = allMedicine.size();
        int start = Math.min(pageNo * pageSize, total);
        int end = Math.min((pageNo + 1) * pageSize, total);
        int startIndex = pageNo * pageSize;

        List<Medicine> medicineStock = allMedicine.subList(start, end);

        // List<Medicine> medicineStock = pharmacistService.getListMedicine();

        model.addAttribute("startIndex", startIndex);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
        model.addAttribute("pharmacist", pharmacist);
        model.addAttribute("medicineStock", medicineStock);
        model.addAttribute("searchQuery", searchQuery);
        
        return "PharmacistDashboard";
    }

    @GetMapping("/updateProfile")
    public String updateProfile(Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Pharmacist pharmacist = pharmacistService.getPharmacist(myUserDetails.getUsername());
        model.addAttribute("pharmacist", pharmacist);
        return "PharmacistProfile";
    }

    @PostMapping("/updateProfile/profile")
    public String submitProfile(@ModelAttribute Pharmacist pharmacist, @RequestParam("profilePicture") MultipartFile profilePicture) throws ExecutionException, InterruptedException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        pharmacist.setUserId(myUserDetails.getUsername());
    
        if (!profilePicture.isEmpty()) {
            byte[] profilePictureBytes = profilePicture.getBytes();
            pharmacist.setProfilePicture(profilePictureBytes); // Store image as byte array in the entity
        }
    
        pharmacistService.updatePharmacist(pharmacist);
        return "redirect:/pharmacist/updateProfile";
    }  

    @GetMapping("/pharmacist/profilePicture/{userId}")
    @ResponseBody
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String userId) throws ExecutionException, InterruptedException {
        // Retrieve the profile picture data from the database based on the user ID
        byte[] profilePictureData = pharmacistService.getProfilePictureData(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // Adjust content type based on your image format

        return new ResponseEntity<>(profilePictureData, headers, HttpStatus.OK);
    }

    @GetMapping("/viewMedicineList")
    public String viewMedicineList(Model model, @RequestParam(defaultValue = "0") int pageNo, 
    @RequestParam(defaultValue = "5") int pageSize, @RequestParam(defaultValue = "") String searchQuery) {
        try {
            List<Medicine> allMedicine = pharmacistService.getListMedicine();

            if (!searchQuery.isEmpty()) {
                allMedicine = allMedicine.stream()
                                 .filter(p -> p.getMedName().toLowerCase().contains(searchQuery.toLowerCase()) 
                                           || p.getMedId().toString().contains(searchQuery))
                                 .collect(Collectors.toList());
            }

            int total = allMedicine.size();
            int start = Math.min(pageNo * pageSize, total);
            int end = Math.min((pageNo + 1) * pageSize, total);
            int startIndex = pageNo * pageSize;
            // List<Medicine> medicineList = pharmacistService.getListMedicine();
            
            List<Medicine> medicineList = allMedicine.subList(start, end);


            model.addAttribute("startIndex", startIndex);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
            model.addAttribute("medicineList", medicineList);
            model.addAttribute("searchQuery", searchQuery);

    
            // Format the current timestamp and add it to the model
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM dd yyyy HH:mm:ss");
            String formattedTimestamp = now.format(formatter);
            model.addAttribute("lastUpdated", formattedTimestamp);
    
            return "viewMedicineList";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error retrieving medicines.");
            return "viewMedicineList";
        }
    }

    @GetMapping("/editMedicine/{medId}")
    public String editMedicine(@PathVariable String medId, Model model) {
        try {
            Medicine medicine = pharmacistService.getMedicine(medId);
            model.addAttribute("medicine", medicine);
            return "editMedicine";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @PostMapping("/updateMedicine/{medId}")
    public String updateMedicine(@PathVariable String medId, @ModelAttribute Medicine updatedMedicine, RedirectAttributes redirectAttributes) {
        try {
            updatedMedicine.setMedId(medId);
            String updateResult = pharmacistService.updateMedicine(updatedMedicine);
            LocalDateTime now = LocalDateTime.now();
    
            // Format the timestamp
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM dd yyyy HH:mm:ss");
            String formattedTimestamp = now.format(formatter);
    
            if (updateResult.equals("Medicine updated successfully")) {
                String successMessage = "Medicine updated successfully at " + formattedTimestamp;
                redirectAttributes.addFlashAttribute("successMessage", successMessage);
                return "redirect:/pharmacist/viewMedicineList";
            } else {
                String errorMessage = "Error updating medicine: " + updateResult;
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/pharmacist/editMedicine/" + medId;
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating medicine.");
            return "redirect:/pharmacist/editMedicine/" + medId;
        }
    }

    @PostMapping("/deleteMedicine/{medId}")
    public String deleteSelectedMedicine(@PathVariable("medId") String medId, RedirectAttributes redirectAttributes) {
        try {
            String deleteMessage = medicineService.deleteMedicine(medId);
    
            // Format the current timestamp
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM dd yyyy HH:mm:ss");
            String formattedTimestamp = now.format(formatter);
            redirectAttributes.addFlashAttribute("lastUpdated", formattedTimestamp);
    
            if (deleteMessage.startsWith("Error")) {
                redirectAttributes.addFlashAttribute("errorMessage", deleteMessage);
            } else {
                redirectAttributes.addFlashAttribute("successMessage", deleteMessage);
            }
    
            return "redirect:/pharmacist/viewMedicineList";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting medicine.");
            return "redirect:/pharmacist/viewMedicineList";
        }
    }

    @PostMapping("/addStock")
    public String addStock(@RequestParam String medId, @RequestParam int quantity, RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        try {
            Medicine medicine = medicineService.getMedicine(medId);
            if (medicine != null) {
                medicine.setQuantity(medicine.getQuantity() + quantity);
                medicineService.updateMedicine(medicine);
    
                // Format the current timestamp
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM dd yyyy HH:mm:ss");
                String formattedTimestamp = now.format(formatter);
                redirectAttributes.addFlashAttribute("lastUpdated", formattedTimestamp);
    
                String successMessage = "Stock added successfully at " + formattedTimestamp;
                redirectAttributes.addFlashAttribute("successMessage", successMessage);
            }
            return "redirect:/pharmacist";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding stock.");
            return "redirect:/pharmacist";
        }
    }

    @GetMapping("/addMedicineForm")
    public String showAddMedicineForm(Model model) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM dd yyyy HH:mm:ss");
        String formattedTimestamp = now.format(formatter);

        model.addAttribute("lastUpdated", formattedTimestamp);
        model.addAttribute("newMedicine", new Medicine());
        return "addMedicineForm";
    }

    @PostMapping("/addMedicine")
    public String addMedicine(@ModelAttribute("newMedicine") Medicine newMedicine, RedirectAttributes redirectAttributes) {
        try {
            // Check if medicine with the same name already exists
            List<Medicine> existingMedicines = medicineService.getListMedicine();
    
            for (Medicine existingMedicine : existingMedicines) {
                if (Objects.equals(existingMedicine.getMedName(), newMedicine.getMedName())) {
                    String errorMessage = "Error adding medicine. Medicine with name '" + newMedicine.getMedName() + "' already exists.";
                    redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                    return "redirect:/pharmacist/viewMedicineList";
                }
            }
    
            // Call the service method to add the new medicine
            medicineService.createMedicine(newMedicine);
    
            // Format the current timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd yyyy HH:mm:ss");
            String formattedTimestamp = dateFormat.format(new Date());
    
            // Set timestamp as message too
            String message = "Medicine added successfully at " + formattedTimestamp;
    
            // Add the new medicine to the end of the list
            existingMedicines.add(newMedicine);
    
            redirectAttributes.addFlashAttribute("successMessage", message);
            redirectAttributes.addFlashAttribute("lastUpdated", formattedTimestamp);
    
            return "redirect:/pharmacist/viewMedicineList";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding medicine.");
            return "redirect:/pharmacist/viewMedicineList";
        }
    }
}
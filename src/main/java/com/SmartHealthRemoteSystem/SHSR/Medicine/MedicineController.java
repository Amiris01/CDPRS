package com.SmartHealthRemoteSystem.SHSR.Medicine;

import com.SmartHealthRemoteSystem.SHSR.Service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class MedicineController {

    private final MedicineService medicineService;

    @Autowired
    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping("/edit/{medId}")
    public String editMedicine(@PathVariable String medId, Model model) throws ExecutionException, InterruptedException {
        Medicine medicine = medicineService.getMedicine(medId);
        model.addAttribute("medicine", medicine);
        return "editMedicine"; 
    }

    @PostMapping("/edit/{medId}")
    public String updateMedicine(@PathVariable String medId, @ModelAttribute Medicine updatedMedicine) throws ExecutionException, InterruptedException {
        updatedMedicine.setMedId(medId);
        medicineService.updateMedicine(updatedMedicine);
        return "redirect:/pharmacist/medicines";
    }

    @GetMapping("/delete/{medId}")
    public String deleteMedicine(@PathVariable String medId) throws ExecutionException, InterruptedException {
        medicineService.deleteMedicine(medId);
        return "redirect:/pharmacist/medicines";
    }

    @PostMapping("/add")
    public String addNewMedicine(@ModelAttribute Medicine newMedicine) throws ExecutionException, InterruptedException {
        medicineService.createMedicine(newMedicine);
        return "redirect:/pharmacist/viewMedicineList";
    }
}

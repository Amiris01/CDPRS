package com.SmartHealthRemoteSystem.SHSR.Symptoms;
import com.SmartHealthRemoteSystem.SHSR.Service.SymptomsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class SymptomsController {

    @Autowired
    private SymptomsService symptomsService;

    @GetMapping("/symptoms")
    public String getSymptomsPage(Model model) throws ExecutionException, InterruptedException {
        List<Symptoms> symptomsList = symptomsService.getAllSymptoms();
        model.addAttribute("symptomsList", symptomsList);
        return "symptoms"; 
    }
}

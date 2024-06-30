package com.SmartHealthRemoteSystem.SHSR.Prediction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorPredictionService;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Service.PredictionService;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class PredictionController {

    @Autowired
    private PredictionService predictionService;
    private PredictionRestController predictionRestController;
    private PatientService patientService;
    private DoctorService doctorService;
    private DoctorPredictionService doctorPredictionService;

    public PredictionController(PredictionService predictionService, PredictionRestController predictionRestController, PatientService patientService, DoctorService doctorService, DoctorPredictionService doctorPredictionService){
        this.predictionService = predictionService;
        this.predictionRestController = predictionRestController;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.doctorPredictionService = doctorPredictionService;
    }

    @PostMapping("/DiagnosisResult")
    public String makePrediction(@RequestParam("symptom[]") List<String> symptoms, @RequestParam(value = "patientId") String patientId, Model model) throws ExecutionException, InterruptedException {
        ResponseEntity<String> response = predictionRestController.callDjangoAPI(symptoms);
        String predictionResult = response.getBody();
        JsonObject jsonObject = new Gson().fromJson(predictionResult, JsonObject.class);
        JsonArray topDiseasesArray = jsonObject.getAsJsonArray("top_diseases");
    
        List<String> diseases = new ArrayList<>();
        List<Float> probabilities = new ArrayList<>();
    
        for (int i = 0; i < topDiseasesArray.size(); i++) {
            String diseaseWithProbability = topDiseasesArray.get(i).getAsString();
            String[] parts = diseaseWithProbability.split(": ");
            String disease = parts[0];
            float probability = Float.parseFloat(parts[1].replace("%", "")); 
            diseases.add(disease);
            probabilities.add(probability);
        }

        Prediction prediction1 = new Prediction();
        prediction1.setDiagnosisList(diseases);
        prediction1.setProbabilityList(probabilities);
        prediction1.setSymptomsList(symptoms);
        String timeCreated = predictionService.createPrediction(prediction1, patientId);
        //String timeCreated = "2024-05-29T15:12:50.911377000Z";

        Patient patient=patientService.getPatient(patientId);
        Doctor doctor=doctorService.getDoctor(patient.getAssigned_doctor());

        List<String> formattedSymptoms = new ArrayList<>();
        for (String symptom : symptoms) {
            String formattedSymptom = formatSymptom(symptom);
            formattedSymptoms.add(formattedSymptom);
        }

        model.addAttribute("diseases", diseases);
        model.addAttribute("probabilities", probabilities);
        model.addAttribute("symptoms", formattedSymptoms);
        model.addAttribute("timeCreated", timeCreated);
        model.addAttribute(patient);
        model.addAttribute(doctor);
        
        
        return "DiagnosisResult";
    }

    private String formatSymptom(String symptom) {
        String[] words = symptom.split("_");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            formatted.append(Character.toUpperCase(word.charAt(0)))
                     .append(word.substring(1).toLowerCase())
                     .append(" ");
        }
        return formatted.toString().trim();
    }

    @PostMapping("/DoctorDiagnosisResult")
    public String makeDoctorPrediction(@RequestParam("symptom[]") List<String> symptoms, @RequestParam(value = "doctorId") String doctorId, Model model) throws ExecutionException, InterruptedException {
        ResponseEntity<String> response = predictionRestController.callDjangoAPI(symptoms);
        String predictionResult = response.getBody();
        JsonObject jsonObject = new Gson().fromJson(predictionResult, JsonObject.class);
        JsonArray topDiseasesArray = jsonObject.getAsJsonArray("top_diseases");
    
        List<String> diseases = new ArrayList<>();
        List<Float> probabilities = new ArrayList<>();
    
        for (int i = 0; i < topDiseasesArray.size(); i++) {
            String diseaseWithProbability = topDiseasesArray.get(i).getAsString();
            String[] parts = diseaseWithProbability.split(": ");
            String disease = parts[0];
            float probability = Float.parseFloat(parts[1].replace("%", "")); 
            diseases.add(disease);
            probabilities.add(probability);
        }

        DoctorPrediction prediction1 = new DoctorPrediction();
        prediction1.setDiagnosisList(diseases);
        prediction1.setProbabilityList(probabilities);
        prediction1.setSymptomsList(symptoms);
        String timeCreated = doctorPredictionService.createDoctorPrediction(prediction1, doctorId);
        //String timeCreated = "2024-05-29T15:12:50.911377000Z";

        Doctor doctor=doctorService.getDoctor(doctorId);

        List<String> formattedSymptoms = new ArrayList<>();
        for (String symptom : symptoms) {
            String formattedSymptom = formatSymptom(symptom);
            formattedSymptoms.add(formattedSymptom);
        }

        model.addAttribute("diseases", diseases);
        model.addAttribute("probabilities", probabilities);
        model.addAttribute("symptoms", formattedSymptoms);
        model.addAttribute("timeCreated", timeCreated);
        model.addAttribute(doctor);
        
        
        return "DoctorDiagnosisResult";
    }

    @GetMapping("/predictionHistory")
    public String showDiagnosisPage(@RequestParam("doctorId") String doctorId, Model model) throws ExecutionException, InterruptedException {

        Doctor doctor = doctorService.getDoctor(doctorId);
        model.addAttribute("doctor", doctor);

        List<DoctorPrediction> predictionList = doctorPredictionService.getListDoctorPrediction(doctorId);
        model.addAttribute("predictionList", predictionList);

        return "DoctorPredictionHistory";
    }
}

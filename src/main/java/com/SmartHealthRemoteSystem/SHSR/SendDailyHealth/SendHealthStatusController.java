package com.SmartHealthRemoteSystem.SHSR.SendDailyHealth;

import com.SmartHealthRemoteSystem.SHSR.Prediction.Prediction;
import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.SensorData;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.HealthStatusService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Service.PredictionService;
import com.SmartHealthRemoteSystem.SHSR.Service.SensorDataService;
import com.SmartHealthRemoteSystem.SHSR.Service.SymptomsService;
import com.SmartHealthRemoteSystem.SHSR.Symptoms.Symptoms;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.Query.Direction;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Optional;
import java.util.Arrays;

@Controller
@RequestMapping("/Health-status")
public class SendHealthStatusController {
    
    private final HealthStatusService healthStatusService;
    private final PatientService patientService;
    private final SensorDataService sensorDataService;
    private final DoctorService doctorService;
    private final PredictionService predictionService;
    
    public SendHealthStatusController(HealthStatusService healthStatusService, PatientService patientService, SensorDataService sensorDataService, DoctorService doctorService, PredictionService predictionService) {
        this.healthStatusService = healthStatusService;
        this.patientService=patientService;
        this.sensorDataService=sensorDataService;
        this.doctorService = doctorService;
        this.predictionService = predictionService;
    }

    @PostMapping("/sendHealthStatus")
    public String sendHealthStatus(@RequestParam(value = "symptom") String symptom,
                                   @RequestParam(value="patientId") String patientId,
                                   @RequestParam (value = "doctorId")String doctorId,
                                   Model model) throws ExecutionException, InterruptedException {

        String sensorId=patientService.getPatientSensorId(patientId);
        //symptom+="\n"+ sensorDataService.stringSensorData(sensorId);
        HealthStatus healthStatus=new HealthStatus(symptom,doctorId);
        healthStatusService.createHealthStatus(healthStatus,patientId);

        Patient patient=patientService.getPatient(patientId);
        Doctor doctor=doctorService.getDoctor(patient.getAssigned_doctor());
        model.addAttribute(patient);
        model.addAttribute(doctor);
        return "patientDashBoard";
    }

    @PostMapping("/viewHealthStatusForm")
    public String healthStatusForm(@RequestParam(value = "patientId") String patientId, Model model) throws ExecutionException, InterruptedException {
        Patient patient = patientService.getPatient(patientId);
        Doctor doctor = doctorService.getDoctor(patient.getAssigned_doctor());
        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);
    
        Optional<Prediction> predictions = predictionService.getRecentPrediction(patientId);
        model.addAttribute("predictions", predictions.orElse(null));
    
        List<String> formattedSymptoms = new ArrayList<>();
        List<String> sensorBasedSymptoms = new ArrayList<>(); // Track symptoms added based on sensor data
    
        if (predictions.isPresent()) {
            Prediction prediction = predictions.get();
            List<String> symptoms = prediction.getSymptomsList();
            formattedSymptoms = formatSymptoms(symptoms);
        }
    
        SensorData sensorData = sensorDataService.getSensorData(patient.getSensorDataId());
        model.addAttribute("sensorData", sensorData);
    
        if (sensorData.getBodyTemperature() > 37.5) {
            List<String> symptoms = Arrays.asList("high_fever", "chills", "sweating", "shivering");
            formattedSymptoms.addAll(formatSymptoms(symptoms));
            sensorBasedSymptoms.addAll(formatSymptoms(symptoms));
        }
        if (sensorData.getBodyTemperature() < 35) {
            List<String> symptoms = Arrays.asList("shivering");
            formattedSymptoms.addAll(formatSymptoms(symptoms));
            sensorBasedSymptoms.addAll(formatSymptoms(symptoms));
        }
        if (sensorData.getOxygenReading() < 90) {
            List<String> symptoms = Arrays.asList("breathlessness", "dizziness", "fatigue");
            formattedSymptoms.addAll(formatSymptoms(symptoms));
            sensorBasedSymptoms.addAll(formatSymptoms(symptoms));
        }
        if (sensorData.getHeart_Rate() > 100) {
            List<String> symptoms = Arrays.asList("high_heart_rate", "chest_pain", "breathlessness", "sweating");
            formattedSymptoms.addAll(formatSymptoms(symptoms));
            sensorBasedSymptoms.addAll(formatSymptoms(symptoms));
        }
        if (sensorData.getHeart_Rate() < 60) {
            List<String> symptoms = Arrays.asList("dizziness", "fatigue");
            formattedSymptoms.addAll(formatSymptoms(symptoms));
            sensorBasedSymptoms.addAll(formatSymptoms(symptoms));
        }
    
        // Add formatted symptoms and sensor-based symptoms to the model
        model.addAttribute("formattedSymptoms", formattedSymptoms);
        model.addAttribute("sensorBasedSymptoms", sensorBasedSymptoms);
        System.out.println("Formatted Symptoms: " + formattedSymptoms);
    
        return "sendDailyHealthSymptom";
    }
    
    
    @GetMapping("/Diagnosis")
    public String showDiagnosisPage(@RequestParam("patientId") String patientId, @RequestParam("doctorId") String doctorId, Model model) throws ExecutionException, InterruptedException {

        Patient patient = patientService.getPatient(patientId);
        Doctor doctor = doctorService.getDoctor(patient.getAssigned_doctor());

        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);

        List<Prediction> predictionList = predictionService.getListPrediction(patientId);
        model.addAttribute("predictionList", predictionList);

        return "Diagnosis";
    }

    private List<String> formatSymptoms(List<String> symptoms) {
        List<String> formattedSymptoms = new ArrayList<>();
        for (String symptom : symptoms) {
            // Format each symptom by replacing underscores with spaces and capitalizing each word
            String formattedSymptom = capitalizeWords(symptom.replace("_", " "));
            formattedSymptoms.add(formattedSymptom);
        }
        return formattedSymptoms;
    }
    
    private String capitalizeWords(String str) {
        // Capitalize each word in the string
        StringBuilder result = new StringBuilder(str.length());
        boolean capitalize = true;
        for (char ch : str.toCharArray()) {
            if (Character.isWhitespace(ch)) {
                capitalize = true;
            } else if (capitalize) {
                ch = Character.toTitleCase(ch);
                capitalize = false;
            }
            result.append(ch);
        }
        return result.toString();
    }

}





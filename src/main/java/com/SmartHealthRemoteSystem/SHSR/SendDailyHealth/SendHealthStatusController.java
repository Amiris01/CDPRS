package com.SmartHealthRemoteSystem.SHSR.SendDailyHealth;

import com.SmartHealthRemoteSystem.SHSR.Prediction.Prediction;
import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.SensorData;
import com.SmartHealthRemoteSystem.SHSR.Service.DiagnosisService;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.HealthStatusService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Service.PredictionService;
import com.SmartHealthRemoteSystem.SHSR.Service.SensorDataService;
import com.SmartHealthRemoteSystem.SHSR.Service.SymptomsService;
import com.SmartHealthRemoteSystem.SHSR.Symptoms.Symptoms;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis.Diagnosis;
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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Optional;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.time.ZonedDateTime;
import java.util.Set;

@Controller
@RequestMapping("/Health-status")
public class SendHealthStatusController {
    
    private final HealthStatusService healthStatusService;
    private final PatientService patientService;
    private final SensorDataService sensorDataService;
    private final DoctorService doctorService;
    private final PredictionService predictionService;
    private final DiagnosisService diagnosisService;
    
    public SendHealthStatusController(HealthStatusService healthStatusService, PatientService patientService, SensorDataService sensorDataService, DoctorService doctorService, PredictionService predictionService, DiagnosisService diagnosisService) {
        this.healthStatusService = healthStatusService;
        this.patientService=patientService;
        this.sensorDataService=sensorDataService;
        this.doctorService = doctorService;
        this.predictionService = predictionService;
        this.diagnosisService = diagnosisService;
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

        Set<String> formattedSymptomsSet = new LinkedHashSet<>();
        Set<String> sensorBasedSymptomsSet = new LinkedHashSet<>(); // Track symptoms added based on sensor data

        boolean hasSubmittedToday = false;

        if (predictions.isPresent()) {
            Prediction prediction = predictions.get();
            List<String> symptoms = prediction.getSymptomsList();
            formattedSymptomsSet.addAll(symptoms);
            String timestamp = prediction.getTimestamp();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
            ZonedDateTime predictionDateTime = ZonedDateTime.parse(timestamp, formatter);
            ZonedDateTime predictionDateTimeInLocalZone = predictionDateTime.withZoneSameInstant(ZoneId.systemDefault());
            LocalDate predictionDate = predictionDateTimeInLocalZone.toLocalDate();
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            hasSubmittedToday = predictionDate.equals(today);
        }

        SensorData sensorData = sensorDataService.getSensorData(patient.getSensorDataId());
        model.addAttribute("sensorData", sensorData);

        if (sensorData.getBodyTemperature() > 38.5) {
            List<String> symptoms = Arrays.asList("high_fever", "chills", "sweating", "shivering", "unsteadiness", "dizziness", "lethargy");
            sensorBasedSymptomsSet.addAll(symptoms);
        }
        if (sensorData.getBodyTemperature() < 35) {
            List<String> symptoms = Arrays.asList("shivering", "restlessness", "breathlessness");
            sensorBasedSymptomsSet.addAll(symptoms);
        }
        if (sensorData.getOxygenReading() < 90) {
            List<String> symptoms = Arrays.asList("breathlessness", "dizziness", "fatigue", "cough", "headache");
            sensorBasedSymptomsSet.addAll(symptoms);
        }
        if (sensorData.getHeart_Rate() > 100) {
            List<String> symptoms = Arrays.asList("high_heart_rate", "chest_pain", "breathlessness", "fatigue");
            sensorBasedSymptomsSet.addAll(symptoms);
        }
        if (sensorData.getHeart_Rate() < 60) {
            List<String> symptoms = Arrays.asList("dizziness", "fatigue", "muscle_weakness", "blurred_and_distorted_vision", "breathlessness", "cough");
            sensorBasedSymptomsSet.addAll(symptoms);
        }

        // Combine sensor-based symptoms first, followed by other symptoms, avoiding duplicates
        Set<String> combinedSymptomsSet = new LinkedHashSet<>();
        combinedSymptomsSet.addAll(sensorBasedSymptomsSet);
        combinedSymptomsSet.addAll(formattedSymptomsSet);

        // Convert set to list
        List<String> combinedSymptoms = new ArrayList<>(combinedSymptomsSet);

        // Add combined symptoms and sensor-based symptoms to the model
        model.addAttribute("hasSubmittedToday", hasSubmittedToday);
        model.addAttribute("formattedSymptoms", combinedSymptoms);
        model.addAttribute("sensorBasedSymptoms", new ArrayList<>(sensorBasedSymptomsSet));
        System.out.println("Combined Symptoms: " + combinedSymptoms);

        return "sendDailyHealthSymptom";
    }
        
    @GetMapping("/Diagnosis")
    public String showDiagnosisPage(@RequestParam("patientId") String patientId, 
                                    @RequestParam("doctorId") String doctorId, 
                                    Model model,
                                    @RequestParam(defaultValue = "0") int pageNo, 
                                    @RequestParam(defaultValue = "5") int pageSize, 
                                    @RequestParam(defaultValue = "") String startDate,
                                    @RequestParam(defaultValue = "") String endDate) throws ExecutionException, InterruptedException {
    
        Patient patient = patientService.getPatient(patientId);
        Doctor doctor = doctorService.getDoctor(patient.getAssigned_doctor());
    
        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);
    
        List<Prediction> allpredictionList = predictionService.getListPrediction(patientId);
    
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    
        // Sort the predictions based on timestamp (latest to oldest)
        allpredictionList.sort(Comparator.comparing((Prediction p) -> LocalDateTime.parse(p.getTimestamp(), formatter)).reversed());
    
        // Filter the predictions based on the date range
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            allpredictionList = allpredictionList.stream()
                    .filter(p -> {
                        LocalDateTime timestamp = LocalDateTime.parse(p.getTimestamp(), formatter);
                        return (timestamp.isEqual(start) || timestamp.isAfter(start)) && (timestamp.isEqual(end) || timestamp.isBefore(end));
                    })
                    .collect(Collectors.toList());
        }
    
        int total = allpredictionList.size();
        int startIdx = Math.min(pageNo * pageSize, total);
        int endIdx = Math.min((pageNo + 1) * pageSize, total);
        int startIndex = pageNo * pageSize;
    
        List<Prediction> predictionList = allpredictionList.subList(startIdx, endIdx);
    
        model.addAttribute("startIndex", startIndex);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
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

    @GetMapping("/predictionHistory")
    public String getPredictionHistory(@RequestParam("patientId") String patientId, 
                                    @RequestParam("doctorId") String doctorId, 
                                    Model model,
                                    @RequestParam(defaultValue = "0") int pageNo, 
                                    @RequestParam(defaultValue = "5") int pageSize, 
                                    @RequestParam(defaultValue = "") String startDate,
                                    @RequestParam(defaultValue = "") String endDate) throws ExecutionException, InterruptedException {
    
        Patient patient = patientService.getPatient(patientId);
        Doctor doctor = doctorService.getDoctor(patient.getAssigned_doctor());
    
        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);
    
        List<Prediction> allpredictionList = predictionService.getListPrediction(patientId);
        List<Diagnosis> allDiagnosisList = diagnosisService.getListDiagnosis(patientId);

        model.addAttribute("allDiagnosisList", allDiagnosisList);
    
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    
        // Sort the predictions based on timestamp (latest to oldest)
        allpredictionList.sort(Comparator.comparing((Prediction p) -> LocalDateTime.parse(p.getTimestamp(), formatter)).reversed());
    
        // Filter the predictions based on the date range
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            allpredictionList = allpredictionList.stream()
                    .filter(p -> {
                        LocalDateTime timestamp = LocalDateTime.parse(p.getTimestamp(), formatter);
                        return (timestamp.isEqual(start) || timestamp.isAfter(start)) && (timestamp.isEqual(end) || timestamp.isBefore(end));
                    })
                    .collect(Collectors.toList());
        }
    
        int total = allpredictionList.size();
        int startIdx = Math.min(pageNo * pageSize, total);
        int endIdx = Math.min((pageNo + 1) * pageSize, total);
        int startIndex = pageNo * pageSize;
    
        List<Prediction> predictionList = allpredictionList.subList(startIdx, endIdx);
        List<String> allDiagnosis = allpredictionList.stream().flatMap(prediction -> prediction.getDiagnosisList().stream()).distinct().collect(Collectors.toList());
    
        model.addAttribute("startIndex", startIndex);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("predictionList", predictionList);
        model.addAttribute("allDiagnosis", allDiagnosis);
    
        return "predictionHistory";
    }

    @GetMapping("/manageDiagnosis")
    public String getManageDiagnosis(
        @RequestParam("patientId") String patientId,
        Model model,
        @RequestParam(defaultValue= "0") int pageNo,
        @RequestParam(defaultValue = "5") int pageSize,
        @RequestParam(defaultValue = "") String startDate,
        @RequestParam(defaultValue = "") String endDate)throws ExecutionException, InterruptedException{
            Patient patient = patientService.getPatient(patientId);
            Doctor doctor = doctorService.getDoctor(patient.getAssigned_doctor());
    
            model.addAttribute("patient", patient);
            model.addAttribute("doctor", doctor);

            List<Diagnosis> allDiagnosisList = diagnosisService.getListDiagnosis(patientId);
            List<Prediction> allpredictionList = predictionService.getListPrediction(patientId);
            List<String> allDiagnosis = allpredictionList.stream().flatMap(prediction -> prediction.getDiagnosisList().stream()).distinct().collect(Collectors.toList());
            model.addAttribute("allDiagnosis", allDiagnosis);

            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            allDiagnosisList.sort(Comparator.comparing((Diagnosis p) -> LocalDateTime.parse(p.getTimestamp(), formatter)).reversed());

        // Filter the diagnosis based on the date range
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            allDiagnosisList = allDiagnosisList.stream()
                    .filter(p -> {
                        LocalDateTime timestamp = LocalDateTime.parse(p.getTimestamp(), formatter);
                        return (timestamp.isEqual(start) || timestamp.isAfter(start)) && (timestamp.isEqual(end) || timestamp.isBefore(end));
                    })
                    .collect(Collectors.toList());
        }

        int total = allDiagnosisList.size();
        int startIdx = Math.min(pageNo * pageSize, total);
        int endIdx = Math.min((pageNo + 1) * pageSize, total);
        int startIndex = pageNo * pageSize;

        List<Diagnosis> diagnosisList = allDiagnosisList.subList(startIdx, endIdx);

        model.addAttribute("startIndex", startIndex);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("diagnosisList", diagnosisList);

        return "manageDiagnosis";
    }

    @GetMapping("/viewDiagnosis")
    public String getViewDiagnosis(
        @RequestParam("patientId") String patientId,
        Model model,
        @RequestParam(defaultValue= "0") int pageNo,
        @RequestParam(defaultValue = "5") int pageSize,
        @RequestParam(defaultValue = "") String startDate,
        @RequestParam(defaultValue = "") String endDate)throws ExecutionException, InterruptedException{
            Patient patient = patientService.getPatient(patientId);
            Doctor doctor = doctorService.getDoctor(patient.getAssigned_doctor());
    
            model.addAttribute("patient", patient);
            model.addAttribute("doctor", doctor);

            List<Diagnosis> allDiagnosisList = diagnosisService.getListDiagnosis(patientId);

            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            allDiagnosisList.sort(Comparator.comparing((Diagnosis p) -> LocalDateTime.parse(p.getTimestamp(), formatter)).reversed());

        // Filter the diagnosis based on the date range
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            allDiagnosisList = allDiagnosisList.stream()
                    .filter(p -> {
                        LocalDateTime timestamp = LocalDateTime.parse(p.getTimestamp(), formatter);
                        return (timestamp.isEqual(start) || timestamp.isAfter(start)) && (timestamp.isEqual(end) || timestamp.isBefore(end));
                    })
                    .collect(Collectors.toList());
        }

        int total = allDiagnosisList.size();
        int startIdx = Math.min(pageNo * pageSize, total);
        int endIdx = Math.min((pageNo + 1) * pageSize, total);
        int startIndex = pageNo * pageSize;

        List<Diagnosis> diagnosisList = allDiagnosisList.subList(startIdx, endIdx);

        model.addAttribute("startIndex", startIndex);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("diagnosisList", diagnosisList);

        return "viewDiagnosis";
    }
}





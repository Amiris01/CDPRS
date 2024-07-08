package com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SmartHealthRemoteSystem.SHSR.Service.DiagnosisService;

@Controller
public class DiagnosisController {
  
  private final DiagnosisService diagnosisService;

  public DiagnosisController(DiagnosisService diagnosisService){
    this.diagnosisService=diagnosisService;
  }

  @PostMapping("/sendDiagnosis")
  public String sendDiagnosis(
          @RequestParam("patientId") String patientId,
          @RequestParam("doctorId") String doctorId,
          @RequestParam("diagnosisConfirmation") String diagnosisConfirmation,
          @RequestParam(required = false) String predictedDisease,
          @RequestParam(required = false) String alternativeDiagnosisText,
          @RequestParam("remarks") String remarks,
          RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
  
      try {
          Diagnosis diagnosis = new Diagnosis();
          diagnosis.setDoctorId(doctorId);
          diagnosis.setDiagnosisConfirmation(diagnosisConfirmation);
  
          if ("confirmed".equals(diagnosisConfirmation)) {
              if (predictedDisease != null && !predictedDisease.isEmpty()) {
                  diagnosis.setDiagnosis(predictedDisease);
              } else {
                  throw new IllegalArgumentException("Predicted disease must be provided if diagnosis is confirmed.");
              }
          } else if ("alternative".equals(diagnosisConfirmation)) {
              if (alternativeDiagnosisText != null && !alternativeDiagnosisText.isEmpty()) {
                  diagnosis.setDiagnosis(alternativeDiagnosisText);
              } else {
                  throw new IllegalArgumentException("Alternative diagnosis must be provided if diagnosis is alternative.");
              }
          }
  
          diagnosis.setRemarks(remarks);
          String timeCreated = diagnosisService.createDiagnosis(diagnosis, patientId);
  
          return "redirect:/Health-status/predictionHistory?patientId=" + patientId + "&doctorId=" + doctorId + "&success=true";
      } catch (Exception e) {
          return "redirect:/Health-status/predictionHistory?patientId=" + patientId + "&doctorId=" + doctorId + "&success=false";
      }
  }

  @PostMapping("/updateDiagnosis")
  public String updateDiagnosis(
          @RequestParam("diagnosisId") String diagnosisId,
          @RequestParam("patientId") String patientId,
          @RequestParam("doctorId") String doctorId,
          @RequestParam("diagnosisConfirmation") String diagnosisConfirmation,
          @RequestParam(required = false) String predictedDisease,
          @RequestParam(required = false) String alternativeDiagnosisText,
          @RequestParam("remarks") String remarks,
          RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
  
      try {
          Diagnosis diagnosis = diagnosisService.getDiagnosis(patientId,diagnosisId);
          diagnosis.setDoctorId(doctorId);
          diagnosis.setDiagnosisConfirmation(diagnosisConfirmation);
  
          if ("confirmed".equals(diagnosisConfirmation)) {
              if (predictedDisease != null && !predictedDisease.isEmpty()) {
                  diagnosis.setDiagnosis(predictedDisease);
              } else {
                  throw new IllegalArgumentException("Predicted disease must be provided if diagnosis is confirmed.");
              }
          } else if ("alternative".equals(diagnosisConfirmation)) {
              if (alternativeDiagnosisText != null && !alternativeDiagnosisText.isEmpty()) {
                  diagnosis.setDiagnosis(alternativeDiagnosisText);
              } else {
                  throw new IllegalArgumentException("Alternative diagnosis must be provided if diagnosis is alternative.");
              }
          }
  
          diagnosis.setRemarks(remarks);
          String timeCreated = diagnosisService.updateDiagnosis(diagnosis, patientId);
  
          return "redirect:/Health-status/manageDiagnosis?patientId=" + patientId + "&success=true";
      } catch (Exception e) {
          return "redirect:/Health-status/manageDiagnosis?patientId=" + patientId + "&success=false";
      }
  }

  @PostMapping("/deleteDiagnosis")
  public String deleteDiagnosis(
          @RequestParam("diagnosisId") String diagnosisId,
          @RequestParam("patientId") String patientId,
          RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
  
      try {
          String timeCreated = diagnosisService.deleteDiagnosis(patientId, diagnosisId);
          System.out.println(timeCreated);
          return "redirect:/Health-status/manageDiagnosis?patientId=" + patientId + "&deleteSuccess=true";
      } catch (Exception e) {
          return "redirect:/Health-status/manageDiagnosis?patientId=" + patientId + "&deleteSuccess=false";
      }
  }  
}

// package com.SmartHealthRemoteSystem.SHSR.Service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis.Diagnosis;
// import com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis.DiagnosisRepository;

// import java.util.List;
// import java.util.concurrent.ExecutionException;

// @Service
// public class DiagnosisService {

//     private final DiagnosisRepository diagnosisRepository;

//     @Autowired
//     public DiagnosisService(DiagnosisRepository diagnosisRepository) {
//         this.diagnosisRepository = diagnosisRepository;
//     }
 
//     // Save diagnosis
//     public Diagnosis saveDiagnosis(Diagnosis diagnosis) throws ExecutionException, InterruptedException {
//         return diagnosisRepository.save(diagnosis);
//     }

//     // Get all diagnoses
//     public List<Diagnosis> getAllDiagnoses() throws ExecutionException, InterruptedException {
//         return diagnosisRepository.findAll();

        
//     }}

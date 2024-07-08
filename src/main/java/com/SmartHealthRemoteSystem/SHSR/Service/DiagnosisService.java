package com.SmartHealthRemoteSystem.SHSR.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SmartHealthRemoteSystem.SHSR.Prediction.Prediction;
import com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis.Diagnosis;
import com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis.DiagnosisRepository;
import com.SmartHealthRemoteSystem.SHSR.Repository.SubCollectionSHSRDAO;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class DiagnosisService {
    private final SubCollectionSHSRDAO<Diagnosis> diagnosisRepository;

    @Autowired
    public DiagnosisService(SubCollectionSHSRDAO<Diagnosis> diagnosisRepository) {
        this.diagnosisRepository = diagnosisRepository;
    }
 
      public String createDiagnosis(Diagnosis diagnosis, String patientId) throws ExecutionException, InterruptedException{
    System.out.println("patient id inside service "+patientId);
    return diagnosisRepository.save(diagnosis, patientId);
  }

  public Diagnosis getDiagnosis(String patientId, String diagnosisId) throws ExecutionException, InterruptedException{
    return diagnosisRepository.get(patientId ,diagnosisId);
  }

  public List<Diagnosis> getListDiagnosis(String patientId) throws ExecutionException, InterruptedException{
    return diagnosisRepository.getAll(patientId);
  }

  public String updateDiagnosis(Diagnosis diagnosis, String patientId) throws ExecutionException, InterruptedException{
    return diagnosisRepository.update(diagnosis, patientId);
  }

  public String deleteDiagnosis(String patientId,String diagnosisId) throws ExecutionException, InterruptedException{
    return diagnosisRepository.delete(patientId,diagnosisId);
  }
  }

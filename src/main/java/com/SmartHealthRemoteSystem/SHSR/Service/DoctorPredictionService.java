package com.SmartHealthRemoteSystem.SHSR.Service;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SmartHealthRemoteSystem.SHSR.Prediction.DoctorPrediction;
import com.SmartHealthRemoteSystem.SHSR.Prediction.Prediction;
import com.SmartHealthRemoteSystem.SHSR.Repository.SubCollectionSHSRDAO;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;

@Service
public class DoctorPredictionService {
  private final SubCollectionSHSRDAO<DoctorPrediction> predictionRepository;

  @Autowired
  public DoctorPredictionService(SubCollectionSHSRDAO<DoctorPrediction> predictionRepository){
    this.predictionRepository = predictionRepository;
  }

  public String createDoctorPrediction(DoctorPrediction prediction, String doctorId) throws ExecutionException, InterruptedException{
    System.out.println("doctor id inside service "+doctorId);
    return predictionRepository.save(prediction, doctorId);
  }

  public DoctorPrediction getDoctorPrediction(String predictionId, String doctorId) throws ExecutionException, InterruptedException{
    return predictionRepository.get(predictionId, doctorId);
  }

  public List<DoctorPrediction> getListDoctorPrediction(String doctorId) throws ExecutionException, InterruptedException{
    return predictionRepository.getAll(doctorId);
  }

  public String updateDoctorPrediction(DoctorPrediction prediction, String doctorId) throws ExecutionException, InterruptedException{
    return predictionRepository.update(prediction, doctorId);
  }

  public String deleteDoctorPrediction(String predictionId, String doctorId) throws ExecutionException, InterruptedException{
    return predictionRepository.delete(predictionId, doctorId);
  }

  public Optional<DoctorPrediction> getRecentDoctorPrediction(String doctorId) throws ExecutionException, InterruptedException{
    List<DoctorPrediction> PredictionList = predictionRepository.getAll(doctorId);
    return PredictionList.stream().max(Comparator.comparing(DoctorPrediction::getTimestamp));
  }
}

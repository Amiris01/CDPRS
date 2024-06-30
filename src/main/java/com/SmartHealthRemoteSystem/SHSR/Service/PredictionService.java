package com.SmartHealthRemoteSystem.SHSR.Service;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SmartHealthRemoteSystem.SHSR.Prediction.Prediction;
import com.SmartHealthRemoteSystem.SHSR.Repository.SubCollectionSHSRDAO;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;

@Service
public class PredictionService {
  private final SubCollectionSHSRDAO<Prediction> predictionRepository;

  @Autowired
  public PredictionService(SubCollectionSHSRDAO<Prediction> predictionRepository){
    this.predictionRepository = predictionRepository;
  }

  public String createPrediction(Prediction prediction, String patientId) throws ExecutionException, InterruptedException{
    System.out.println("patient id inside service "+patientId);
    return predictionRepository.save(prediction, patientId);
  }

  public Prediction getPrediction(String predictionId, String patientId) throws ExecutionException, InterruptedException{
    return predictionRepository.get(predictionId, patientId);
  }

  public List<Prediction> getListPrediction(String patientId) throws ExecutionException, InterruptedException{
    return predictionRepository.getAll(patientId);
  }

  public String updatePrediction(Prediction prediction, String patientId) throws ExecutionException, InterruptedException{
    return predictionRepository.update(prediction, patientId);
  }

  public String deletePrediction(String predictionId, String patientId) throws ExecutionException, InterruptedException{
    return predictionRepository.delete(predictionId, patientId);
  }

  public Optional<Prediction> getRecentPrediction(String patientId) throws ExecutionException, InterruptedException{
    List<Prediction> PredictionList = predictionRepository.getAll(patientId);
    return PredictionList.stream().max(Comparator.comparing(Prediction::getTimestamp));
  }
}

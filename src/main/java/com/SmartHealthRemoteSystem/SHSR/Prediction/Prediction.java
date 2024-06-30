package com.SmartHealthRemoteSystem.SHSR.Prediction;

import java.util.List;

public class Prediction {
  public String predictionID;
  private List<String> symptomsList;
  private List<String> diagnosisList;
  private List<Float> probabilityList;
  private String timestamp;

  public Prediction(){
  }

  public Prediction(String predictionID, List<String> symptomsList, List<String> diagnosisList, List<Float> probabilityList, String timestamp){
    this.predictionID = predictionID;
    this.symptomsList= symptomsList;
    this.diagnosisList = diagnosisList;
    this.probabilityList = probabilityList;
    this.timestamp= timestamp;
  }

  public String getPredictionID(){
    return predictionID;
  }

  public void setPredictionID(String predictionID){
    this.predictionID= predictionID;
  }

  public String getTimestamp(){
    return timestamp;
  }

  public void setTimestamp(String timestamp){
    this.timestamp=timestamp;
  }

  public List<String> getSymptomsList(){
    return symptomsList;
  }

  public void setSymptomsList(List<String> symptomsList){
    this.symptomsList=symptomsList;
  }

  public List<String> getDiagnosisList(){
    return diagnosisList;
  }

  public void setDiagnosisList(List<String> diagnosisList){
    this.diagnosisList=diagnosisList;
  }

  public List<Float> getProbabilityList(){
    return probabilityList;
  }

  public void setProbabilityList(List<Float> probabilityList){
    this.probabilityList = probabilityList;
  }

}


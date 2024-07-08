package com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis;

import java.util.List;

public class Diagnosis{
  public String diagnosisId;
  private String doctorId;
  private String diagnosisConfirmation;
  private String diagnosis;
  private String remarks;
  private String timestamp;

  public Diagnosis(){

  }

  public Diagnosis(String diagnosisId,String doctorId,String diagnosisConfirmation,String diagnosis,String remarks,String timestamp){
    this.diagnosisId= diagnosisId;
    this.doctorId=doctorId;
    this.diagnosisConfirmation=diagnosisConfirmation;
    this.diagnosis=diagnosis;
    this.remarks=remarks;
    this.timestamp=timestamp;
  }

  public String getDiagnosisId(){
    return diagnosisId;
  }

  public void setDiagnosisId(String diagnosisId){
    this.diagnosisId=diagnosisId;
  }

  public String getDoctorId(){
    return doctorId;
  }

  public void setDoctorId(String doctorId){
    this.doctorId=doctorId;
  }

  public String getDiagnosisConfirmation(){
    return diagnosisConfirmation;
  }

  public void setDiagnosisConfirmation(String diagnosisConfirmation){
    this.diagnosisConfirmation=diagnosisConfirmation;
  }

  public String getDiagnosis(){
    return diagnosis;
  }

  public void setDiagnosis(String diagnosis){
    this.diagnosis=diagnosis;
  }

  public String getRemarks(){
    return remarks;
  }

  public void setRemarks(String remarks){
    this.remarks=remarks;
  }

  public String getTimestamp(){
    return timestamp;
  }

  public void setTimestamp(String timestamp){
    this.timestamp=timestamp;
  }

}
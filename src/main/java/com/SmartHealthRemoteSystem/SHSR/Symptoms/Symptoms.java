package com.SmartHealthRemoteSystem.SHSR.Symptoms;

public class Symptoms {
  private String SymptomID;
  private String name;
  private String value;
  
  public Symptoms(){
  }

  public Symptoms(String SymptomID){
    this.SymptomID=SymptomID;
  }

  public Symptoms(String SymptomID, String name){
    this.SymptomID=SymptomID;
    this.name=name;
  }

  public Symptoms(String SymptomID,String name, String value){
    this.SymptomID=SymptomID;
    this.name=name;
    this.value=value;
  }

  public void setSymptomID(String SymptomID){
    this.SymptomID=SymptomID;
  }

  public String getSymptomID(){
    return SymptomID;
  }

  public void setName(String name){
    this.name=name;
  }

  public String getName(){
    return name;
  }

  public void setValue(String value){
    this.value=value;
  }

  public String getValue(){
    return value;
  }
}

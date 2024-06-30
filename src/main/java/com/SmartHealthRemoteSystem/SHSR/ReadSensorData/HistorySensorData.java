package com.SmartHealthRemoteSystem.SHSR.ReadSensorData;

import com.google.cloud.Timestamp;

public class HistorySensorData {
    private Double ecgReading;
    private Double bodyTemperature;
    private Timestamp timestamp;
    private String sensorDataId;
    private Double oxygenReading;
    private int Heart_Rate;//mg, ijat, keng, faruq, din


    public HistorySensorData() {
    }

    public HistorySensorData( int Heart_Rate, Double bodyTemperature,Double ecgReading,Double OxygenReading, String sensorDataId, Timestamp timestamp) {//din
        this.ecgReading = ecgReading;
        this.bodyTemperature = bodyTemperature;
        this.timestamp = timestamp;
        this.sensorDataId = sensorDataId;
        this.oxygenReading= OxygenReading;
        this.Heart_Rate = Heart_Rate;

    }

    public void SetHistorySensorData( int Heart_Rate, Double bodyTemperature,Double ecgReading,Double OxygenReading, String sensorDataId, Timestamp timestamp) {//din
        this.ecgReading = ecgReading;
        this.bodyTemperature = bodyTemperature;
        this.timestamp = timestamp;
        this.sensorDataId = sensorDataId;
        this.oxygenReading= OxygenReading;
        this.Heart_Rate = Heart_Rate;//mg, ijat, keng, faruq, din

    }

    public Double getEcgReading() {
        return ecgReading;
    }

    public void setEcgReading(Double ecgReading) {
        this.ecgReading = ecgReading;
    }

    public Double getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(Double bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }

    public void setOxygenReading(Double OxygenReading){this.oxygenReading=OxygenReading;}

    public Double getOxygenReading(){return oxygenReading;}

    public void setHeart_Rate(int Heart_Rate){this.Heart_Rate=Heart_Rate;}//mg, ijat, keng, faruq, din

    public int getHeart_Rate(){return Heart_Rate;}//mg, ijat, keng, faruq, din

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getSensorDataId() {
        return sensorDataId;
    }

    public void setSensorDataId(String sensorDataId) {
        this.sensorDataId = sensorDataId;
    }
}

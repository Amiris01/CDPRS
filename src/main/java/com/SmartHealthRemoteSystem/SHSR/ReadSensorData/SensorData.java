package com.SmartHealthRemoteSystem.SHSR.ReadSensorData;

import com.google.cloud.Timestamp;

import java.sql.Time;

public class SensorData {
    private Double ecgReading;
    private Double bodyTemperature;
    private Timestamp timestamp;
    private String sensorDataId;
    private Double oxygenReading;
    private int Heart_Rate;//mg, ijat, keng, faruq, din


    public SensorData() {
    }

    public SensorData(Double ecgReading, Double bodyTemperature, Double oxygenReading, int Heart_Rate) {//din
        this.ecgReading = ecgReading;
        this.bodyTemperature = bodyTemperature;
        this.oxygenReading = oxygenReading;
        this.Heart_Rate = Heart_Rate;//mg, ijat, keng, faruq, din

    }

    public SensorData(Double ecgReading, Double bodyTemperature, Timestamp timestamp, String sensorDataId, Double OxygenReading, int Heart_Rate) {//din
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

    @Override
    public String toString() {
        return "SensorData{" +
                "ecgReading='" + ecgReading + '\'' +
                "\n timestamp=" + timestamp +
                "\n sensorDataId='" + sensorDataId + '\'' +
                '}';
    }
}

package com.SmartHealthRemoteSystem.SHSR.User.Patient;

import com.SmartHealthRemoteSystem.SHSR.User.User;

public class Patient extends User {
    private String sensorDataId;
    private String address;
    private String emergencyContact;
    private String assigned_doctor;
    private String status;
    
    public Patient() {
    }

    public Patient(String userId, String name, String password, String contact, String role, String email, String sensorDataId, String address, String emergencyContact, String assigned_doctor, String status) {
        super(userId, name, password, contact, role, email);
        this.sensorDataId = sensorDataId;
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.assigned_doctor = assigned_doctor;
        this.status = status;
    }

    public Patient(String userId, String name, String password, String contact, String role, String email, String sensorDataId, String address, String emergencyContact, String assigned_doctor) {
        super(userId, name, password, contact, role, email);
        this.sensorDataId = sensorDataId;
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.assigned_doctor = assigned_doctor;

    }

    public Patient(String userId, String name, String password, String contact, String role, String email, String address, String emergencyContact,String sensorDataId) {
        super(userId, name, password, contact, role, email);
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.sensorDataId=sensorDataId;
    }

    public Patient(Patient editProf) {
    }

    public String getSensorDataId() {
        return sensorDataId;
    }

    public void setSensorDataId(String sensorDataId) {
        this.sensorDataId = sensorDataId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getAssigned_doctor() {
        return assigned_doctor;
    }

    public void setAssigned_doctor(String assigned_doctor) {
        this.assigned_doctor = assigned_doctor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

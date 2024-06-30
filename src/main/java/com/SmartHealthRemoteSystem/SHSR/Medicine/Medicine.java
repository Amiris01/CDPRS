package com.SmartHealthRemoteSystem.SHSR.Medicine;

public class Medicine {

    private String medName;
    private String medId;
    private int quantity;
    private String patientId;

    public Medicine() {
    }

    public Medicine(String medName) {
        this.medName = medName;
    }

    public Medicine(String medName, int quantity) {
        this.medName = medName;
        this.quantity = quantity;
    }

    public Medicine(String medName, String medId, int quantity) {
        this.medName = medName;
        this.medId = medId;
        this.quantity = quantity;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    public String getMedId() {
        return medId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientId() {
        return patientId;
    }
}

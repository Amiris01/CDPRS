package com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription;

public class PrescribeMedicine {
    private String prescriptionId;
    private String medName;
    private int prescribedQuantity;

    public PrescribeMedicine() {
    }

    public PrescribeMedicine(String prescriptionId, String medName, int prescribedQuantity) {
        this.prescriptionId = prescriptionId;
        this.medName = medName;
        this.prescribedQuantity = prescribedQuantity;
    }

    public String getPrescriptonId() {
        return prescriptionId;
    }

    public void setPrescriptonId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public int getPrescribedQuantity() {
        return prescribedQuantity;
    }

    public void setPrescribedQuantity(int prescribedQuantity) {
        this.prescribedQuantity = prescribedQuantity;
    }
}
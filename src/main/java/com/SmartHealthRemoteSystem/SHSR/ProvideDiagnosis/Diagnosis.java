// package com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis;

// import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
// import javax.persistence.GenerationType;
// import javax.persistence.Id;

// @Entity // This tells Hibernate to make a table out of this class
// public class Diagnosis {
 
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private String id; // primary key
//     private String patientId;
//     private String diagnosis;

//     // Default Constructor
//     public Diagnosis() {
//     }

    
//     // Parameterized Constructor
//     public Diagnosis(String patientId, String diagnosis) {
//         this.patientId = patientId;
//         this.diagnosis = diagnosis;
//     }

//     // Getters and setters...
//     public String getId() {
//         return id;
//     }

//     public void setId(String documentId) {
//         this.id = documentId;
//     }

//     public String getPatientId() {
//         return patientId;
//     }

//     public void setPatientId(String patientId) {
//         this.patientId = patientId;
//     }

//     public String getDiagnosis() {
//         return diagnosis;
//     }

//     public void setDiagnosis(String diagnosis) {
//         this.diagnosis = diagnosis;
//     }

//     // toString method
//     @Override
//     public String toString() {
//         return "Diagnosis{" +
//                 "id=" + id +
//                 ", patientId='" + patientId + '\'' +
//                 ", diagnosis='" + diagnosis + '\'' +
//                 '}';
//     }
// }
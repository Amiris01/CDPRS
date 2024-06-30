package com.SmartHealthRemoteSystem.SHSR.User.Patient;

import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.SensorData;
import com.SmartHealthRemoteSystem.SHSR.Repository.SHSRDAO;
import com.SmartHealthRemoteSystem.SHSR.Repository.SubCollectionSHSRDAO;
import com.SmartHealthRemoteSystem.SHSR.SendDailyHealth.HealthStatus;
import com.SmartHealthRemoteSystem.SHSR.User.User;
import com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription.Prescription;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Repository
public class PatientRepository implements SHSRDAO<Patient> {
    public static final String COL_NAME = "Patient";

    private final SHSRDAO<User> userRepository;
    private final SubCollectionSHSRDAO<HealthStatus> healthStatusRepository;
    private final SHSRDAO<SensorData> sensorDataRepository;
    private final SubCollectionSHSRDAO<Prescription> prescriptionRepository;

    @Autowired
    public PatientRepository(SHSRDAO<User> userRepository, SubCollectionSHSRDAO<HealthStatus> healthStatusRepository,
                             SHSRDAO<SensorData> sensorDataRepository, SubCollectionSHSRDAO<Prescription> prescriptionRepository) {
        this.userRepository = userRepository;
        this.healthStatusRepository = healthStatusRepository;
        this.sensorDataRepository = sensorDataRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @Override
    public Patient get(String patientId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COL_NAME).document(patientId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        Patient tempPatient;
        if (document.exists()) {
            tempPatient = document.toObject(Patient.class);
            User user = userRepository.get(patientId);
            assert tempPatient != null;
            tempPatient.setUserId(user.getUserId());
            tempPatient.setName(user.getName());
            tempPatient.setPassword(user.getPassword());
            tempPatient.setContact(user.getContact());
            tempPatient.setRole(user.getRole());
            tempPatient.setEmail(user.getEmail());
            return tempPatient;
        } else {
            return null;
        }
    }

    @Override
    public List<Patient> getAll() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Iterable<DocumentReference> documentReference = dbFirestore.collection(COL_NAME).listDocuments();
        Iterator<DocumentReference> iterator = documentReference.iterator();

        List<Patient> patientList = new ArrayList<>();
        Patient patient;
        
        while (iterator.hasNext()) {
            DocumentReference documentReference1 = iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference1.get();
            DocumentSnapshot document = future.get();
            patient = document.toObject(Patient.class);

            // Check if user is null before further processing
            if (patient != null) {
                User user = userRepository.get(document.getId());
                if (user != null) {
                    patient.setUserId(user.getUserId());
                    patient.setPassword(user.getPassword());
                    patient.setName(user.getName());
                    patient.setContact(user.getContact());
                    patient.setRole(user.getRole());
                    patient.setEmail(user.getEmail());
                    patientList.add(patient);
                }
            }
        }

        return patientList;
    }

    @Override
    public String save(Patient patient) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Map<String, String> tempPatient = new HashMap<>();
        tempPatient.put("address", patient.getAddress());
        tempPatient.put("emergencyContact", patient.getEmergencyContact());
        tempPatient.put("sensorDataId", patient.getSensorDataId());
        tempPatient.put("assigned_doctor", patient.getAssigned_doctor());
        tempPatient.put("status",patient.getStatus());


        //Create a temporary User
        User user = new User(patient.getUserId(), patient.getName(), patient.getPassword(), patient.getContact(), patient.getRole(), patient.getEmail());
        userRepository.save(user);

        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COL_NAME).document(patient.getUserId()).set(tempPatient);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

   /*  @Override
    public String update(Patient patient) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        if (!(patient.getAddress().isEmpty())) {
            ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COL_NAME).document(patient.getUserId())
                    .update("address", patient.getAddress());
        }
        if (!(patient.getAssigned_doctor() == null)) {
            ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COL_NAME).document(patient.getUserId())
                    .update("assigned_doctor", patient.getAssigned_doctor());
        }
        if (!(patient.getEmergencyContact().isEmpty())) {
            ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COL_NAME).document(patient.getUserId())
                    .update("emergencyContact", patient.getEmergencyContact());
        }
        if (!(patient.getSensorDataId() == null)) {
            ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COL_NAME).document(patient.getUserId())
                    .update("sensorDataId", patient.getSensorDataId());
        }
        if (!(patient.getStatus() == null)) {
            ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COL_NAME).document(patient.getUserId())
                    .update("status", patient.getStatus());
        }

        return userRepository.update(new User(patient.getUserId(), patient.getName(), patient.getPassword(), patient.getContact(), patient.getRole()));
    } */

    @Override
    public String update(Patient patient) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        // DocumentReference docRef = dbFirestore.collection(COL_NAME).document(patient.getUserId());

        // Map<String, Object> updates = new HashMap<>();
        if (!(patient.getAddress().isEmpty())) {
            dbFirestore.collection(COL_NAME).document(patient.getUserId()).update("address", patient.getAddress());
        }
        if (!(patient.getEmergencyContact().isEmpty())) {
            dbFirestore.collection(COL_NAME).document(patient.getUserId()).update("emergencyContact", patient.getEmergencyContact());
        }
        if (!(patient.getAssigned_doctor() == null)){
            dbFirestore.collection(COL_NAME).document(patient.getUserId()).update("assigned_doctor", patient.getAssigned_doctor());
        }
        if (!(patient.getName().isEmpty())) {
            dbFirestore.collection(COL_NAME).document(patient.getUserId()).update("name", patient.getName());
        }
        if (!(patient.getContact().isEmpty())) {
            dbFirestore.collection(COL_NAME).document(patient.getUserId()).update("contact", patient.getContact());
        }

        User user = new User(patient.getUserId(), patient.getName(), patient.getPassword(), patient.getContact(), patient.getRole(), patient.getEmail());
        return userRepository.update(user);
    } 
 
    @Override
    public String delete(String patientId) throws ExecutionException, InterruptedException {
        String healthStatusMessage = "", prescriptionMessage = "", timeDeleteUser, messageSensor = "";

        Firestore dbFirestore = FirestoreClient.getFirestore();
        Patient patient = get(patientId);
     
        //delete sensor from sensor table
        //since patient and sensor is 1-to-1 composition relation,
        //delete the patient will also delete the sensor
        if (patient.getSensorDataId().isEmpty()) {
            ApiFuture<WriteResult> writeResult = dbFirestore.collection(COL_NAME).document(patientId).delete();
             timeDeleteUser = userRepository.delete(patientId);
             return "Document with Patient Id " + patientId + " has been deleted. " + "\n";// + timeDeleteUser;
            // messageSensor = "sensor is not included";
            
            //since patient doesn't have sensorId, we are not deleting the sensor from sensor database table
        } else {
            messageSensor = sensorDataRepository.delete(patient.getSensorDataId());
        }

        //Delete all health status patient in the database
        List<HealthStatus> healthStatusList = healthStatusRepository.getAll(patientId);
        for (HealthStatus healthStatus : healthStatusList) {
            healthStatusMessage += healthStatusRepository.delete(patientId, healthStatus.getHealthStatusId()) + "\n";
        }

        //Delete all prescription patient in the database
        List<Prescription> prescriptionList = prescriptionRepository.getAll(patientId);
        for (Prescription prescription : prescriptionList) {
            prescriptionMessage += prescriptionRepository.delete(patientId, prescription.getPrescriptionId()) + "\n";
        }

        //delete patient
        ApiFuture<WriteResult> writeResult = dbFirestore.collection(COL_NAME).document(patientId).delete();
        timeDeleteUser = userRepository.delete(patientId);


        return "Document with Patient Id " + patientId + " has been deleted. " + "\n" ;
        // messageSensor + " \n+" +
        // healthStatusMessage +
        // prescriptionMessage + "\n" +
        // timeDeleteUser;
    }
}

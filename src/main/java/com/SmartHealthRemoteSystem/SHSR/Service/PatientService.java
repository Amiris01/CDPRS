package com.SmartHealthRemoteSystem.SHSR.Service;


import com.SmartHealthRemoteSystem.SHSR.Repository.SHSRDAO;
import com.SmartHealthRemoteSystem.SHSR.Repository.SubCollectionSHSRDAO;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.DoctorRepository;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.User.User;
import com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription.Prescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PatientService {

    private SHSRDAO<User> userRepository;
    private SHSRDAO<Patient> patientRepository;
    private SHSRDAO<Doctor> doctorRepository;
    private SubCollectionSHSRDAO<Prescription> prescriptionRepository;


    @Autowired
    public PatientService(SHSRDAO<User> userRepository, SHSRDAO<Patient> patientRepository,
                          SHSRDAO<Doctor> doctorRepository, SubCollectionSHSRDAO<Prescription> prescriptionRepository) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public String createPatient(Patient patient) throws ExecutionException, InterruptedException {
        boolean checkUserExist = false;
        //Create a temporary User
        User user = new User(patient.getUserId(), patient.getName(), patient.getPassword(), patient.getContact(), patient.getRole(), patient.getEmail());
        //get list of all user
        List<User> userList = userRepository.getAll();
        for(User u: userList){
            //if the Id already exist
            if(u.getUserId().equals(patient.getUserId())){
                checkUserExist = true;
                break;
            }
        }
        //return error message
        if(checkUserExist){
            return "Error create patient with id "+ patient.getUserId() + ". Please use another Id.";
        }

        //Otherwise, save the user in user collection and patient collection
        userRepository.save(user);
        return patientRepository.save(patient);
    }

    public String deletePatient(String patientId) throws ExecutionException, InterruptedException {
        System.out.println(patientId);
        return patientRepository.delete(patientId);
    }

     public String updatePatient(Patient patient) throws ExecutionException, InterruptedException {
        return patientRepository.update(patient);
    } 

    public Patient getPatient(String patientId) throws ExecutionException, InterruptedException {
        return patientRepository.get(patientId);
    }

    public String getPatientSensorId(String patientId) throws ExecutionException, InterruptedException {
        Patient patient = patientRepository.get(patientId);
        return patient.getSensorDataId();
    }


    public Prescription getPrescription(String patientId) throws ExecutionException, InterruptedException {
        Prescription prescription = null;

        List<Prescription> prescriptionList = prescriptionRepository.getAll(patientId);
        if (!prescriptionList.isEmpty()) {
            prescription = prescriptionList.get(prescriptionList.size() - 1);
        }

        return prescription;
    } 

    public List<Prescription> getAllPrescription(String patientId) throws ExecutionException, InterruptedException {
        return prescriptionRepository.getAll(patientId);
    }

    public List<Patient> getPatientList() throws ExecutionException, InterruptedException {
        return patientRepository.getAll();
    }


}
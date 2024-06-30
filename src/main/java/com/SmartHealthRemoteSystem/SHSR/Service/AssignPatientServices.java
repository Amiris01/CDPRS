package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.Repository.SHSRDAO;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class AssignPatientServices {

    private final SHSRDAO<Patient> patientRepository;
    private final SHSRDAO<Doctor> doctorRepository;

    public AssignPatientServices(SHSRDAO<Patient> patientRepository, SHSRDAO<Doctor> doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }
    public List<Patient> getListPatient() throws ExecutionException, InterruptedException {
        //function to return list of unassigned patient
        List<Patient> patients=patientRepository.getAll();
        for(int i=patients.size()-1;i>=0;i--)
        {
           if (!(patients.get(i).getAssigned_doctor().isEmpty()&& patients.get(i).getStatus().equals("Under Surveillance"))){
                patients.remove(i);
           }
        }
        return patients;
    }

    public Doctor getDoctor(String drID) throws ExecutionException, InterruptedException {

        return doctorRepository.get(drID);
    }

    public Patient getPatient(String patientID) throws ExecutionException, InterruptedException {
        return patientRepository.get(patientID);
    }

    public void AssignPatient(String patientId,String doctorId) throws ExecutionException, InterruptedException {
        Doctor doctor=doctorRepository.get(doctorId);
        Patient patient=patientRepository.get(patientId);
        patient.setAssigned_doctor(doctorId);
        patient.setStatus("Under Surveillance");
        patientRepository.update(patient);
    }

    public void UnassignDoctor(String patientId,String doctorId) throws ExecutionException, InterruptedException {
        Doctor doctor=doctorRepository.get(doctorId);
        Patient patient=patientRepository.get(patientId);
        patient.setAssigned_doctor("");
        patientRepository.update(patient);
    }
    
    public void ReleasePatient(String patientId,String doctorId) throws ExecutionException, InterruptedException {
        Doctor doctor=doctorRepository.get(doctorId);
        Patient patient=patientRepository.get(patientId);
        patient.setStatus("Released");
        patientRepository.update(patient);
    }
}

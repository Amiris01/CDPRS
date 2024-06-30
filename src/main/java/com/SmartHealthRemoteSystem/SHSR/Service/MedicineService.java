package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.Medicine.Medicine;
import com.SmartHealthRemoteSystem.SHSR.Medicine.MedicineRepository;
import com.SmartHealthRemoteSystem.SHSR.Repository.SHSRDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class MedicineService {
    
    private final SHSRDAO<Medicine> MedicineRepository;

    public MedicineService() {
        MedicineRepository=new MedicineRepository();
    }

    @Autowired
    public MedicineService(SHSRDAO<Medicine> MedicineRepository) {
        this.MedicineRepository = MedicineRepository;
    }

    public String createMedicine(Medicine medicine) throws ExecutionException, InterruptedException {
        return MedicineRepository.save(medicine);
    }

    public List<Medicine> getListMedicine() throws ExecutionException, InterruptedException {
        return MedicineRepository.getAll();
    }

    public String deleteMedicine(String MedId) throws ExecutionException, InterruptedException {
        return MedicineRepository.delete(MedId);
    }

    public Medicine getMedicine(String medicine) throws ExecutionException, InterruptedException {
        return MedicineRepository.get(medicine);
    }

    public String updateMedicine(Medicine medicine) throws ExecutionException, InterruptedException {
        return MedicineRepository.update(medicine);
    }

    public String stringMedicine(String MedId) throws ExecutionException, InterruptedException {
        Medicine medicine=MedicineRepository.get(MedId);
        return medicine.toString();
    }

    public void addMedicine(String medName, int quantity) throws ExecutionException, InterruptedException {
        // Create a new Medicine object with auto-generated medId
        Medicine newMedicine = new Medicine(medName, quantity);
    
        // Call the service method to add the new medicine to the database
        MedicineRepository.save(newMedicine);
    }

    public List<Medicine> getMedicineList() throws ExecutionException, InterruptedException {
        return MedicineRepository.getAll();
    }

    public void prescribeMedicine(String patientId, String medId, int quantity) throws ExecutionException, InterruptedException {
        // Retrieve the medicine using its ID
        Medicine medicine = MedicineRepository.get(medId);
        
        // Update the medicine quantity (based on your business logic; you might need to decrease stock)
        int newQuantity = medicine.getQuantity() - quantity;
        medicine.setQuantity(newQuantity);
        
        // Persist the updated medicine quantity
        MedicineRepository.update(medicine);
    }

    public List<Medicine> getListPrescribe(String patientId) throws ExecutionException, InterruptedException {
        List<Medicine> prescirbeMedicine = MedicineRepository.getAll();
        return prescirbeMedicine;
    }
}

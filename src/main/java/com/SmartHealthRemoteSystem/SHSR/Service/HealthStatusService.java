package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.Repository.SubCollectionSHSRDAO;
import com.SmartHealthRemoteSystem.SHSR.SendDailyHealth.HealthStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class HealthStatusService {

    private final SubCollectionSHSRDAO<HealthStatus> healthStatusRepository;

    @Autowired
    public HealthStatusService(SubCollectionSHSRDAO<HealthStatus> healthStatusRepository) {
        this.healthStatusRepository = healthStatusRepository;
    }

    public String createHealthStatus(HealthStatus healthStatus, String patientId) throws ExecutionException, InterruptedException {
        return healthStatusRepository.save(healthStatus,patientId);
    }

    public List<HealthStatus> getListHealthStatus(String patientId) throws ExecutionException, InterruptedException {
        return healthStatusRepository.getAll(patientId);
    }

    public HealthStatus getHealthStatus(String healthStatusId, String patientId) throws ExecutionException, InterruptedException {
        return healthStatusRepository.get(healthStatusId, patientId);
    }

    public void updateHealthStatus(HealthStatus healthStatus, String patientId) throws ExecutionException, InterruptedException {
        String timeUpdated = healthStatusRepository.update(healthStatus, patientId);
    }

    public String deleteHealthStatus(String healthStatusId, String patientId) throws ExecutionException, InterruptedException {
            return healthStatusRepository.delete(healthStatusId, patientId);
    }     
}

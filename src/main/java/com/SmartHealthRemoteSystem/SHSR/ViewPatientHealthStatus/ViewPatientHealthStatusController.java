package com.SmartHealthRemoteSystem.SHSR.ViewPatientHealthStatus;

import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.SensorData;
import com.SmartHealthRemoteSystem.SHSR.SendDailyHealth.HealthStatus;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.HealthStatusService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Service.PrescriptionService;
import com.SmartHealthRemoteSystem.SHSR.Service.SensorDataService;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/viewPatientHealthStatus")
public class ViewPatientHealthStatusController {
    private final HealthStatusService healthStatusService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    @Autowired
    public ViewPatientHealthStatusController( HealthStatusService healthStatusService,DoctorService doctorService, PatientService patientService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.healthStatusService = healthStatusService;
    }

    @PostMapping("/a")
    public String getHealthStatus(@RequestParam("patientId")String patientId,
                                  @RequestParam("doctorId")String doctorId, Model model) throws ExecutionException, InterruptedException {
        //Retrieve information
        Patient patient=patientService.getPatient(patientId);
        Doctor doctor=doctorService.getDoctor(doctorId);

        //Retrive patient list of health status
        List<HealthStatus> healthStatus= healthStatusService.getListHealthStatus(patientId);
        
        SensorDataService sensorDataService = new SensorDataService();
        model.addAttribute("patientid",patientId);
        

        SensorData sensorData = sensorDataService.getSensorData(patient.getSensorDataId());
        model.addAttribute("sensorDataList",sensorData);

        model.addAttribute("patient",patient);
        model.addAttribute("doctor",doctor);
        model.addAttribute("healthStatusList",healthStatus);

        return "viewPatientHealthStatus" ;

    } 

    @GetMapping("/b")
    public String getHealthStatusPatient(@RequestParam("patientId")String patientId,
                                  @RequestParam("doctorId")String doctorId, Model model, @RequestParam(value = "pageNo") int pageNo) throws ExecutionException, InterruptedException {
        //Retrieve information
        Patient patient=patientService.getPatient(patientId);
        Doctor doctor=doctorService.getDoctor(doctorId);

        //Retrive patient list of health status
        List<HealthStatus> healthStatus= healthStatusService.getListHealthStatus(patientId);
        //        List<HealthStatus> threeLastHealth=null;
        //    for (int i = healthStatus.size()-1; i==(healthStatus.size()-3) ; i--) {
        //        threeLastHealth.add(healthStatus.get(i));
        //    }

        //paging start
        int pageSize = 5;
        int currentPage = pageNo;
        int start = (currentPage - 1) * pageSize;
        int end = start + pageSize;
        if (start > healthStatus.size()) {
            start = 0;
        }
        if (end > healthStatus.size()) {
            end = healthStatus.size();
        }
        List<HealthStatus> healthStatusDisplay = healthStatus.subList(start, end);
        int totalPages = (int) Math.ceil((double) healthStatus.size() / pageSize);

        int prevPage = currentPage - 1;
        int nextPage = currentPage + 1;
        if (prevPage < 1) {
        prevPage = 1;
        }
        if (nextPage > totalPages) {
        nextPage = totalPages;
        }
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("prevPage", prevPage);
        //paging end

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", totalPages);
        model.addAttribute("patient",patient);
        model.addAttribute("doctor",doctor);
        model.addAttribute("healthStatusList",healthStatusDisplay);

        return "viewDailyHealthSymptom" ;
    }

    @PostMapping("/deletesymptom")
    public String deletesymptom(@RequestParam("patientId")String patientId,
                                  @RequestParam("doctorId")String doctorId,
                                  @RequestParam("healthstatus")String healthstatusId,
                                   Model model) throws ExecutionException, InterruptedException {
       
        Patient patient=patientService.getPatient(patientId);
        Doctor doctor=doctorService.getDoctor(doctorId);
        healthStatusService.deleteHealthStatus(patientId,healthstatusId);

        List<HealthStatus> healthStatus= healthStatusService.getListHealthStatus(patientId);

        model.addAttribute("patient",patient);
        model.addAttribute("doctor",doctor);
        model.addAttribute("healthStatusList",healthStatus);

        return "redirect:/viewPatientHealthStatus/b?patientId=" + patientId + "&doctorId=" + doctorId;
    } 
}

    

    /**
     * @param id
     * @return
     */


   
 

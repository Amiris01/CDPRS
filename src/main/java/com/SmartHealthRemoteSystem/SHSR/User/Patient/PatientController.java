package com.SmartHealthRemoteSystem.SHSR.User.Patient;

import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.SensorData;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Service.SensorDataService;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription.Prescription;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.Query.Direction;
import com.google.firebase.cloud.FirestoreClient;
// import javax.persistence.EntityManager;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.Acl.Entity;

import java.util.ArrayList;

import java.util.concurrent.ExecutionException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final DoctorService doctorService;
   /*  @Autowired    
        EntityManager entityManager; */

    @Autowired
    public PatientController(PatientService patientService, DoctorService doctorService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @GetMapping
    public String getPatientDashboard(Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Patient patient = patientService.getPatient(myUserDetails.getUsername());
        Doctor doctor = doctorService.getDoctor(patient.getAssigned_doctor());
        List<Patient> patientList = patientService.getPatientList();

        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);
        model.addAttribute("patientList", patientList);

        return "patientDashBoard";
    }

    @GetMapping("/editProfile")
    public String editUserProfile( Model model) throws ExecutionException, InterruptedException{
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Patient patient = patientService.getPatient(myUserDetails.getUsername());
        Doctor doctor = doctorService.getDoctor(patient.getAssigned_doctor());
        List<Patient> patientList = patientService.getPatientList();

        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);
        model.addAttribute("patientList", patientList);
  
        return "test";
    }

    @PostMapping("/editProfile/submit")
    public String submitUserProfile(@ModelAttribute Patient patient) throws ExecutionException, InterruptedException{
        
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        patient.setUserId(myUserDetails.getUsername());
        patientService.updatePatient(patient);
        
        
       
        return "test";

        
    }

    @GetMapping("/viewPrescription")
    public String getPatientListThatAssignedToDoctor(Model model, @RequestParam(value = "pageNo") int pageNo) throws ExecutionException, InterruptedException {


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();

        Patient patient = patientService.getPatient(myUserDetails.getUsername());
        Doctor doctor = doctorService.getDoctor(patient.getAssigned_doctor());
        
        ArrayList<Prescription> prescriptionTemp = (ArrayList<Prescription>) patientService.getAllPrescription(patient.getUserId());

        ArrayList<Prescription> prescriptionList = quickSort(prescriptionTemp);

        Prescription prescription = null;
        if ((!prescriptionList.isEmpty()) && pageNo == -1) {
            prescription = prescriptionList.get(prescriptionList.size() - 1);
            pageNo = prescriptionList.size() - 1;
        } else if (!prescriptionList.isEmpty()) {
            prescription = prescriptionList.get(pageNo);
        }
        int totalPage = prescriptionList.size();

        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);
        model.addAttribute("prescription", prescription);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", totalPage);

        return "viewPrescription";
    }

    @PostMapping("/backDashboard")
    public String backDashboard(@RequestParam(value = "patientId") String patientId, Model model) throws ExecutionException, InterruptedException {
        Patient patient = patientService.getPatient(patientId);
        Doctor doctor = doctorService.getDoctor(patient.getAssigned_doctor());
        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);
        return "patientDashBoard";
    }

    @GetMapping("/list") 
    public String ListAssignedPatient(Model model)throws ExecutionException, InterruptedException{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails= (MyUserDetails) auth.getPrincipal();
        Doctor doctor = doctorService.getDoctor(myUserDetails.getUsername());
        List<Patient> patientList = patientService.getPatientList();
        //Remove unassigned patients
        for(int i=patientList.size()-1;i>=0;i--)
        {
            if ((patientList.get(i).getAssigned_doctor().isEmpty())){
                patientList.remove(i);
            }
        }
        model.addAttribute("patientList", patientList);
        model.addAttribute("doctor", doctor);
        return "listAssignedPatient";
    }

         //SENSOR DASHBOARD FOR PATIENT VIEW // 
         @GetMapping("/sensorDashboard")
        public String getSensorDashboard(Model model, @RequestParam(value= "patientId") String patientId) throws Exception {
        Firestore firestore = FirestoreClient.getFirestore();
          Patient patient = doctorService.getPatient(patientId);
          SensorDataService sensorDataService = new SensorDataService();
          SensorData sensorData = sensorDataService.getSensorData(patient.getSensorDataId());
        
          Query query = firestore.collection("SensorData")
          .document(patient.getSensorDataId())
          .collection("SensorDataHistory").orderBy("#", Direction.DESCENDING).limit(1);
          ApiFuture<QuerySnapshot> querySnapshot = query.get();
          List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        
          int highestDocumentNumber = 0;
          if (!documents.isEmpty()) {
            highestDocumentNumber = documents.get(0).getLong("#").intValue();
          }
          
          // Create a new document with the next document number
          DocumentReference docRef = firestore.collection("SensorData")
          .document(patient.getSensorDataId())
          .collection("SensorDataHistory")
          .document("sensordata" + String.format("%03d", highestDocumentNumber + 1));
  
    // Populate the document with the sensor data fields
          Map<String, Object> data = new HashMap<>();
          data.put("#", highestDocumentNumber +1 );
          data.put("Heart_Rate", sensorData.getHeart_Rate());
          data.put("bodyTemperature", sensorData.getBodyTemperature());
          data.put("ecgReading", sensorData.getEcgReading());
          data.put("oxygenReading", sensorData.getOxygenReading());
          data.put("sensorDataId", sensorData.getSensorDataId());
          data.put("timestamp", sensorData.getTimestamp());
  
          model.addAttribute("sensorDataList",sensorData);
          model.addAttribute("patientid",patientId);
  
          // Write the data to the document
          docRef.set(data);

         
       
        Iterable<DocumentReference> documentReference = firestore.collection("SensorData")
        .document(patient.getSensorDataId())
        .collection("SensorDataHistory").listDocuments();
        Iterator<DocumentReference> iterator = documentReference.iterator();

        List<SensorData> sensorDataList = new ArrayList<>();
        SensorData sensorDatahistory;
        while (iterator.hasNext()) {
            DocumentReference documentReference1=iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference1.get();
            DocumentSnapshot document = future.get();
            sensorData = document.toObject(SensorData.class);
            sensorDataList.add(sensorData);
            model.addAttribute("sensorDataListHistory",sensorDataList);
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println(sensorDataList);
            System.out.println("-------------------------------------------------------------------------------");

        }

        model.addAttribute("success","success");
       
          return "sensorDashboard";
      }

    


      //Prescription//
    public ArrayList<Prescription> quickSort(ArrayList<Prescription> list)
    {
        if (list.isEmpty())
            return list;
        ArrayList<Prescription> sorted;
        ArrayList<Prescription> smaller = new ArrayList<Prescription>();
        ArrayList<Prescription> greater = new ArrayList<Prescription>();
        Prescription pivot = list.get(0);
        int i;
        Prescription j;
        for (i=1;i<list.size();i++)
        {
            j=list.get(i);
            if (compare(j,pivot)<0)
                smaller.add(j);
            else
                greater.add(j);
        }
        smaller=quickSort(smaller);
        greater=quickSort(greater);
        smaller.add(pivot);
        smaller.addAll(greater);
        sorted = smaller;

        return sorted;
    }

    int compare(Prescription obj1, Prescription obj2){
        String ts1 = obj1.getTimestamp();
        String ts2 = obj2.getTimestamp();
        Timestamp timestamp1 = Timestamp.parseTimestamp(ts1);
        Timestamp timestamp2 = Timestamp.parseTimestamp(ts2);
        return timestamp1.compareTo(timestamp2);

    }

}

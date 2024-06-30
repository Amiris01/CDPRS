package com.SmartHealthRemoteSystem.SHSR.ReadSensorData;

import com.SmartHealthRemoteSystem.SHSR.SendDailyHealth.HealthStatus;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.SensorDataService;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.Query.Direction;
import com.google.api.core.ApiFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.List;

@RestController
@RequestMapping("/Sensor-data")
public class ReadSensorDataController {
    private final SensorDataService sensorDataService;
    private final DoctorService doctorService;

    @Autowired
    public ReadSensorDataController(SensorDataService sensorDataService, DoctorService doctorService) {
        this.sensorDataService = sensorDataService;
        this.doctorService = doctorService;
    }

    @PostMapping("/savehistory")
    public String savehistory(Model model, @RequestParam(value= "patientid") String patientId) throws Exception {
        Firestore firestore = FirestoreClient.getFirestore();
        Patient patient = doctorService.getPatient(patientId);
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

        

        // Write the data to the document
        docRef.set(data);


        return "sensorDashboard";
    }
       
    
      
    @GetMapping("/get-sensor-data/{sensorDataId}")
    public SensorData getHealthStatus(@PathVariable String sensorDataId) throws ExecutionException, InterruptedException {

        SensorData sensorData = sensorDataService.getSensorData(sensorDataId);
        if(sensorData != null){
            return sensorData;
            //display patient data on the web
        }else{
            return null;
            //display error message
        }
    }
//trying
    @PutMapping("/update-sensor-data")
    public void updateSensorData(@RequestBody SensorData sensorData) throws ExecutionException, InterruptedException {
        String timeUpdated = sensorDataService.updateSensorData(sensorData);
    }

    @DeleteMapping("/delete-sensor-data/{sensorDataId}")
    public void deleteHealthStatus(@PathVariable String sensorDataId) throws ExecutionException, InterruptedException {
        sensorDataService.deleteSensorData(sensorDataId);
    }
    
    
}

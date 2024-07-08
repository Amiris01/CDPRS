package com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis;

import com.SmartHealthRemoteSystem.SHSR.Repository.SubCollectionSHSRDAO;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class DiagnosisRepository implements SubCollectionSHSRDAO<Diagnosis>{
  
  public static final String COL_NAME = "Patient";
  public static final String SUB_COL_NAME = "Diagnosis";

  @Override
  public Diagnosis get(String patientId, String diagnosisId) throws ExecutionException, InterruptedException{
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document(diagnosisId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        Diagnosis tempDiagnosis;
        if (document.exists()) {
          tempDiagnosis = document.toObject(Diagnosis.class);
            return tempDiagnosis;
        } else {
            return null;
        }
  }

  @Override
  public List<Diagnosis> getAll(String patientId) throws ExecutionException, InterruptedException{
    Firestore dbFirestore = FirestoreClient.getFirestore();
    Iterable<DocumentReference> documentReference = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).listDocuments();
    Iterator<DocumentReference> iterator = documentReference.iterator();

    List<Diagnosis> diagnosisList = new ArrayList<>();
    Diagnosis diagnosis;
    while (iterator.hasNext()){
      DocumentReference documentReference1=iterator.next();
      ApiFuture<DocumentSnapshot> future = documentReference1.get();
      DocumentSnapshot document = future.get();
      diagnosis = document.toObject(Diagnosis.class);
      diagnosisList.add(diagnosis);
    }

    return diagnosisList;
  }

  @Override
  public String save(Diagnosis diagnosis, String patientId) throws ExecutionException, InterruptedException{
    Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference addedDocRef = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document();
        diagnosis.setDiagnosisId(addedDocRef.getId());
        ApiFuture<WriteResult> collectionsApiFuture = addedDocRef.set(diagnosis);
        ApiFuture<WriteResult> writeResult = addedDocRef.update("timestamp", collectionsApiFuture.get().getUpdateTime().toString());
        return collectionsApiFuture.get().getUpdateTime().toString();
  }

  @Override
  public String update(Diagnosis diagnosis, String patientId) throws ExecutionException, InterruptedException{
    Firestore dbFirestore = FirestoreClient.getFirestore();
    DocumentReference addedDocRef = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document(diagnosis.getDiagnosisId());
    ApiFuture<WriteResult> collectionsApiFuture = addedDocRef.update("diagnosisConfirmation", diagnosis.getDiagnosisConfirmation());
    addedDocRef.update("diagnosis", diagnosis.getDiagnosis());
    addedDocRef.update("remarks", diagnosis.getRemarks());
    ApiFuture<WriteResult> writeResult = addedDocRef.update("timestamp", collectionsApiFuture.get().getUpdateTime().toString());
    return collectionsApiFuture.get().getUpdateTime().toString();
  }

  @Override
  public String delete(String patientId, String diagnosisId) throws ExecutionException, InterruptedException{
    Firestore dbFirestore = FirestoreClient.getFirestore();
        if(get(patientId, diagnosisId) == null){
            return "Diagnosis with Id"  + diagnosisId +  "is not exist.";
        }
        ApiFuture<WriteResult> writeResult = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document(diagnosisId).delete();
        return "Document with Diagnosis Id " + diagnosisId + " has been deleted";
  }
}

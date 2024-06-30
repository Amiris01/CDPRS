// package com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis;

// import com.google.api.core.ApiFuture;
// import com.google.cloud.firestore.*;
// import com.google.firebase.cloud.FirestoreClient;
// import org.springframework.stereotype.Repository;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.ExecutionException;

// @Repository 
// public class DiagnosisRepository {
 
//     public static final String COL_NAME = "Diagnoses"; // Name of the diagnosis collection

//     // Save a diagnosis to Firestore
//     public Diagnosis save(Diagnosis diagnosis) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
        
//         // Generate a new ID only if the patientId is not present
//         String documentId = diagnosis.getPatientId() != null && !diagnosis.getPatientId().isEmpty() 
//                             ? diagnosis.getPatientId() 
//                             : dbFirestore.collection(COL_NAME).document().getId();

//         // Set the document in the collection with the specified ID
//         ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COL_NAME)
//         .document(documentId)
//         .set(diagnosis);
//         collectionsApiFuture.get();
//         diagnosis.setId(documentId); // Set the generated document ID to the diagnosis object
//         return diagnosis; // Return the saved diagnosis object
//     }

//     // Retrieve all diagnoses from Firestore
//     public List<Diagnosis> findAll() throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         ApiFuture<QuerySnapshot> querySnapshotApiFuture = dbFirestore.collection(COL_NAME).get();
        
//         List<Diagnosis> diagnoses = new ArrayList<>();
//         QuerySnapshot querySnapshot = querySnapshotApiFuture.get(); // Block and wait for the query to complete
        
//         for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//             Diagnosis diagnosis = document.toObject(Diagnosis.class);
//             if (diagnosis != null) { // Check for null to avoid adding null to the list
//                 diagnoses.add(diagnosis);
//             }
//         }
        
//         return diagnoses;
//     }

    
// }
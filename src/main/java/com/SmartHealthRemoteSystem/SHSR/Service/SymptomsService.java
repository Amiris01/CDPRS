package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.Symptoms.Symptoms;
import com.SmartHealthRemoteSystem.SHSR.Symptoms.SymptomsRepository;
import com.SmartHealthRemoteSystem.SHSR.Repository.SHSRDAO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class SymptomsService {

    private final SHSRDAO<Symptoms> SymptomsRepository;

    public SymptomsService() {
      SymptomsRepository=new SymptomsRepository();
      }
  
      @Autowired
      public SymptomsService(SHSRDAO<Symptoms> SymptomsRepository) {
          this.SymptomsRepository = SymptomsRepository;
      }
  
      public List<Symptoms> getSymptomsList() throws ExecutionException, InterruptedException {
          List<Symptoms> allSymptoms = SymptomsRepository.getAll();
          return allSymptoms;
      }
  
      private static final String CSV_FILE_PATH = "src/main/resources/Notebooks/Symptom-severity.csv";
      private Map<String, Integer> symptomWeights;
  
      @PostConstruct
      public void init() throws IOException {
          symptomWeights = new HashMap<>();
          try (Reader in = new FileReader(CSV_FILE_PATH)) {
              Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(in);
              for (CSVRecord record : records) {
                  String symptom = record.get("Symptom").trim();
                  int weight = Integer.parseInt(record.get("weight").trim());
                  symptomWeights.put(symptom, weight);
              }
          }
      }
  
      public int getSymptomWeight(String symptom) {
          return symptomWeights.getOrDefault(symptom, 0);
      }

    // CRUD operations
    public Symptoms getSymptom(String id) throws ExecutionException, InterruptedException {
        return SymptomsRepository.get(id);
    }

    public List<Symptoms> getAllSymptoms() throws ExecutionException, InterruptedException {
        return SymptomsRepository.getAll();
    }

    public String saveSymptom(Symptoms symptom) throws ExecutionException, InterruptedException {
        return SymptomsRepository.save(symptom);
    }

    public String updateSymptom(Symptoms symptom) throws ExecutionException, InterruptedException {
        return SymptomsRepository.update(symptom);
    }

    public String deleteSymptom(String id) throws ExecutionException, InterruptedException {
        return SymptomsRepository.delete(id);
    }
}

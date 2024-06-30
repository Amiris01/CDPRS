package com.SmartHealthRemoteSystem.SHSR.Prediction;

import com.SmartHealthRemoteSystem.SHSR.Service.SymptomsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
public class PredictionRestController {

    @Autowired
    private SymptomsService symptomService;
    
    public PredictionRestController(SymptomsService symptomService){
        this.symptomService=symptomService;
    }

    @PostMapping("/apicall")
    public ResponseEntity<String> callDjangoAPI(@RequestParam("symptom[]") List<String> symptoms) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Construct the request body
            List<Integer> symptomValues = new ArrayList<>();
            for (String symptom : symptoms) {
                symptomValues.add(symptomService.getSymptomWeight(symptom));
            }

            Map<String, List<Integer>> requestBody = new HashMap<>();
            requestBody.put("symptoms", symptomValues);

            // Make a POST request to the Django API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity("http://127.0.0.1:8000/status/", new HttpEntity<>(requestBody, headers), String.class);

            // Return the response from the Django API
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Handle exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    
}

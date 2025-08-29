package com.example.bajajtask;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BajajTaskApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BajajTaskApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        // STEP 1: Generate Webhook
        String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Aastha");
        requestBody.put("regNo", "22BCE0026");
        requestBody.put("email", "aerohika@gmail.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                generateWebhookUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        String webhookUrl = (String) response.getBody().get("webhook");
        String accessToken = (String) response.getBody().get("accessToken");

        System.out.println("Generated Webhook: " + webhookUrl);
        System.out.println("Access Token: " + accessToken);

        // STEP 2: Final SQL query (Question 2)
        String finalQuery = "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, " +
                "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
                "FROM EMPLOYEE e1 " +
                "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID " +
                "LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT " +
                "AND e2.DOB > e1.DOB " +
                "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME " +
                "ORDER BY e1.EMP_ID DESC;";

        Map<String, String> answerBody = new HashMap<>();
        answerBody.put("finalQuery", finalQuery);

        HttpHeaders answerHeaders = new HttpHeaders();
        answerHeaders.setContentType(MediaType.APPLICATION_JSON);
        answerHeaders.setBearerAuth(accessToken);

        HttpEntity<Map<String, String>> answerEntity = new HttpEntity<>(answerBody, answerHeaders);

        ResponseEntity<String> answerResponse = restTemplate.exchange(
                webhookUrl,
                HttpMethod.POST,
                answerEntity,
                String.class
        );

        System.out.println("Submission Response: " + answerResponse.getBody());
    }
}

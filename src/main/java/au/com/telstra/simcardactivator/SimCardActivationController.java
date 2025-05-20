package au.com.telstra.simcardactivator;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/sim")
public class SimCardActivationController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/activate")
    public ResponseEntity<String> activateSim(@RequestBody Map<String, String> request) {
        String iccid = request.get("iccid");
        String customerEmail = request.get("customerEmail");

        if (iccid == null || customerEmail == null) {
            return ResponseEntity.badRequest().body("Missing 'iccid' or 'customerEmail'");
        }

        String actuatorUrl = "http://localhost:8444/actuate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> actuatorRequest = new HttpEntity<>(Map.of("iccid", iccid), headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(actuatorUrl, actuatorRequest, Map.class);
            boolean success = Boolean.TRUE.equals(response.getBody().get("success"));

            String message = success
                    ? "Activation successful for ICCID: " + iccid
                    : "Activation failed for ICCID: " + iccid;

            System.out.println(message);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to contact actuator service");
        }
    }
}

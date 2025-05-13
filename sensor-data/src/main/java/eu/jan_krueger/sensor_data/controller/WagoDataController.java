package eu.jan_krueger.sensor_data.controller;

import eu.jan_krueger.sensor_data.model.WagoData;
import eu.jan_krueger.sensor_data.repository.WagoDataRepository;
import eu.jan_krueger.sensor_data.service.WagoControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wago")
public class WagoDataController {
    
    private static final Logger logger = LoggerFactory.getLogger(WagoDataController.class);
    
    @Autowired
    private WagoDataRepository wagoDataRepository;

    @Autowired
    private WagoControlService wagoControlService;

    @GetMapping("/latest")
    public ResponseEntity<WagoData> getLatestData() {
        WagoData latestData = wagoDataRepository.findTopByOrderByTimestampDesc();
        if (latestData == null) {
            logger.warn("No Wago data found in database");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(latestData);
    }

    @PostMapping("/control")
    public ResponseEntity<Void> sendControlCommand(@RequestParam int command) {
        logger.info("Received control command: {}", command);
        
        // Validate command value
        if (command < 0 || command > 3) {
            logger.warn("Invalid control command value: {}", command);
            return ResponseEntity.badRequest().build();
        }

        wagoControlService.sendControlCommand(command);
        return ResponseEntity.ok().build();
    }
} 
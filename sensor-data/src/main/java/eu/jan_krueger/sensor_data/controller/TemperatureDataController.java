package eu.jan_krueger.sensor_data.controller;

import eu.jan_krueger.sensor_data.model.TemperatureData;
import eu.jan_krueger.sensor_data.repository.TemperatureDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/temperature")
public class TemperatureDataController {
    
    private static final Logger logger = LoggerFactory.getLogger(TemperatureDataController.class);
    
    @Autowired
    private TemperatureDataRepository temperatureDataRepository;

    @GetMapping("/ist")
    public ResponseEntity<TemperatureData> getLatestIstValue() {
        List<TemperatureData> latestData = temperatureDataRepository.findTopIstValueByOrderByTimestampDesc();
        if (latestData.isEmpty()) {
            logger.warn("No Ist temperature data found");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(latestData.get(0));
    }

    @GetMapping("/soll")
    public ResponseEntity<TemperatureData> getLatestSollValue() {
        List<TemperatureData> latestData = temperatureDataRepository.findTopSollValueByOrderByTimestampDesc();
        if (latestData.isEmpty()) {
            logger.warn("No Soll temperature data found");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(latestData.get(0));
    }

    @GetMapping("/differenz")
    public ResponseEntity<TemperatureData> getLatestDifferenzValue() {
        List<TemperatureData> latestData = temperatureDataRepository.findTopDifferenzValueByOrderByTimestampDesc();
        if (latestData.isEmpty()) {
            logger.warn("No Differenz temperature data found");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(latestData.get(0));
    }

    @GetMapping("/ist/all")
    public ResponseEntity<List<TemperatureData>> getAllIstValues() {
        List<TemperatureData> allData = temperatureDataRepository.findAllIstValues();
        if (allData.isEmpty()) {
            logger.warn("No Ist temperature data found");
            return ResponseEntity.notFound().build();
        }
        logger.info("Returning {} Ist temperature values", allData.size());
        return ResponseEntity.ok(allData);
    }

    @GetMapping("/soll/all")
    public ResponseEntity<List<TemperatureData>> getAllSollValues() {
        List<TemperatureData> allData = temperatureDataRepository.findAllSollValues();
        if (allData.isEmpty()) {
            logger.warn("No Soll temperature data found");
            return ResponseEntity.notFound().build();
        }
        logger.info("Returning {} Soll temperature values", allData.size());
        return ResponseEntity.ok(allData);
    }

    @GetMapping("/differenz/all")
    public ResponseEntity<List<TemperatureData>> getAllDifferenzValues() {
        List<TemperatureData> allData = temperatureDataRepository.findAllDifferenzValues();
        if (allData.isEmpty()) {
            logger.warn("No Differenz temperature data found");
            return ResponseEntity.notFound().build();
        }
        logger.info("Returning {} Differenz temperature values", allData.size());
        return ResponseEntity.ok(allData);
    }
} 
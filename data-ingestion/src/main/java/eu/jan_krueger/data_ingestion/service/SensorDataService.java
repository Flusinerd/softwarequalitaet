package eu.jan_krueger.data_ingestion.service;

import eu.jan_krueger.data_ingestion.model.SensorData;
import eu.jan_krueger.data_ingestion.model.WagoData;
import eu.jan_krueger.data_ingestion.model.SiemensData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;

@Service
public class SensorDataService {
    private static final Logger logger = LoggerFactory.getLogger(SensorDataService.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
        String payload = message.getPayload().toString();
        logger.info("Received MQTT message - Topic: {}, Payload: {}", topic, payload);

        try {
            SensorData sensorData = createSensorData(topic, payload);
            if (sensorData != null) {
                try {
                    mongoTemplate.save(sensorData);
                    logger.info("Saved sensor data to MongoDB - Topic: {}", topic);
                } catch (DuplicateKeyException e) {
                    logger.debug("Duplicate sensor data entry - Topic: {}, Payload: {}", topic, payload);
                }
            } else {
                logger.debug("No sensor data created for topic: {}", topic);
            }
        } catch (Exception e) {
            logger.error("Error processing MQTT message - Topic: {}, Payload: {}, Error: {}", topic, payload, e.getMessage());
        }
    }

    private SensorData createSensorData(String topic, String payload) throws Exception {
        switch (topic) {
            case "Wago750/Status":
                logger.debug("Processing Wago status data");
                WagoData wagoData = new WagoData();
                String cleanPayload = payload.replaceAll("[\\[\\]]", "");
                wagoData.setStatus(Integer.parseInt(cleanPayload));
                wagoData.setTopic(topic);
                return wagoData;

            case "S7_1500/Temperatur/Ist":
            case "S7_1500/Temperatur/Soll":
            case "S7_1500/Temperatur/Differenz":
                logger.debug("Processing Siemens temperature data - Topic: {}", topic);
                SiemensData siemensData = new SiemensData();
                double value = Double.parseDouble(payload);
                switch (topic) {
                    case "S7_1500/Temperatur/Ist":
                        siemensData.setIstTemperatur(value);
                        break;
                    case "S7_1500/Temperatur/Soll":
                        siemensData.setSollTemperatur(value);
                        break;
                    case "S7_1500/Temperatur/Differenz":
                        siemensData.setDifferenzTemperatur(value);
                        break;
                }
                siemensData.setTopic(topic);
                return siemensData;

            case "Random/Integer":
                logger.debug("Received test data - Topic: {}", topic);
                return null;

            default:
                logger.debug("Unknown topic: {}", topic);
                return null;
        }
    }
} 
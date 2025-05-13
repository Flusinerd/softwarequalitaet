package eu.jan_krueger.data_ingestion.service;

import eu.jan_krueger.data_ingestion.model.SensorData;
import eu.jan_krueger.data_ingestion.model.WagoData;
import eu.jan_krueger.data_ingestion.model.SiemensData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;

@Service
public class SensorDataService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
        String payload = message.getPayload().toString();

        try {
            SensorData sensorData = createSensorData(topic, payload);
            if (sensorData != null) {
                try {
                    mongoTemplate.save(sensorData);
                } catch (DuplicateKeyException e) {
                    // Ignore duplicate entries (same timestamp and topic)
                    // This effectively drops the new value
                }
            }
        } catch (Exception e) {
            // Log error and handle appropriately
            e.printStackTrace();
        }
    }

    private SensorData createSensorData(String topic, String payload) throws Exception {
        switch (topic) {
            case "Wago750/Status":
                WagoData wagoData = new WagoData();
                // Remove brackets and parse the integer
                String cleanPayload = payload.replaceAll("[\\[\\]]", "");
                wagoData.setStatus(Integer.parseInt(cleanPayload));
                wagoData.setTopic(topic);
                return wagoData;

            case "S7_1500/Temperatur/Ist":
            case "S7_1500/Temperatur/Soll":
            case "S7_1500/Temperatur/Differenz":
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
                // Handle test data if needed
                return null;

            default:
                return null;
        }
    }
} 
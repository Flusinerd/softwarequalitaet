package eu.jan_krueger.sensor_data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "sensor-data")
public class TemperatureData {
    
    @Id
    private String id;
    private String topic;
    private double istTemperatur;
    private double sollTemperatur;
    private double differenzTemperatur;
    private Instant timestamp;

    public TemperatureData() {
        this.timestamp = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public double getIstTemperatur() {
        return istTemperatur;
    }

    public void setIstTemperatur(double istTemperatur) {
        this.istTemperatur = istTemperatur;
    }

    public double getSollTemperatur() {
        return sollTemperatur;
    }

    public void setSollTemperatur(double sollTemperatur) {
        this.sollTemperatur = sollTemperatur;
    }

    public double getDifferenzTemperatur() {
        return differenzTemperatur;
    }

    public void setDifferenzTemperatur(double differenzTemperatur) {
        this.differenzTemperatur = differenzTemperatur;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}   
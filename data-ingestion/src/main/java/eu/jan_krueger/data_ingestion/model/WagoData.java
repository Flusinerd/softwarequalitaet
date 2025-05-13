package eu.jan_krueger.data_ingestion.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sensor-data")
public class WagoData extends SensorData {
    private Integer status; // 2-byte binary array representing light states

    public WagoData() {
        super();
        this.source = "Wago750";
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
} 
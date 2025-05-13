package eu.jan_krueger.data_ingestion.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sensor-data")
public class SiemensData extends SensorData {
    private Double istTemperatur;
    private Double sollTemperatur;
    private Double differenzTemperatur;

    public SiemensData() {
        super();
        this.source = "SiemensS7";
    }

    public Double getIstTemperatur() {
        return istTemperatur;
    }

    public void setIstTemperatur(Double istTemperatur) {
        this.istTemperatur = istTemperatur;
    }

    public Double getSollTemperatur() {
        return sollTemperatur;
    }

    public void setSollTemperatur(Double sollTemperatur) {
        this.sollTemperatur = sollTemperatur;
    }

    public Double getDifferenzTemperatur() {
        return differenzTemperatur;
    }

    public void setDifferenzTemperatur(Double differenzTemperatur) {
        this.differenzTemperatur = differenzTemperatur;
    }
} 
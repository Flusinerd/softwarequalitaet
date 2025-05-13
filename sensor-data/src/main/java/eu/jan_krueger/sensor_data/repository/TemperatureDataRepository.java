package eu.jan_krueger.sensor_data.repository;

import eu.jan_krueger.sensor_data.model.TemperatureData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface TemperatureDataRepository extends MongoRepository<TemperatureData, String> {
    
    @Query(value = "{'topic': 'S7_1500/Temperatur/Ist', 'istTemperatur': {$ne: null}}", sort = "{'timestamp': -1}")
    List<TemperatureData> findTopIstValueByOrderByTimestampDesc();
    
    @Query(value = "{'topic': 'S7_1500/Temperatur/Soll', 'sollTemperatur': {$ne: null}}", sort = "{'timestamp': -1}")
    List<TemperatureData> findTopSollValueByOrderByTimestampDesc();
    
    @Query(value = "{'topic': 'S7_1500/Temperatur/Differenz', 'differenzTemperatur': {$ne: null}}", sort = "{'timestamp': -1}")
    List<TemperatureData> findTopDifferenzValueByOrderByTimestampDesc();

    @Query(value = "{'topic': 'S7_1500/Temperatur/Ist'}", sort = "{'timestamp': -1}")
    List<TemperatureData> findAllIstValues();
    
    @Query(value = "{'topic': 'S7_1500/Temperatur/Soll'}", sort = "{'timestamp': -1}")
    List<TemperatureData> findAllSollValues();
    
    @Query(value = "{'topic': 'S7_1500/Temperatur/Differenz'}", sort = "{'timestamp': -1}")
    List<TemperatureData> findAllDifferenzValues();
} 
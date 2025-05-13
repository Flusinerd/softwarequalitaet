package eu.jan_krueger.sensor_data.repository;

import eu.jan_krueger.sensor_data.model.WagoData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface WagoDataRepository extends MongoRepository<WagoData, String> {
    
    @Query(sort = "{'timestamp': -1}")
    WagoData findTopByOrderByTimestampDesc();
} 
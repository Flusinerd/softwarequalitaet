package eu.jan_krueger.sensor_data.integration;

import eu.jan_krueger.sensor_data.model.TemperatureData;
import eu.jan_krueger.sensor_data.repository.TemperatureDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TemperatureDataIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TemperatureDataRepository temperatureDataRepository;

    @BeforeEach
    void setUp() {
        temperatureDataRepository.deleteAll();
    }

    @Test
    void testTemperatureDataFlow() throws Exception {
        // Simulate receiving a temperature message
        TemperatureData testData = new TemperatureData();
        testData.setTopic("S7_1500/Temperatur/Ist");
        testData.setIstTemperatur(25.5);
        testData.setTimestamp(Instant.now());
        
        // Save to database
        temperatureDataRepository.save(testData);

        // Verify data is retrievable via REST API
        mockMvc.perform(get("/api/temperature/ist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.istTemperatur").value(25.5));

        // Verify data is in database
        List<TemperatureData> savedData = temperatureDataRepository.findAllIstValues();
        assertNotNull(savedData);
        assertEquals(1, savedData.size());
        assertEquals(25.5, savedData.get(0).getIstTemperatur());
    }

    @Test
    void testMultipleTemperatureDataFlow() throws Exception {
        // Simulate receiving multiple temperature messages
        TemperatureData testData1 = new TemperatureData();
        testData1.setTopic("S7_1500/Temperatur/Ist");
        testData1.setIstTemperatur(25.5);
        testData1.setTimestamp(Instant.now());

        TemperatureData testData2 = new TemperatureData();
        testData2.setTopic("S7_1500/Temperatur/Ist");
        testData2.setIstTemperatur(26.0);
        testData2.setTimestamp(Instant.now());

        // Save to database
        temperatureDataRepository.save(testData1);
        temperatureDataRepository.save(testData2);

        // Verify data is retrievable via REST API
        mockMvc.perform(get("/api/temperature/ist/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].istTemperatur").value(25.5))
                .andExpect(jsonPath("$[1].istTemperatur").value(26.0));

        // Verify data is in database
        List<TemperatureData> savedData = temperatureDataRepository.findAllIstValues();
        assertNotNull(savedData);
        assertEquals(2, savedData.size());
    }
} 
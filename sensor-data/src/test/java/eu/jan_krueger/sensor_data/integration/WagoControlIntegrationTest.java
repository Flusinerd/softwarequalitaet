package eu.jan_krueger.sensor_data.integration;

import eu.jan_krueger.sensor_data.service.WagoControlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class WagoControlIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        // Mock MQTT configuration for testing
        registry.add("mqtt.broker.url", () -> "tcp://localhost:1883");
        registry.add("mqtt.client.id", () -> "test-client");
        registry.add("mqtt.username", () -> "test-user");
        registry.add("mqtt.password", () -> "test-password");
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WagoControlService wagoControlService;

    @Test
    void testControlCommandFlow() throws Exception {
        // Test valid command
        mockMvc.perform(post("/api/wago/control")
                .param("command", "1"))
                .andExpect(status().isOk());

        // Verify that the service was called with the correct command
        verify(wagoControlService).sendControlCommand(1);
    }

    @Test
    void testInvalidControlCommand() throws Exception {
        // Test invalid command (out of range)
        mockMvc.perform(post("/api/wago/control")
                .param("command", "5"))
                .andExpect(status().isBadRequest());

        // Verify that the service was not called
        verify(wagoControlService, org.mockito.Mockito.never()).sendControlCommand(5);
    }

    @Test
    void testNegativeControlCommand() throws Exception {
        // Test negative command
        mockMvc.perform(post("/api/wago/control")
                .param("command", "-1"))
                .andExpect(status().isBadRequest());

        // Verify that the service was not called
        verify(wagoControlService, org.mockito.Mockito.never()).sendControlCommand(-1);
    }
} 
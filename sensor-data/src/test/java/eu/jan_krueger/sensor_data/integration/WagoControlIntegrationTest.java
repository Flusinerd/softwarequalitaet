package eu.jan_krueger.sensor_data.integration;

import eu.jan_krueger.sensor_data.service.WagoControlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
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

    @Autowired
    private WagoControlService wagoControlService;

    @MockitoBean
    private MqttPahoMessageHandler mqttOutbound;

    @Test
    void testControlCommandFlow() throws Exception {
        // Test valid command
        mockMvc.perform(post("/api/wago/control")
                .param("command", "1"))
                .andExpect(status().isOk());

        // Verify that the MQTT message handler was called with the correct message
        verify(mqttOutbound).handleMessage(any(Message.class));
    }

    @Test
    void testValidControlCommands() throws Exception {
        // Test all valid commands (0-3)
        for (int command = 0; command <= 3; command++) {
            mockMvc.perform(post("/api/wago/control")
                    .param("command", String.valueOf(command)))
                    .andExpect(status().isOk());
        }

        // Verify MQTT handler was called 4 times (once for each valid command)
        verify(mqttOutbound, org.mockito.Mockito.times(4)).handleMessage(any(Message.class));
    }

    @Test
    void testInvalidControlCommand() throws Exception {
        // Test invalid command (out of range)
        mockMvc.perform(post("/api/wago/control")
                .param("command", "5"))
                .andExpect(status().isBadRequest());

        // Verify that the MQTT handler was not called
        verify(mqttOutbound, never()).handleMessage(any(Message.class));
    }

    @Test
    void testNegativeControlCommand() throws Exception {
        // Test negative command
        mockMvc.perform(post("/api/wago/control")
                .param("command", "-1"))
                .andExpect(status().isBadRequest());

        // Verify that the MQTT handler was not called
        verify(mqttOutbound, never()).handleMessage(any(Message.class));
    }

    @Test
    void testServiceDirectly() throws Exception {
        // Test the service directly to ensure MQTT integration
        wagoControlService.sendControlCommand(2);

        // Verify that the MQTT message handler was called
        verify(mqttOutbound).handleMessage(any(Message.class));
    }

    @Test
    void testMqttMessageContent() throws Exception {
        // Test that the correct MQTT message is sent with proper topic and payload
        mockMvc.perform(post("/api/wago/control")
                .param("command", "3"))
                .andExpect(status().isOk());

        // Verify that the MQTT handler was called with correct topic and payload
        verify(mqttOutbound).handleMessage(argThat(message -> {
            String topic = (String) message.getHeaders().get(MqttHeaders.TOPIC);
            String payload = (String) message.getPayload();
            return "Wago750/Control".equals(topic) && "3".equals(payload);
        }));
    }

    @Test
    void testMqttMessageContentForDifferentCommands() throws Exception {
        // Test different commands produce correct payloads
        wagoControlService.sendControlCommand(0);
        wagoControlService.sendControlCommand(1);

        // Verify first message
        verify(mqttOutbound).handleMessage(argThat(message -> {
            String topic = (String) message.getHeaders().get(MqttHeaders.TOPIC);
            String payload = (String) message.getPayload();
            return "Wago750/Control".equals(topic) && "0".equals(payload);
        }));

        // Verify second message  
        verify(mqttOutbound).handleMessage(argThat(message -> {
            String topic = (String) message.getHeaders().get(MqttHeaders.TOPIC);
            String payload = (String) message.getPayload();
            return "Wago750/Control".equals(topic) && "1".equals(payload);
        }));
    }
} 
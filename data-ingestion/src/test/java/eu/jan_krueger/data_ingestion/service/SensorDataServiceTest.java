package eu.jan_krueger.data_ingestion.service;

import eu.jan_krueger.data_ingestion.model.WagoData;
import eu.jan_krueger.data_ingestion.model.SiemensData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.dao.DuplicateKeyException;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SensorDataServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private SensorDataService sensorDataService;

    private Message<?> message;
    private Map<String, Object> headers;

    @BeforeEach
    void setUp() {
        headers = new HashMap<>();
    }

    @Test
    void handleMessage_WagoStatus_Success() {
        // Arrange
        headers.put("mqtt_receivedTopic", "Wago750/Status");
        message = new TestMessage("[1]", new MessageHeaders(headers));

        // Act
        sensorDataService.handleMessage(message);

        // Assert
        verify(mongoTemplate, times(1)).save(any(WagoData.class));
    }

    @Test
    void handleMessage_SiemensIstTemperatur_Success() {
        // Arrange
        headers.put("mqtt_receivedTopic", "S7_1500/Temperatur/Ist");
        message = new TestMessage("25.5", new MessageHeaders(headers));

        // Act
        sensorDataService.handleMessage(message);

        // Assert
        verify(mongoTemplate, times(1)).save(any(SiemensData.class));
    }

    @Test
    void handleMessage_SiemensSollTemperatur_Success() {
        // Arrange
        headers.put("mqtt_receivedTopic", "S7_1500/Temperatur/Soll");
        message = new TestMessage("30.0", new MessageHeaders(headers));

        // Act
        sensorDataService.handleMessage(message);

        // Assert
        verify(mongoTemplate, times(1)).save(any(SiemensData.class));
    }

    @Test
    void handleMessage_SiemensDifferenzTemperatur_Success() {
        // Arrange
        headers.put("mqtt_receivedTopic", "S7_1500/Temperatur/Differenz");
        message = new TestMessage("-4.5", new MessageHeaders(headers));

        // Act
        sensorDataService.handleMessage(message);

        // Assert
        verify(mongoTemplate, times(1)).save(any(SiemensData.class));
    }

    @Test
    void handleMessage_RandomInteger_NoSave() {
        // Arrange
        headers.put("mqtt_receivedTopic", "Random/Integer");
        message = new TestMessage("42", new MessageHeaders(headers));

        // Act
        sensorDataService.handleMessage(message);

        // Assert
        verify(mongoTemplate, never()).save(any());
    }

    @Test
    void handleMessage_UnknownTopic_NoSave() {
        // Arrange
        headers.put("mqtt_receivedTopic", "Unknown/Topic");
        message = new TestMessage("data", new MessageHeaders(headers));

        // Act
        sensorDataService.handleMessage(message);

        // Assert
        verify(mongoTemplate, never()).save(any());
    }

    @Test
    void handleMessage_DuplicateKey_NoError() {
        // Arrange
        headers.put("mqtt_receivedTopic", "Wago750/Status");
        message = new TestMessage("[1]", new MessageHeaders(headers));
        doThrow(new DuplicateKeyException("Duplicate key")).when(mongoTemplate).save(any());

        // Act & Assert
        assertDoesNotThrow(() -> sensorDataService.handleMessage(message));
    }

    @Test
    void handleMessage_InvalidPayload_NoError() {
        // Arrange
        headers.put("mqtt_receivedTopic", "Wago750/Status");
        message = new TestMessage("invalid", new MessageHeaders(headers));

        // Act & Assert
        assertDoesNotThrow(() -> sensorDataService.handleMessage(message));
    }

    // Helper class for creating test messages
    private static class TestMessage implements Message<String> {
        private final String payload;
        private final MessageHeaders headers;

        public TestMessage(String payload, MessageHeaders headers) {
            this.payload = payload;
            this.headers = headers;
        }

        @Override
        public String getPayload() {
            return payload;
        }

        @Override
        public MessageHeaders getHeaders() {
            return headers;
        }
    }
} 
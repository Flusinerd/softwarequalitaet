package eu.jan_krueger.sensor_data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WagoControlServiceTest {

    @Mock
    private MqttPahoMessageHandler mqttOutbound;

    @InjectMocks
    private WagoControlService wagoControlService;

    @Test
    void sendControlCommand_Success() {
        // Act
        wagoControlService.sendControlCommand(1);

        // Assert
        verify(mqttOutbound, times(1)).handleMessage(any(Message.class));
    }

    @Test
    void sendControlCommand_ZeroValue_Success() {
        // Act
        wagoControlService.sendControlCommand(0);

        // Assert
        verify(mqttOutbound, times(1)).handleMessage(any(Message.class));
    }

    @Test
    void sendControlCommand_MaxValue_Success() {
        // Act
        wagoControlService.sendControlCommand(3);

        // Assert
        verify(mqttOutbound, times(1)).handleMessage(any(Message.class));
    }
} 
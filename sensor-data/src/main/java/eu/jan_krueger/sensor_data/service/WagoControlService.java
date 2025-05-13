package eu.jan_krueger.sensor_data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WagoControlService {
    
    private static final Logger logger = LoggerFactory.getLogger(WagoControlService.class);
    private static final String CONTROL_TOPIC = "Wago750/Control";
    
    @Autowired
    private MqttPahoMessageHandler mqttOutbound;
    
    public void sendControlCommand(int command) {
        logger.info("Sending control command {} to Wago PLC", command);
        
        Message<?> message = MessageBuilder
            .withPayload(String.valueOf(command))
            .setHeader(MqttHeaders.TOPIC, CONTROL_TOPIC)
            .build();
            
        mqttOutbound.handleMessage(message);
    }
} 
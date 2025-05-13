package eu.jan_krueger.data_ingestion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

@Configuration
public class MqttErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(MqttErrorHandler.class);

    @Bean
    @ServiceActivator(inputChannel = "mqttErrorChannel")
    public MessageHandler errorHandler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(@NonNull Message<?> message) {
                logger.error("MQTT Error: {}", message.getPayload());
            }
        };
    }
} 
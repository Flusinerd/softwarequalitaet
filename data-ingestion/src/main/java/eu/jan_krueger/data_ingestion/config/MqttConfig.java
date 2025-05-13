package eu.jan_krueger.data_ingestion.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;

@Configuration
public class MqttConfig {
    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        
        options.setServerURIs(new String[] { brokerUrl });
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);
        
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound(
            MqttPahoClientFactory mqttClientFactory,
            MessageChannel mqttInputChannel) {
        
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId, mqttClientFactory,
                        "Wago750/Status",
                        "Wago750/Control",
                        "S7_1500/Temperatur/Ist",
                        "S7_1500/Temperatur/Soll",
                        "S7_1500/Temperatur/Differenz",
                        "Random/Integer");

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel);
        
        adapter.setErrorChannelName("mqttErrorChannel");
        
        return adapter;
    }

    @Bean
    public MqttPahoMessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId + "_outbound", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(1);
        return messageHandler;
    }
} 
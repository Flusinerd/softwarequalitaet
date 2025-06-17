package eu.jan_krueger.sensor_data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SensorDataApplication {
	private static final Logger logger = LoggerFactory.getLogger(SensorDataApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Sensor Data Application");
		SpringApplication.run(SensorDataApplication.class, args);
		logger.info("Sensor Data Application started successfully");
	}

}

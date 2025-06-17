package eu.jan_krueger.data_ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class DataIngestionApplication {
    private static final Logger logger = LoggerFactory.getLogger(DataIngestionApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Data Ingestion Application");
        SpringApplication.run(DataIngestionApplication.class, args);
        logger.info("Data Ingestion Application started successfully");
    }
}

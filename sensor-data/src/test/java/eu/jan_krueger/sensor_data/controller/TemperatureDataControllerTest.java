package eu.jan_krueger.sensor_data.controller;

import eu.jan_krueger.sensor_data.model.TemperatureData;
import eu.jan_krueger.sensor_data.repository.TemperatureDataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TemperatureDataController.class)
class TemperatureDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TemperatureDataRepository temperatureDataRepository;

    @Test
    void getLatestIstValue_Success() throws Exception {
        // Arrange
        TemperatureData mockData = createMockTemperatureData(25.5, null, null);
        when(temperatureDataRepository.findTopIstValueByOrderByTimestampDesc())
                .thenReturn(Collections.singletonList(mockData));

        // Act & Assert
        mockMvc.perform(get("/api/temperature/ist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.istTemperatur").value(25.5));
    }

    @Test
    void getLatestSollValue_Success() throws Exception {
        // Arrange
        TemperatureData mockData = createMockTemperatureData(null, 30.0, null);
        when(temperatureDataRepository.findTopSollValueByOrderByTimestampDesc())
                .thenReturn(Collections.singletonList(mockData));

        // Act & Assert
        mockMvc.perform(get("/api/temperature/soll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sollTemperatur").value(30.0));
    }

    @Test
    void getLatestDifferenzValue_Success() throws Exception {
        // Arrange
        TemperatureData mockData = createMockTemperatureData(null, null, -4.5);
        when(temperatureDataRepository.findTopDifferenzValueByOrderByTimestampDesc())
                .thenReturn(Collections.singletonList(mockData));

        // Act & Assert
        mockMvc.perform(get("/api/temperature/differenz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.differenzTemperatur").value(-4.5));
    }

    @Test
    void getAllIstValues_Success() throws Exception {
        // Arrange
        List<TemperatureData> mockDataList = Arrays.asList(
                createMockTemperatureData(25.5, null, null),
                createMockTemperatureData(26.0, null, null)
        );
        when(temperatureDataRepository.findAllIstValues()).thenReturn(mockDataList);

        // Act & Assert
        mockMvc.perform(get("/api/temperature/ist/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].istTemperatur").value(25.5))
                .andExpect(jsonPath("$[1].istTemperatur").value(26.0));
    }

    @Test
    void getAllSollValues_Success() throws Exception {
        // Arrange
        List<TemperatureData> mockDataList = Arrays.asList(
                createMockTemperatureData(null, 30.0, null),
                createMockTemperatureData(null, 31.0, null)
        );
        when(temperatureDataRepository.findAllSollValues()).thenReturn(mockDataList);

        // Act & Assert
        mockMvc.perform(get("/api/temperature/soll/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sollTemperatur").value(30.0))
                .andExpect(jsonPath("$[1].sollTemperatur").value(31.0));
    }

    @Test
    void getAllDifferenzValues_Success() throws Exception {
        // Arrange
        List<TemperatureData> mockDataList = Arrays.asList(
                createMockTemperatureData(null, null, -4.5),
                createMockTemperatureData(null, null, -5.0)
        );
        when(temperatureDataRepository.findAllDifferenzValues()).thenReturn(mockDataList);

        // Act & Assert
        mockMvc.perform(get("/api/temperature/differenz/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].differenzTemperatur").value(-4.5))
                .andExpect(jsonPath("$[1].differenzTemperatur").value(-5.0));
    }

    @Test
    void getLatestIstValue_NotFound() throws Exception {
        // Arrange
        when(temperatureDataRepository.findTopIstValueByOrderByTimestampDesc())
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/temperature/ist"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllIstValues_NotFound() throws Exception {
        // Arrange
        when(temperatureDataRepository.findAllIstValues())
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/temperature/ist/all"))
                .andExpect(status().isNotFound());
    }

    private TemperatureData createMockTemperatureData(Double ist, Double soll, Double differenz) {
        TemperatureData data = new TemperatureData();
        data.setIstTemperatur(ist != null ? ist : 0.0);
        data.setSollTemperatur(soll != null ? soll : 0.0);
        data.setDifferenzTemperatur(differenz != null ? differenz : 0.0);
        data.setTimestamp(Instant.now());
        return data;
    }
} 
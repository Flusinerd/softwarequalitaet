package eu.jan_krueger.sensor_data.controller;

import eu.jan_krueger.sensor_data.model.WagoData;
import eu.jan_krueger.sensor_data.repository.WagoDataRepository;
import eu.jan_krueger.sensor_data.service.WagoControlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WagoDataController.class)
class WagoDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WagoDataRepository wagoDataRepository;

    @MockitoBean
    private WagoControlService wagoControlService;

    @Test
    void getLatestData_Success() throws Exception {
        // Arrange
        WagoData mockData = new WagoData();
        mockData.setStatus(1);
        mockData.setTimestamp(Instant.now());
        when(wagoDataRepository.findTopByOrderByTimestampDesc()).thenReturn(mockData);

        // Act & Assert
        mockMvc.perform(get("/api/wago/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1));
    }

    @Test
    void getLatestData_NotFound() throws Exception {
        // Arrange
        when(wagoDataRepository.findTopByOrderByTimestampDesc()).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/wago/latest"))
                .andExpect(status().isNotFound());
    }

    @Test
    void sendControlCommand_ValidCommand_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/wago/control")
                .param("command", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(wagoControlService, times(1)).sendControlCommand(1);
    }

    @Test
    void sendControlCommand_InvalidCommand_BadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/wago/control")
                .param("command", "4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(wagoControlService, never()).sendControlCommand(anyInt());
    }

    @Test
    void sendControlCommand_NegativeCommand_BadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/wago/control")
                .param("command", "-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(wagoControlService, never()).sendControlCommand(anyInt());
    }
} 
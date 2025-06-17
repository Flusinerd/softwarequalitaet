package eu.jan_krueger.sensor_data.integration;

import eu.jan_krueger.sensor_data.service.WagoControlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WagoControlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WagoControlService wagoControlService;

    @Test
    void testControlCommandFlow() throws Exception {
        // Test valid command
        mockMvc.perform(post("/api/wago/control")
                .param("command", "1"))
                .andExpect(status().isOk());

        // Verify that the service was called with the correct command
        verify(wagoControlService).sendControlCommand(1);
    }

    @Test
    void testInvalidControlCommand() throws Exception {
        // Test invalid command (out of range)
        mockMvc.perform(post("/api/wago/control")
                .param("command", "5"))
                .andExpect(status().isBadRequest());

        // Verify that the service was not called
        verify(wagoControlService, org.mockito.Mockito.never()).sendControlCommand(5);
    }

    @Test
    void testNegativeControlCommand() throws Exception {
        // Test negative command
        mockMvc.perform(post("/api/wago/control")
                .param("command", "-1"))
                .andExpect(status().isBadRequest());

        // Verify that the service was not called
        verify(wagoControlService, org.mockito.Mockito.never()).sendControlCommand(-1);
    }
} 
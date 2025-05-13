package com.ridehailing.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehailing.models.Driver;
import com.ridehailing.services.DriverManager;
import com.ridehailing.services.RideManager;
import com.ridehailing.services.RideService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(DriverController.class)
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriverManager driverManager;

    @MockBean
    private RideManager rideManager;

    @MockBean
    private RideService rideService;

    @Test
    public void testAddDriver() throws Exception {
        Driver driver = new Driver("driver1", 10, 20);

        doNothing().when(driverManager).addDriver(anyString(), anyInt(), anyInt());

        mockMvc.perform(MockMvcRequestBuilders.post("/drivers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driver)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Driver driver1 added successfully."));

        verify(driverManager, times(1)).addDriver("driver1", 10, 20);
    }


    @Test
    public void testGetDriverById() throws Exception {
        Driver driver = new Driver("driver1", 10, 20);

        when(driverManager.getDriverById("driver1")).thenReturn(Optional.of(driver));

        mockMvc.perform(MockMvcRequestBuilders.get("/drivers/driver1"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("driver1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.x").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.y").value(20));

        verify(driverManager, times(1)).getDriverById("driver1");
    }

    @Test
    public void testGetDriverByIdNotFound() throws Exception {
        when(driverManager.getDriverById("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/drivers/nonexistent"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(driverManager, times(1)).getDriverById("nonexistent");
    }

    @Test
    public void testGetAllDrivers() throws Exception {
        Driver driver1 = new Driver("driver1", 10, 20);
        Driver driver2 = new Driver("driver2", 30, 40);

        when(driverManager.getAllDrivers()).thenReturn(Arrays.asList(driver1, driver2));

        mockMvc.perform(MockMvcRequestBuilders.get("/drivers"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("driver1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("driver2"));

        verify(driverManager, times(1)).getAllDrivers();
    }

    @Test
    public void testUpdateAvailability() throws Exception {
        when(driverManager.updateAvailability("driver1", true)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.put("/drivers/driver1/availability")
                        .param("available", "true"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Driver availability updated."));

        verify(driverManager, times(1)).updateAvailability("driver1", true);
    }

    @Test
    public void testUpdateAvailabilityDriverNotFound() throws Exception {
        when(driverManager.updateAvailability("nonexistent", true)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.put("/drivers/nonexistent/availability")
                        .param("available", "true"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(driverManager, times(1)).updateAvailability("nonexistent", true);
    }

    @Test
    public void testDeleteDriver() throws Exception {
        doNothing().when(driverManager).deleteDriver("driver1");

        mockMvc.perform(MockMvcRequestBuilders.delete("/drivers/driver1"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Driver driver1 deleted successfully."));

        verify(driverManager, times(1)).deleteDriver("driver1");
    }


}


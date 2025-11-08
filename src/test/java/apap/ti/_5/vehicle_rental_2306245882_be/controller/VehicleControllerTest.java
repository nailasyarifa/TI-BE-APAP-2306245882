package apap.ti._5.vehicle_rental_2306245882_be.controller;

import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {}

    @Test
    void viewAllVehicles_rendersPage() throws Exception {
        VehicleResponseDTO v = new VehicleResponseDTO("VEH0001","SUV","Brand","Model",7,"Available",100000.0,"B 1","Jakarta",2019,"Auto","Bensin","Vendor A");
        when(vehicleService.getAllVehicles(any(), any(), any())).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(v)));

        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicles/vehicles"))
                .andExpect(model().attributeExists("vehicles"));
    }

    @Test
    void viewVehicleDetail_rendersDetail() throws Exception {
        VehicleResponseDTO v = new VehicleResponseDTO("VEH0001","SUV","Brand","Model",7,"Available",100000.0,"B 1","Jakarta",2019,"Auto","Bensin","Vendor A");
        when(vehicleService.getById("VEH0001")).thenReturn(v);

        mockMvc.perform(get("/vehicles/VEH0001"))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicles/vehicle-detail"))
                .andExpect(model().attributeExists("vehicle"));
    }
}

package apap.ti._5.vehicle_rental_2306245882_be.restcontroller;

import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class VehicleRestControllerTest {

    @Mock VehicleService service;
    @InjectMocks VehicleRestController controller;

    @Test
    void getAll_shouldReturnList() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();
        VehicleResponseDTO v = new VehicleResponseDTO("VEH1","SUV","B","M",5,"Available",100000.0,"B 1","Jakarta",2020,"Automatic","Bensin","Vendor");
        Page<VehicleResponseDTO> page = new PageImpl<>(List.of(v));
        when(service.getAllVehicles(null, null, PageRequest.of(0,20))).thenReturn(page);

        mvc.perform(get("/api/vehicles").param("page","0").param("size","20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("VEH1"));
    }

    @Test
    void getById_shouldReturnVehicle() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();
        VehicleResponseDTO v = new VehicleResponseDTO("VEH2","SUV","B","M",5,"Available",100000.0,"B 1","Jakarta",2020,"Automatic","Bensin","Vendor");
        when(service.getById("VEH2")).thenReturn(v);

        mvc.perform(get("/api/vehicles/VEH2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("VEH2"));
    }
}


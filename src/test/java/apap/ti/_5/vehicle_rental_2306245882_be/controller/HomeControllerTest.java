package apap.ti._5.vehicle_rental_2306245882_be.controller;

import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private RentalVendorRepository rentalVendorRepository;

    @Mock
    private RentalBookingRepository rentalBookingRepository;

    @InjectMocks
    private HomeController homeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Tambahkan view resolver supaya MockMvc dapat resolve nama view "home"
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        // prefix/suffix hanya contoh; tidak perlu sesuai file nyata karena kita hanya butuh resolve nama view
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders
                    .standaloneSetup(homeController)
                    .setViewResolvers(viewResolver)
                    .build();
    }

    @Test
    void testHomeWithData() throws Exception {
        when(vehicleRepository.count()).thenReturn(10L);
        when(rentalVendorRepository.count()).thenReturn(5L);
        when(rentalBookingRepository.count()).thenReturn(20L);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("totalVehicles", 10L))
                .andExpect(model().attribute("totalVendors", 5L))
                .andExpect(model().attribute("totalBookings", 20L));

        verify(vehicleRepository, times(1)).count();
        verify(rentalVendorRepository, times(1)).count();
        verify(rentalBookingRepository, times(1)).count();
    }

    @Test
    void testHomeEndpoint() throws Exception {
        when(vehicleRepository.count()).thenReturn(0L);
        when(rentalVendorRepository.count()).thenReturn(0L);
        when(rentalBookingRepository.count()).thenReturn(0L);

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("totalVehicles", 0L))
                .andExpect(model().attribute("totalVendors", 0L))
                .andExpect(model().attribute("totalBookings", 0L));
    }

    @Test
    void testHomeWithLargeNumbers() throws Exception {
        when(vehicleRepository.count()).thenReturn(999999L);
        when(rentalVendorRepository.count()).thenReturn(888888L);
        when(rentalBookingRepository.count()).thenReturn(777777L);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("totalVehicles", 999999L))
                .andExpect(model().attribute("totalVendors", 888888L))
                .andExpect(model().attribute("totalBookings", 777777L));
    }

    @Test
    void testHomeDirectCall() {
        Model model = mock(Model.class);
        when(vehicleRepository.count()).thenReturn(15L);
        when(rentalVendorRepository.count()).thenReturn(7L);
        when(rentalBookingRepository.count()).thenReturn(25L);

        String viewName = homeController.home(model);

        assertEquals("home", viewName);
        verify(model).addAttribute("totalVehicles", 15L);
        verify(model).addAttribute("totalVendors", 7L);
        verify(model).addAttribute("totalBookings", 25L);
    }
}

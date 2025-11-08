package apap.ti._5.vehicle_rental_2306245882_be.controller;

import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    VehicleRepository vehicleRepository;

    @Mock
    RentalVendorRepository rentalVendorRepository;

    @Mock
    RentalBookingRepository rentalBookingRepository;

    HomeController homeController;

    Model model;

    @BeforeEach
    void setup() {
        homeController = new HomeController(vehicleRepository, rentalVendorRepository, rentalBookingRepository);
        model = new BindingAwareModelMap();
    }

    @Test
    void home_populatesModelAndReturnsView() {
        when(vehicleRepository.count()).thenReturn(42L);
        when(rentalVendorRepository.count()).thenReturn(3L);
        when(rentalBookingRepository.count()).thenReturn(7L);

        String view = homeController.home(model);

        assertEquals("home", view);
        assertEquals(42L, model.getAttribute("totalVehicles"));
        assertEquals(3L, model.getAttribute("totalVendors"));
        assertEquals(7L, model.getAttribute("totalBookings"));

        verify(vehicleRepository).count();
        verify(rentalVendorRepository).count();
        verify(rentalBookingRepository).count();
    }
}

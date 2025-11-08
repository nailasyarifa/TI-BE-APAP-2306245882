package apap.ti._5.vehicle_rental_2306245882_be.controller;

import apap.ti._5.vehicle_rental_2306245882_be.service.RentalBookingService;
import apap.ti._5.vehicle_rental_2306245882_be.service.RentalVendorService;
import apap.ti._5.vehicle_rental_2306245882_be.service.RentalAddOnService;
import apap.ti._5.vehicle_rental_2306245882_be.service.VehicleService;
import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.BookingListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RentalBookingControllerTest {

    @Mock RentalBookingService bookingService;
    @Mock RentalVendorService rentalVendorService;
    @Mock RentalAddOnService addOnService;
    @Mock VehicleService vehicleService;

    @InjectMocks RentalBookingController controller;

    @Test
    void viewAllBookings_rendersBookingsTemplate() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();
        when(bookingService.getAllBookingsForList()).thenReturn(List.of(new BookingListItem()));
        mvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookings/bookings"))
                .andExpect(model().attributeExists("bookings"));
    }
}

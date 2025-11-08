package apap.ti._5.vehicle_rental_2306245882_be.restcontroller;

import apap.ti._5.vehicle_rental_2306245882_be.service.RentalBookingService;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245882_be.service.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RentalBookingRestControllerTest {

    @Mock RentalBookingService bookingService;
    @InjectMocks RentalBookingRestController restController;

    @Test
    void apiCancelBooking_shouldReturnOk_whenSuccess() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(restController).build();
        RentalBooking rb = new RentalBooking(); rb.setId("B1");
        when(bookingService.cancelBooking("B1")).thenReturn(rb);

        mvc.perform(delete("/api/bookings/B1/delete").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.id").value("B1"));
    }

    @Test
    void apiCancelBooking_shouldReturnBadRequest_whenBad() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(restController).build();
        when(bookingService.cancelBooking("X")).thenThrow(new BadRequestException("cannot"));

        mvc.perform(delete("/api/bookings/X/delete").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }
}

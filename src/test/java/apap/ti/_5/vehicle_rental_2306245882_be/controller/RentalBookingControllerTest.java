package apap.ti._5.vehicle_rental_2306245882_be.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245882_be.service.RentalBookingService;
import apap.ti._5.vehicle_rental_2306245882_be.service.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RentalBookingController.class)
class RentalBookingControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private RentalBookingService bookingService;

  private RentalBooking booking;

  @BeforeEach
  void setUp() {
    booking = new RentalBooking();
    booking.setId("VR000001");
    booking.setVehicleId("VEH0001");
    booking.setPickUpTime(LocalDateTime.now().plusDays(1));
    booking.setDropOffTime(LocalDateTime.now().plusDays(3));
    booking.setPickUpLocation("Jakarta");
    booking.setDropOffLocation("Bandung");
    booking.setCapacityNeeded(5);
    booking.setTransmissionNeeded("Automatic");
    booking.setTotalPrice(1000000.0);
    booking.setIncludeDriver(true);
    booking.setStatus(RentalBooking.BookingStatus.Upcoming);
  }
  // @Test
  // void testViewAllBookings() throws Exception {
  //     Page<RentalBooking> page = new PageImpl<>(Arrays.asList(booking));
  //     when(bookingService.getAllBookings(any(Pageable.class))).thenReturn(page);

  //     mockMvc.perform(get("/bookings"))
  //             .andExpect(status().isOk())
  //             .andExpect(view().name("bookings/bookings"))
  //             .andExpect(model().attributeExists("bookings"));
  // }

  // @Test
  // void testViewBookingDetail() throws Exception {
  //     when(bookingService.getById("VR000001")).thenReturn(booking);

  //     mockMvc.perform(get("/bookings/VR000001"))
  //             .andExpect(status().isOk())
  //             .andExpect(view().name("bookings/booking-detail"))
  //             .andExpect(model().attributeExists("booking"));
  // }

  // @Test
  // void testViewBookingDetailNotFound() throws Exception {
  //     when(bookingService.getById("VRXXX")).thenThrow(new ResourceNotFoundException("Not found"));

  //     mockMvc.perform(get("/bookings/VRXXX"))
  //             .andExpect(status().is3xxRedirection())
  //             .andExpect(redirectedUrl("/bookings"));
  // }
}
// @WebMvcTest(HomeController.class)
// class HomeControllerTest {
//     @Autowired
//     private MockMvc mockMvc;
//     @Test
//     void testHomeEndpoint() throws Exception {
//         mockMvc.perform(get("/"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("home"));
//     }
// }

package apap.ti._5.vehicle_rental_2306245882_be.service;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentalBookingServiceTest {

    @Mock
    RentalBookingRepository bookingRepository;

    @Mock
    VehicleRepository vehicleRepository;

    @InjectMocks
    RentalBookingServiceImpl bookingService; 

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void cancelBooking_beforePickup_setsPriceZero_and_deleted_true() {
        RentalBooking b = new RentalBooking();
        b.setId("VR0001");
        b.setStatus(RentalBooking.BookingStatus.UPCOMING);
        b.setPickUpTime(LocalDateTime.now().plusDays(1));
        b.setTotalPrice(500000.0);

        when(bookingRepository.findById("VR0001")).thenReturn(Optional.of(b));
        when(bookingRepository.save(any(RentalBooking.class))).thenAnswer(i -> i.getArgument(0));

        RentalBooking res = bookingService.cancelBooking("VR0001");

        assertThat(res.isDeleted()).isTrue();
        assertThat(res.getTotalPrice()).isEqualTo(0.0);
        assertThat(res.getStatus()).isEqualTo(RentalBooking.BookingStatus.DONE);
        verify(bookingRepository).save(any(RentalBooking.class));
    }

    @Test
    void cancelBooking_notFound_throws() {
        when(bookingRepository.findById("not")).thenReturn(Optional.empty());
        org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> bookingService.cancelBooking("not"));
    }
}

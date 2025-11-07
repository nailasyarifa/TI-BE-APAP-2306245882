package apap.ti._5.vehicle_rental_2306245882_be.repository;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface RentalBookingRepository extends JpaRepository<RentalBooking, String> {
    // check overlaps for vehicle in a period
    @Query("SELECT b FROM RentalBooking b WHERE b.vehicleId = :vehicleId AND " +
           "NOT (b.dropOffTime <= :start OR b.pickUpTime >= :end) AND b.status <> apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking.BookingStatus.DONE")
    List<RentalBooking> findOverlappingBookings(String vehicleId, LocalDateTime start, LocalDateTime end);

    // find active bookings (used earlier in your code)
    @Query("SELECT COUNT(b) > 0 FROM RentalBooking b WHERE b.vehicleId = :vehicleId AND b.status = 'ONGOING'")
    boolean existsActiveBookingForVehicle(String vehicleId);
}

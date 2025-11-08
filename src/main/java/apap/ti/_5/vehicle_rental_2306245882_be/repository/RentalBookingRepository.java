package apap.ti._5.vehicle_rental_2306245882_be.repository;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RentalBookingRepository - improved safety:
 *  - use @Param for explicit param binding
 *  - keep the original findOverlappingBookings signature so existing service calls work
 *  - add a derived existsByVehicleIdAndStatus method (recommended)
 */
public interface RentalBookingRepository extends JpaRepository<RentalBooking, String> {

    /**
     * Cari booking yang overlap dengan interval [start, end).
     * Tetap mengecualikan booking yang sudah DONE di query (sama seperti implementasimu sebelumnya).
     *
     * Logic overlap: NOT (existing.dropOffTime <= start OR existing.pickUpTime >= end)
     */
    @Query("SELECT b FROM RentalBooking b " +
           "WHERE b.vehicleId = :vehicleId " +
           "  AND NOT (b.dropOffTime <= :start OR b.pickUpTime >= :end) " +
           "  AND b.status <> apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking.BookingStatus.DONE")
    List<RentalBooking> findOverlappingBookings(
            @Param("vehicleId") String vehicleId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Derived query: periksa apakah ada booking dengan status tertentu (mis. ONGOING)
     * Ini lebih portable/bersih dibandingkan membuat JPQL boolean COUNT > 0.
     */
    boolean existsByVehicleIdAndStatus(String vehicleId, BookingStatus status);

    /**
     * (Opsional) jika kamu masih ingin method boolean bernama spesifik seperti sebelumnya,
     * kamu bisa gunakan query ini — tapi derived method di atas sudah recommended.
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM RentalBooking b " +
           "WHERE b.vehicleId = :vehicleId AND b.status = apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking.BookingStatus.ONGOING")
    boolean existsActiveBookingForVehicle(@Param("vehicleId") String vehicleId);

    @Query("SELECT b FROM RentalBooking b WHERE b.vehicleId = :vehicleId " +
           "AND (:excludeId IS NULL OR b.id <> :excludeId) " +
           "AND b.pickUpTime < :dropOffTime AND b.dropOffTime > :pickUpTime " +
           "AND b.status <> 'Cancelled'") // adjust status name if different
    List<RentalBooking> findOverlappingBookings(
            @Param("vehicleId") String vehicleId,
            @Param("pickUpTime") LocalDateTime pickUpTime,
            @Param("dropOffTime") LocalDateTime dropOffTime,
            @Param("excludeId") String excludeId
    );
}

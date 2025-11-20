package apap.ti._5.vehicle_rental_2306245882_be.repository;

import apap.ti._5.vehicle_rental_2306245882_be.model.Vehicle;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {
  @Query(
    value = """
        SELECT * FROM vehicle v
        WHERE v.is_deleted = false
          AND (:type IS NULL OR v.type = :type)
          AND (:search IS NULL OR (
               LOWER(v.model::text) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(v.brand::text) LIKE LOWER(CONCAT('%', :search, '%'))
            ))
        ORDER BY v.id
        """,
    nativeQuery = true
  )
  Page<Vehicle> searchVehicles(
    @Param("type") String type,
    @Param("search") String search,
    Pageable pageable
  );

  @Query(
    value = "SELECT v FROM Vehicle v " +
    "WHERE (:pickUpLocation IS NULL OR v.location = :pickUpLocation) " +
    "AND (:transmission IS NULL OR v.transmission = :transmission) " +
    "AND (:capacityNeeded IS NULL OR v.capacity >= :capacityNeeded) " +
    "AND v.status = 'Available' " +
    "AND v.id NOT IN ( " +
    "   SELECT b.vehicleId FROM RentalBooking b " +
    "   WHERE (b.status <> 'Done') " +
    "     AND ( (b.pickUpTime <= :end AND b.dropOffTime >= :start) ) " + // overlap
    ")"
  )
  Page<Vehicle> findAvailableForBooking(
    @Param("pickUpLocation") String pickUpLocation,
    @Param("dropOffLocation") String dropOffLocation,
    @Param("transmission") String transmission,
    @Param("capacityNeeded") Integer capacityNeeded,
    @Param("start") java.time.LocalDateTime start,
    @Param("end") java.time.LocalDateTime end,
    Pageable pageable
  );

  Optional<Vehicle> findTopByOrderByIdDesc();
}

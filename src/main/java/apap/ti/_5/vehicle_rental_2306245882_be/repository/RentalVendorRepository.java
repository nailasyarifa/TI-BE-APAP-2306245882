package apap.ti._5.vehicle_rental_2306245882_be.repository;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalVendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalVendorRepository extends JpaRepository<RentalVendor, Long> { 

    // tambahkan custom query kalau perlu, contoh:
    // List<RentalVendor> findAllByDeletedAtIsNull();
}

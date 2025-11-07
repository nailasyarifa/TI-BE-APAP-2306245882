package apap.ti._5.vehicle_rental_2306245882_be.repository;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalAddOn;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalAddOnRepository extends JpaRepository<RentalAddOn, UUID> {}

package apap.ti._5.vehicle_rental_2306245882_be.service;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalAddOn;
import java.util.List;
import java.util.UUID;

public interface RentalAddOnService {
    List<RentalAddOn> getAll();
    RentalAddOn getById(java.util.UUID id);
    RentalAddOn save(RentalAddOn addon);
    void deleteById(UUID id); 
}

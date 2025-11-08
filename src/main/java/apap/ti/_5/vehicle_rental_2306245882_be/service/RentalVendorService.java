package apap.ti._5.vehicle_rental_2306245882_be.service;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalVendor;
import java.util.List;

public interface RentalVendorService {
    List<RentalVendor> getAll();
    RentalVendor getById(Long id); 
}

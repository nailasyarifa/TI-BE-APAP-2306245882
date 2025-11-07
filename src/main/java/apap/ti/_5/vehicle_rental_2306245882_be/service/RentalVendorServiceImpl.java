package apap.ti._5.vehicle_rental_2306245882_be.service;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245882_be.service.RentalVendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RentalVendorServiceImpl implements RentalVendorService {

    private final RentalVendorRepository rentalVendorRepository;

    @Autowired
    public RentalVendorServiceImpl(RentalVendorRepository rentalVendorRepository) {
        this.rentalVendorRepository = rentalVendorRepository;
    }

    @Override
    public List<RentalVendor> getAll() {
        return rentalVendorRepository.findAll();
    }

    @Override
    public RentalVendor getById(Long id) {  // ubah Integer → Long
        return rentalVendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor with ID " + id + " not found"));
    }
}

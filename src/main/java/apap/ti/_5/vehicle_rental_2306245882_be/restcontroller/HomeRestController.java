package apap.ti._5.vehicle_rental_2306245882_be.restcontroller;


import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.BaseResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeRestController {

    private final VehicleRepository vehicleRepository;
    private final RentalVendorRepository rentalVendorRepository;
    private final RentalBookingRepository rentalBookingRepository;

    public HomeRestController(VehicleRepository vehicleRepository,
                              RentalVendorRepository rentalVendorRepository,
                              RentalBookingRepository rentalBookingRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalVendorRepository = rentalVendorRepository;
        this.rentalBookingRepository = rentalBookingRepository;
    }

    @GetMapping
    public BaseResponseDTO<Map<String, Long>> getStats() {
        Map<String, Long> data = Map.of(
            "totalVehicles", vehicleRepository.count(),
            "totalVendors", rentalVendorRepository.count(),
            "totalBookings", rentalBookingRepository.count()
        );
        return BaseResponseDTO.success(data);
    }
}


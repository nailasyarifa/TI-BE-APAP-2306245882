package apap.ti._5.vehicle_rental_2306245882_be.controller;

import apap.ti._5.vehicle_rental_2306245882_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalBookingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final VehicleRepository vehicleRepository;
    private final RentalVendorRepository rentalVendorRepository;
    private final RentalBookingRepository rentalBookingRepository;

    public HomeController(VehicleRepository vehicleRepository,
                          RentalVendorRepository rentalVendorRepository,
                          RentalBookingRepository rentalBookingRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalVendorRepository = rentalVendorRepository;
        this.rentalBookingRepository = rentalBookingRepository;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        long totalVehicles = vehicleRepository.count();
        long totalVendors = rentalVendorRepository.count();
        long totalBookings = rentalBookingRepository.count();

        model.addAttribute("totalVehicles", totalVehicles);
        model.addAttribute("totalVendors", totalVendors);
        model.addAttribute("totalBookings", totalBookings);

        return "home"; 
    
    }
}

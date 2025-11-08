package apap.ti._5.vehicle_rental_2306245882_be;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalVendorRepository;

@SpringBootApplication
public class VehicleRental2306245882BeApplication {

	public static void main(String[] args) {
		SpringApplication.run(VehicleRental2306245882BeApplication.class, args);
	}

	// @Bean
    // CommandLineRunner seed(RentalAddOnRepository addonRepo, RentalVendorRepository vendorRepo) {
    //     return args -> {
    //         if (addonRepo.count() == 0) {
    //             addonRepo.save(RentalAddOn.builder().name("GPS Navigation Device").price(50000.0).build());
    //             addonRepo.save(RentalAddOn.builder().name("Child Safety Seat").price(75000.0).build());
    //             addonRepo.save(RentalAddOn.builder().name("Wi-Fi Hotspot Device").price(60000.0).build());
    //             addonRepo.save(RentalAddOn.builder().name("Phone Charger Kit").price(20000.0).build());
    //             addonRepo.save(RentalAddOn.builder().name("Camping Equipment Set").price(100000.0).build());
    //         }

    //         if (vendorRepo.count() == 0) {
    //             vendorRepo.save(RentalVendor.builder()
    //                     .name("Vendor A")
    //                     .email("vendorA@example.com")
    //                     .phone("081234567890")
    //                     .listOfLocations(List.of("DKI Jakarta", "Depok", "Bogor", "Bekasi"))
    //                     .build());
    //             vendorRepo.save(RentalVendor.builder()
    //                     .name("Vendor B")
    //                     .email("vendorB@example.com")
    //                     .phone("081298765432")
    //                     .listOfLocations(List.of("Tangerang", "Bandung", "Bogor"))
    //                     .build());
    //         }
    //     };
    // }

}



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
}



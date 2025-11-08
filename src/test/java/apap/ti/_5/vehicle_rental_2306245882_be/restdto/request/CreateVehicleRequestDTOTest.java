package apap.ti._5.vehicle_rental_2306245882_be.restdto.request;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CreateVehicleRequestDTOTest {
    
    @Test
    void testGettersSetters() {
        CreateVehicleRequestDTO dto = new CreateVehicleRequestDTO();
        dto.setType("SUV");
        dto.setBrand("Toyota");
        dto.setModel("Fortuner");
        dto.setProductionYear(2022);
        dto.setLocation("Jakarta");
        dto.setLicensePlate("B1234XYZ");
        dto.setCapacity(7);
        dto.setTransmission("Automatic");
        dto.setFuelType("Diesel");
        dto.setPrice(500000.0);
        dto.setStatus("Available");
        dto.setRentalVendorId(1L);
        
        assertEquals("SUV", dto.getType());
        assertEquals("Toyota", dto.getBrand());
        assertEquals("Fortuner", dto.getModel());
        assertEquals(2022, dto.getProductionYear());
        assertEquals("Jakarta", dto.getLocation());
        assertEquals("B1234XYZ", dto.getLicensePlate());
        assertEquals(7, dto.getCapacity());
        assertEquals("Automatic", dto.getTransmission());
        assertEquals("Diesel", dto.getFuelType());
        assertEquals(500000.0, dto.getPrice());
        assertEquals("Available", dto.getStatus());
        assertEquals(1L, dto.getRentalVendorId());
    }
}

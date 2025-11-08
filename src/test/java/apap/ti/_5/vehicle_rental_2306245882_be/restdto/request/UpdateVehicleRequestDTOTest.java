package apap.ti._5.vehicle_rental_2306245882_be.restdto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UpdateVehicleRequestDTOTest {
    
    // @Test
    // void testConstructorAndGetters() {
    //     UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO(
    //         "SUV", "Toyota", "Fortuner", 2022, "Jakarta",
    //         "B1234XYZ", 7, "Automatic", "Diesel", 500000.0,
    //         "Available", 1
    //     );
        
    //     assertEquals("SUV", dto.getType());
    //     assertEquals("Toyota", dto.getBrand());
    //     assertEquals("Fortuner", dto.getModel());
    //     assertEquals(2022, dto.getProductionYear());
    //     assertEquals(1, dto.getRentalVendorId());
    // }
    
    @Test
    void testSetters() {
        UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
        dto.setType("Sedan");
        dto.setBrand("Honda");
        dto.setModel("Civic");
        dto.setPrice(350000.0);
        
        assertEquals("Sedan", dto.getType());
        assertEquals("Honda", dto.getBrand());
        assertEquals("Civic", dto.getModel());
        assertEquals(350000.0, dto.getPrice());
    }
}

package apap.ti._5.vehicle_rental_2306245882_be.restdto.request;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DTORequestsTest {

    @Test
    void testCreateRentalBookingRequestDTO() {
        CreateRentalBookingRequestDTO dto = new CreateRentalBookingRequestDTO();
        
        dto.setVehicleId("VEH123");
        dto.setPickUpTime(LocalDateTime.now());
        dto.setDropOffTime(LocalDateTime.now().plusDays(1));
        dto.setPickUpLocation("Jakarta");
        dto.setDropOffLocation("Bandung");
        dto.setCapacityNeeded(5);
        dto.setTransmissionNeeded("Manual");
        dto.setIncludeDriver(true);
        dto.setAddonIds(Arrays.asList("1", "2"));

        assertEquals("VEH123", dto.getVehicleId());
        assertNotNull(dto.getPickUpTime());
        assertNotNull(dto.getDropOffTime());
        assertEquals("Jakarta", dto.getPickUpLocation());
        assertEquals("Bandung", dto.getDropOffLocation());
        assertEquals(5, dto.getCapacityNeeded());
        assertEquals("Manual", dto.getTransmissionNeeded());
        assertTrue(dto.getIncludeDriver());
        assertEquals(2, dto.getAddonIds().size());
    }

    @Test
    void testCreateRentalBookingRequestDTOAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusDays(1);
        List<String> addons = Arrays.asList("1", "2", "3");

        CreateRentalBookingRequestDTO dto = new CreateRentalBookingRequestDTO(
            "VEH456", now, later, "Surabaya", "Malang", 
            7, "Automatic", false, addons
        );

        assertEquals("VEH456", dto.getVehicleId());
        assertEquals(now, dto.getPickUpTime());
        assertEquals(later, dto.getDropOffTime());
        assertEquals("Surabaya", dto.getPickUpLocation());
        assertEquals("Malang", dto.getDropOffLocation());
        assertEquals(7, dto.getCapacityNeeded());
        assertEquals("Automatic", dto.getTransmissionNeeded());
        assertFalse(dto.getIncludeDriver());
        assertEquals(3, dto.getAddonIds().size());
    }

    @Test
    void testCreateVehicleRequestDTO() {
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

    @Test
    void testCreateVehicleRequestDTOAllArgsConstructor() {
        CreateVehicleRequestDTO dto = new CreateVehicleRequestDTO(
            "Sedan", "Honda", "Civic", 2023, "Bandung",
            "D5678ABC", 5, "Manual", "Petrol", 350000.0,
            "In Use", 2L
        );

        assertEquals("Sedan", dto.getType());
        assertEquals("Honda", dto.getBrand());
        assertEquals("Civic", dto.getModel());
        assertEquals(2023, dto.getProductionYear());
        assertEquals("Bandung", dto.getLocation());
        assertEquals("D5678ABC", dto.getLicensePlate());
        assertEquals(5, dto.getCapacity());
        assertEquals("Manual", dto.getTransmission());
        assertEquals("Petrol", dto.getFuelType());
        assertEquals(350000.0, dto.getPrice());
        assertEquals("In Use", dto.getStatus());
        assertEquals(2L, dto.getRentalVendorId());
    }

    @Test
    void testUpdateRentalBookingRequestDTO() {
        UpdateRentalBookingRequestDTO dto = new UpdateRentalBookingRequestDTO();
        
        dto.setId("BOOK123");
        dto.setVehicleId("VEH789");
        dto.setPickUpTime(LocalDateTime.now());
        dto.setDropOffTime(LocalDateTime.now().plusDays(2));
        dto.setPickUpLocation("Medan");
        dto.setDropOffLocation("Pekanbaru");
        dto.setCapacityNeeded(4);
        dto.setTransmission("Automatic");
        dto.setIncludeDriver(true);
        dto.setAddonIds(Arrays.asList("3", "4"));

        assertEquals("BOOK123", dto.getId());
        assertEquals("VEH789", dto.getVehicleId());
        assertNotNull(dto.getPickUpTime());
        assertNotNull(dto.getDropOffTime());
        assertEquals("Medan", dto.getPickUpLocation());
        assertEquals("Pekanbaru", dto.getDropOffLocation());
        assertEquals(4, dto.getCapacityNeeded());
        assertEquals("Automatic", dto.getTransmission());
        assertTrue(dto.getIncludeDriver());
        assertEquals(2, dto.getAddonIds().size());
    }

    @Test
    void testUpdateStatusRequestDTO() {
        UpdateStatusRequestDTO dto = new UpdateStatusRequestDTO();
        
        dto.setId("BOOK456");
        dto.setStatus("ONGOING");

        assertEquals("BOOK456", dto.getId());
        assertEquals("ONGOING", dto.getStatus());
    }

    @Test
    void testUpdateStatusRequestDTOConstructor() {
        UpdateStatusRequestDTO dto = new UpdateStatusRequestDTO("BOOK789", "DONE");

        assertEquals("BOOK789", dto.getId());
        assertEquals("DONE", dto.getStatus());
    }

    @Test
    void testUpdateVehicleRequestDTO() {
        UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
        
        dto.setType("MPV");
        dto.setBrand("Suzuki");
        dto.setModel("Ertiga");
        dto.setProductionYear(2021);
        dto.setLocation("Yogyakarta");
        dto.setLicensePlate("AB1234CD");
        dto.setCapacity(7);
        dto.setTransmission("Manual");
        dto.setFuelType("Petrol");
        dto.setPrice(300000.0);
        dto.setStatus("Available");
        dto.setRentalVendorId(3L);

        assertEquals("MPV", dto.getType());
        assertEquals("Suzuki", dto.getBrand());
        assertEquals("Ertiga", dto.getModel());
        assertEquals(2021, dto.getProductionYear());
        assertEquals("Yogyakarta", dto.getLocation());
        assertEquals("AB1234CD", dto.getLicensePlate());
        assertEquals(7, dto.getCapacity());
        assertEquals("Manual", dto.getTransmission());
        assertEquals("Petrol", dto.getFuelType());
        assertEquals(300000.0, dto.getPrice());
        assertEquals("Available", dto.getStatus());
        assertEquals(3L, dto.getRentalVendorId());
    }

    @Test
    void testUpdateVehicleRequestDTOAllArgsConstructor() {
        UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO(
            "Hatchback", "Mazda", "2", 2020, "Semarang",
            "H9876EF", 5, "Automatic", "Petrol", 280000.0,
            "Unavailable", 4L
        );

        assertEquals("Hatchback", dto.getType());
        assertEquals("Mazda", dto.getBrand());
        assertEquals("2", dto.getModel());
        assertEquals(2020, dto.getProductionYear());
        assertEquals("Semarang", dto.getLocation());
        assertEquals("H9876EF", dto.getLicensePlate());
        assertEquals(5, dto.getCapacity());
        assertEquals("Automatic", dto.getTransmission());
        assertEquals("Petrol", dto.getFuelType());
        assertEquals(280000.0, dto.getPrice());
        assertEquals("Unavailable", dto.getStatus());
        assertEquals(4L, dto.getRentalVendorId());
    }
}
package apap.ti._5.vehicle_rental_2306245882_be.restdto.response;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DTOResponsesTest {

    @Test
    void testBaseResponseDTOSuccess() {
        BaseResponseDTO<String> dto = BaseResponseDTO.success("Test data");
        
        assertEquals(200, dto.getStatus());
        assertEquals("Success", dto.getMessage());
        assertEquals("Test data", dto.getData());
        assertNotNull(dto.getTimestamp());
    }

    @Test
    void testBaseResponseDTOError() {
        BaseResponseDTO<?> dto = BaseResponseDTO.error(404, "Not found");
        
        assertEquals(404, dto.getStatus());
        assertEquals("Not found", dto.getMessage());
        assertNull(dto.getData());
        assertNotNull(dto.getTimestamp());
    }

    @Test
    void testBaseResponseDTOConstructors() {
        OffsetDateTime now = OffsetDateTime.now();
        BaseResponseDTO<Integer> dto1 = new BaseResponseDTO<>(200, "OK", now, 123);
        
        assertEquals(200, dto1.getStatus());
        assertEquals("OK", dto1.getMessage());
        assertEquals(now, dto1.getTimestamp());
        assertEquals(123, dto1.getData());

        BaseResponseDTO<String> dto2 = new BaseResponseDTO<>(400, "Bad Request", "Error data");
        assertEquals(400, dto2.getStatus());
        assertEquals("Bad Request", dto2.getMessage());
        assertEquals("Error data", dto2.getData());
        assertNotNull(dto2.getTimestamp());
    }

    @Test
    void testBaseResponseDTOSetters() {
        BaseResponseDTO<String> dto = new BaseResponseDTO<>();
        OffsetDateTime time = OffsetDateTime.now();
        
        dto.setStatus(201);
        dto.setMessage("Created");
        dto.setTimestamp(time);
        dto.setData("New data");

        assertEquals(201, dto.getStatus());
        assertEquals("Created", dto.getMessage());
        assertEquals(time, dto.getTimestamp());
        assertEquals("New data", dto.getData());
    }

    @Test
    void testCancelBookingResponseDTO() {
        CancelBookingResponseDTO dto = new CancelBookingResponseDTO();
        
        dto.setId("BOOK123");
        dto.setStatus("CANCELLED");
        dto.setTotalPrice(1500000.0);
        dto.setMessage("Booking cancelled successfully");

        assertEquals("BOOK123", dto.getId());
        assertEquals("CANCELLED", dto.getStatus());
        assertEquals(1500000.0, dto.getTotalPrice());
        assertEquals("Booking cancelled successfully", dto.getMessage());
    }

    @Test
    void testCancelBookingResponseDTOConstructor() {
        CancelBookingResponseDTO dto = new CancelBookingResponseDTO(
            "BOOK456", "CANCELLED", 2000000.0, "Cancelled"
        );

        assertEquals("BOOK456", dto.getId());
        assertEquals("CANCELLED", dto.getStatus());
        assertEquals(2000000.0, dto.getTotalPrice());
        assertEquals("Cancelled", dto.getMessage());
    }

    @Test
    void testVehicleResponseDTO() {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        
        dto.setId("VEH123");
        dto.setType("SUV");
        dto.setBrand("Toyota");
        dto.setModel("Fortuner");
        dto.setCapacity(7);
        dto.setStatus("Available");
        dto.setPrice(500000.0);
        dto.setLicensePlate("B1234XYZ");
        dto.setLocation("Jakarta");
        dto.setProductionYear(2022);
        dto.setTransmission("Automatic");
        dto.setFuelType("Diesel");
        dto.setVendorName("Best Rental");

        assertEquals("VEH123", dto.getId());
        assertEquals("SUV", dto.getType());
        assertEquals("Toyota", dto.getBrand());
        assertEquals("Fortuner", dto.getModel());
        assertEquals(7, dto.getCapacity());
        assertEquals("Available", dto.getStatus());
        assertEquals(500000.0, dto.getPrice());
        assertEquals("B1234XYZ", dto.getLicensePlate());
        assertEquals("Jakarta", dto.getLocation());
        assertEquals(2022, dto.getProductionYear());
        assertEquals("Automatic", dto.getTransmission());
        assertEquals("Diesel", dto.getFuelType());
        assertEquals("Best Rental", dto.getVendorName());
    }

    @Test
    void testVehicleResponseDTOAllArgsConstructor() {
        VehicleResponseDTO dto = new VehicleResponseDTO(
            "VEH456", "Sedan", "Honda", "Civic", 5, "In Use",
            350000.0, "D5678ABC", "Bandung", 2023, "Manual", "Petrol", "Top Rental"
        );

        assertEquals("VEH456", dto.getId());
        assertEquals("Sedan", dto.getType());
        assertEquals("Honda", dto.getBrand());
        assertEquals("Civic", dto.getModel());
        assertEquals(5, dto.getCapacity());
        assertEquals("In Use", dto.getStatus());
        assertEquals(350000.0, dto.getPrice());
        assertEquals("D5678ABC", dto.getLicensePlate());
        assertEquals("Bandung", dto.getLocation());
        assertEquals(2023, dto.getProductionYear());
        assertEquals("Manual", dto.getTransmission());
        assertEquals("Petrol", dto.getFuelType());
        assertEquals("Top Rental", dto.getVendorName());
    }

    @Test
    void testRentalBookingResponseDTO() {
        RentalBookingResponseDTO dto = RentalBookingResponseDTO.builder()
            .id("BOOK123")
            .pickUpTime(LocalDateTime.now())
            .dropOffTime(LocalDateTime.now().plusDays(1))
            .pickUpLocation("Jakarta")
            .dropOffLocation("Bandung")
            .capacityNeeded(5)
            .transmissionNeeded("Automatic")
            .includeDriver(true)
            .totalPrice(1500000.0)
            .status("UPCOMING")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        assertEquals("BOOK123", dto.getId());
        assertNotNull(dto.getPickUpTime());
        assertNotNull(dto.getDropOffTime());
        assertEquals("Jakarta", dto.getPickUpLocation());
        assertEquals("Bandung", dto.getDropOffLocation());
        assertEquals(5, dto.getCapacityNeeded());
        assertEquals("Automatic", dto.getTransmissionNeeded());
        assertTrue(dto.getIncludeDriver());
        assertEquals(1500000.0, dto.getTotalPrice());
        assertEquals("UPCOMING", dto.getStatus());
    }

    @Test
    void testVehicleSummaryDTO() {
        RentalBookingResponseDTO.VehicleSummaryDTO dto = 
            RentalBookingResponseDTO.VehicleSummaryDTO.builder()
                .id("VEH123")
                .rentalVendorId(1)
                .type("SUV")
                .brand("Toyota")
                .model("Fortuner")
                .productionYear(2022)
                .location("Jakarta")
                .licensePlate("B1234XYZ")
                .capacity(7)
                .transmission("Automatic")
                .fuelType("Diesel")
                .pricePerDay(500000.0)
                .status("Available")
                .build();

        assertEquals("VEH123", dto.getId());
        assertEquals(1, dto.getRentalVendorId());
        assertEquals("SUV", dto.getType());
        assertEquals("Toyota", dto.getBrand());
        assertEquals("Fortuner", dto.getModel());
        assertEquals(2022, dto.getProductionYear());
        assertEquals("Jakarta", dto.getLocation());
        assertEquals("B1234XYZ", dto.getLicensePlate());
        assertEquals(7, dto.getCapacity());
        assertEquals("Automatic", dto.getTransmission());
        assertEquals("Diesel", dto.getFuelType());
        assertEquals(500000.0, dto.getPricePerDay());
        assertEquals("Available", dto.getStatus());
    }

    @Test
    void testAddOnDTO() {
        UUID id = UUID.randomUUID();
        RentalBookingResponseDTO.AddOnDTO dto = 
            RentalBookingResponseDTO.AddOnDTO.builder()
                .id(id)
                .name("GPS Navigator")
                .pricePerDay(50000.0)
                .build();

        assertEquals(id, dto.getId());
        assertEquals("GPS Navigator", dto.getName());
        assertEquals(50000.0, dto.getPricePerDay());
    }

    @Test
    void testRentalBookingResponseDTOWithNestedObjects() {
        UUID addonId = UUID.randomUUID();
        RentalBookingResponseDTO.AddOnDTO addon = 
            RentalBookingResponseDTO.AddOnDTO.builder()
                .id(addonId)
                .name("Child Seat")
                .pricePerDay(25000.0)
                .build();

        RentalBookingResponseDTO.VehicleSummaryDTO vehicle = 
            RentalBookingResponseDTO.VehicleSummaryDTO.builder()
                .id("VEH789")
                .type("MPV")
                .brand("Honda")
                .model("Freed")
                .capacity(7)
                .pricePerDay(400000.0)
                .build();

        RentalBookingResponseDTO dto = RentalBookingResponseDTO.builder()
            .id("BOOK789")
            .vehicle(vehicle)
            .listOfAddOns(Arrays.asList(addon))
            .totalPrice(425000.0)
            .status("ONGOING")
            .build();

        assertEquals("BOOK789", dto.getId());
        assertNotNull(dto.getVehicle());
        assertEquals("VEH789", dto.getVehicle().getId());
        assertEquals(1, dto.getListOfAddOns().size());
        assertEquals("Child Seat", dto.getListOfAddOns().get(0).getName());
        assertEquals(425000.0, dto.getTotalPrice());
        assertEquals("ONGOING", dto.getStatus());
    }
}
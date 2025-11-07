package apap.ti._5.vehicle_rental_2306245882_be.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO untuk mengembalikan detail RentalBooking ke client / view.
 * - mencakup ringkasan kendaraan (vehicle) dan daftar add-ons yang dipilih.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalBookingResponseDTO {
    private String id;                    // VRxxxxxx atau id booking string lain
    private VehicleSummaryDTO vehicle;    // ringkasan kendaraan yang dipesan
    private LocalDateTime pickUpTime;
    private LocalDateTime dropOffTime;
    private String pickUpLocation;
    private String dropOffLocation;
    private Integer capacityNeeded;
    private String transmissionNeeded;    // Manual / Automatic
    private Boolean includeDriver;
    private Double totalPrice;
    private String status;                // Upcoming / Ongoing / Done
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AddOnDTO> listOfAddOns;  // daftar add-ons yang dipilih

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VehicleSummaryDTO {
        private String id;
        private Integer rentalVendorId; // tipe sesuai modelmu (Integer menurut schema)
        private String type;
        private String brand;
        private String model;
        private Integer productionYear;
        private String location;
        private String licensePlate;
        private Integer capacity;
        private String transmission;
        private String fuelType;
        private Double pricePerDay;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddOnDTO {
        private UUID id;
        private String name;
        private Double pricePerDay;
    }
}

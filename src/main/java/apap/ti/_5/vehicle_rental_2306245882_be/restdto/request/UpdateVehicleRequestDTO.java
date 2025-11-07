package apap.ti._5.vehicle_rental_2306245882_be.restdto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVehicleRequestDTO {
    @NotBlank
    private String type;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotNull
    private Integer productionYear;

    private String location;

    @NotBlank
    private String licensePlate;

    private Integer capacity;

    @NotBlank
    private String transmission;

    @NotBlank
    private String fuelType;

    @NotNull
    @PositiveOrZero
    private Double price;

    private String status;

    // ubah menjadi Integer (sesuaikan dengan RentalVendor.id)
    private Long rentalVendorId;
}

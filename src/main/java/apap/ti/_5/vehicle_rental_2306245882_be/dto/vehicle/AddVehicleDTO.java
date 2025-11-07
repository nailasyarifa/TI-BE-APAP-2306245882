package apap.ti._5.vehicle_rental_2306245882_be.dto.vehicle;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddVehicleDTO {
    @NotBlank
    private String type;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotNull
    @Min(1900)
    private Integer productionYear;

    private String location;

    @NotBlank
    private String licensePlate;

    @Min(0)
    private Integer capacity;

    @NotBlank
    private String transmission;

    @NotBlank
    private String fuelType;

    @NotNull
    @PositiveOrZero
    private Double price;

    private String status;

    // rental vendor id (optional)
    private Integer rentalVendorId;
}

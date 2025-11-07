package apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRentalBookingDTO {
    private Boolean includeDriver = false;
    private String pickUpLocation;
    private String dropOffLocation;
    private LocalDateTime pickUpTime;
    private LocalDateTime dropOffTime;
    private Integer capacityNeeded;
    private String transmissionNeeded; // "Manual" / "Automatic"
    // intermediate chosen vehicle id when proceed to addons
    private String chosenVehicleId;
}

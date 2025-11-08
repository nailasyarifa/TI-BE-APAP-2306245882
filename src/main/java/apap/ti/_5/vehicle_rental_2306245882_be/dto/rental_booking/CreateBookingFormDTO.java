package apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateRentalBookingRequestDTO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO untuk form binding di Thymeleaf
 * Spring mungkin tidak bisa bind langsung ke record, jadi gunakan class ini
 */
@Data
public class CreateBookingFormDTO {
    private String vehicleId = "";
    private LocalDateTime pickUpTime;
    private LocalDateTime dropOffTime;
    private String pickUpLocation;
    private String dropOffLocation;
    private Integer capacityNeeded = 0;
    private String transmissionNeeded = "Automatic";
    private Boolean includeDriver = false;
    private List<String> addonIds = new ArrayList<>();

    // // Convert to CreateRentalBookingRequestDTO
    // public apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateRentalBookingRequestDTO toRequestDTO() {
    //     return new apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateRentalBookingRequestDTO(
    //             vehicleId != null ? vehicleId : "",
    //             pickUpTime,
    //             dropOffTime,
    //             pickUpLocation,
    //             dropOffLocation,
    //             capacityNeeded,
    //             transmissionNeeded,
    //             includeDriver,
    //             addonIds != null ? addonIds : new ArrayList<>()
    //     );
    // }
}


package apap.ti._5.vehicle_rental_2306245882_be.restdto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO digunakan untuk binding form (Thymeleaf).
 * Gunakan class biasa, bukan record, agar Spring bisa binding dengan baik.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRentalBookingRequestDTO {
    private String vehicleId = "";

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime pickUpTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dropOffTime;

    private String pickUpLocation;
    private String dropOffLocation;
    private Integer capacityNeeded = 0;
    private String transmissionNeeded = "Automatic";
    private Boolean includeDriver = false;
    private List<String> addonIds = new ArrayList<>();
}

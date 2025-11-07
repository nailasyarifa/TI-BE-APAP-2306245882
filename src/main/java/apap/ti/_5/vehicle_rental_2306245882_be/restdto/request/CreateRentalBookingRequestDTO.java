package apap.ti._5.vehicle_rental_2306245882_be.restdto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public record CreateRentalBookingRequestDTO(
    String vehicleId,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime pickUpTime,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime dropOffTime,
    String pickUpLocation,
    String dropOffLocation,
    Integer capacityNeeded,
    String transmissionNeeded,
    Boolean includeDriver,
    List<String> addonIds
) {}

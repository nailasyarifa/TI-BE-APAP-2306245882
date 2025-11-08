package apap.ti._5.vehicle_rental_2306245882_be.restcontroller;

import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.SearchResultVehicleDTO;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.CancelBookingResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.service.BadRequestException;
import apap.ti._5.vehicle_rental_2306245882_be.service.RentalBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class RentalBookingRestController {

    @Autowired
    private RentalBookingService bookingService;

    @PostMapping("/search")
    public ResponseEntity<List<SearchResultVehicleDTO>> searchVehicles(@RequestBody CreateRentalBookingRequestDTO req) {
        List<SearchResultVehicleDTO> results = bookingService.findAvailableVehicles(req);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalBooking> getBooking(@PathVariable String id) {
        RentalBooking b = bookingService.getById(id);
        return ResponseEntity.ok(b);
    }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<?> apiCancelBooking(@PathVariable String id) {
    //     try {
    //         RentalBooking cancelled = bookingService.cancelBooking(id);
    //         CancelBookingResponseDTO dto = new CancelBookingResponseDTO(
    //             cancelled.getId(),
    //             cancelled.getStatus() != null ? cancelled.getStatus().name() : null,
    //             cancelled.getTotalPrice(),
    //             "Booking cancelled"
    //         );
    //         return ResponseEntity.ok(dto);
    //     } catch (BadRequestException e) {
    //         return ResponseEntity.badRequest().body(Map.of("status",400,"message", e.getMessage()));
    //     } catch (Exception e) {
    //         return ResponseEntity.status(500).body(Map.of("status",500,"message","Server error: " + e.getMessage()));
    //     }
    // }
}

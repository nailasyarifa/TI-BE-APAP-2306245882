package apap.ti._5.vehicle_rental_2306245882_be.restcontroller;

import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.SearchResultVehicleDTO;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.service.BadRequestException;
import apap.ti._5.vehicle_rental_2306245882_be.service.RentalBookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class RentalBookingRestController {

    private final RentalBookingService bookingService;

    public RentalBookingRestController(RentalBookingService bookingService) {
        this.bookingService = bookingService;
    }

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

    // API DELETE endpoint - handles JSON response
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> apiCancelBooking(@PathVariable("id") String id) {
        try {
            RentalBooking canceled = bookingService.cancelBooking(id);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "id", canceled.getId()
            ));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    // Form POST endpoint - handles form submission with redirect
    @PostMapping("/{id}/delete")
    public String handleCancelBookingForm(@PathVariable("id") String id,
                                          RedirectAttributes ra) {
        try {
            RentalBooking canceled = bookingService.cancelBooking(id);
            ra.addFlashAttribute("success", "Booking berhasil dibatalkan: " + canceled.getId());
        } catch (BadRequestException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/bookings/" + id;
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Kesalahan saat membatalkan booking: " + e.getMessage());
            return "redirect:/bookings/" + id;
        }
        return "redirect:/bookings";
    }
}
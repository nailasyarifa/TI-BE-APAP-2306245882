package apap.ti._5.vehicle_rental_2306245882_be.restcontroller;

import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.BookingListItem;
import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.SearchResultVehicleDTO;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.UpdateRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.UpdateStatusRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.CancelBookingResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.service.BadRequestException;
import apap.ti._5.vehicle_rental_2306245882_be.service.RentalBookingService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/api/bookings")
public class RentalBookingRestController {

  private final RentalBookingService bookingService;

  public RentalBookingRestController(RentalBookingService bookingService) {
    this.bookingService = bookingService;
  }

  @GetMapping
  public ResponseEntity<BaseResponseDTO<?>> getAllBookings() {
    try {
      List<BookingListItem> bookings = bookingService.getAllBookingsForList();
      return ResponseEntity.ok(BaseResponseDTO.success(bookings));
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
          new BaseResponseDTO<>(
            500,
            "Error fetching bookings: " + e.getMessage(),
            null
          )
        );
    }
  }

  @PostMapping("/search")
  public ResponseEntity<List<SearchResultVehicleDTO>> searchVehicles(
    @RequestBody CreateRentalBookingRequestDTO req
  ) {
    List<SearchResultVehicleDTO> results = bookingService.findAvailableVehicles(
      req
    );
    return ResponseEntity.ok(results);
  }

  @GetMapping("/{id}")
  public ResponseEntity<RentalBooking> getBooking(@PathVariable String id) {
    RentalBooking b = bookingService.getById(id);
    return ResponseEntity.ok(b);
  }

  @PostMapping
  public ResponseEntity<BaseResponseDTO<?>> createBooking(
    @RequestBody CreateRentalBookingRequestDTO request
  ) {
    try {
      RentalBooking created = bookingService.createRentalBooking(request);
      return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(BaseResponseDTO.success(created));
    } catch (BadRequestException e) {
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new BaseResponseDTO<>(400, e.getMessage(), null));
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
          new BaseResponseDTO<>(
            500,
            "Error creating booking: " + e.getMessage(),
            null
          )
        );
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<BaseResponseDTO<?>> updateBooking(
    @PathVariable String id,
    @RequestBody UpdateRentalBookingRequestDTO request
  ) {
    try {
      request.setId(id);
      RentalBooking updated = bookingService.updateBookingDetails(request);
      return ResponseEntity.ok(BaseResponseDTO.success(updated));
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(
          new BaseResponseDTO<>(
            400,
            "Error updating booking: " + e.getMessage(),
            null
          )
        );
    }
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<BaseResponseDTO<?>> updateBookingStatus(
    @PathVariable String id,
    @RequestBody UpdateStatusRequestDTO request
  ) {
    try {
      RentalBooking updated = bookingService.updateBookingStatus(
        id,
        request.getStatus()
      );
      return ResponseEntity.ok(BaseResponseDTO.success(updated));
    } catch (BadRequestException e) {
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new BaseResponseDTO<>(400, e.getMessage(), null));
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
          new BaseResponseDTO<>(
            500,
            "Error updating status: " + e.getMessage(),
            null
          )
        );
    }
  }

  @GetMapping("/statistics")
  public ResponseEntity<BaseResponseDTO<?>> getBookingStatistics(
    @RequestParam(defaultValue = "monthly") String period,
    @RequestParam(defaultValue = "2024") int year
  ) {
    try {
      Map<String, Object> stats = bookingService.getBookingStats(period, year);
      return ResponseEntity.ok(BaseResponseDTO.success(stats));
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
          new BaseResponseDTO<>(
            500,
            "Error fetching statistics: " + e.getMessage(),
            null
          )
        );
    }
  }

  // === form/API handler for cancel ===
  @RequestMapping(
    value = "/{id}/delete",
    method = { RequestMethod.DELETE, RequestMethod.POST }
  )
  public String handleCancelBooking(
    @PathVariable("id") String id,
    RedirectAttributes ra
  ) {
    try {
      RentalBooking canceled = bookingService.cancelBooking(id);
      // buat flash attribute yang menandakan sukses -> akan ditangkap di /bookings
      ra.addFlashAttribute(
        "success",
        "Booking berhasil dibatalkan: " + canceled.getId()
      );
    } catch (BadRequestException e) {
      ra.addFlashAttribute("error", e.getMessage());
      return "redirect:/bookings/" + id;
    } catch (Exception e) {
      ra.addFlashAttribute(
        "error",
        "Kesalahan saat membatalkan booking: " + e.getMessage()
      );
      return "redirect:/bookings/" + id;
    }
    return "redirect:/bookings";
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<BaseResponseDTO<?>> cancelBooking(
    @PathVariable String id
  ) {
    try {
      RentalBooking canceled = bookingService.cancelBooking(id);
      return ResponseEntity.ok(
        new BaseResponseDTO<>(200, "Booking cancelled successfully", canceled)
      );
    } catch (BadRequestException e) {
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new BaseResponseDTO<>(400, e.getMessage(), null));
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
          new BaseResponseDTO<>(
            500,
            "Error cancelling booking: " + e.getMessage(),
            null
          )
        );
    }
  }

  // Legacy endpoint for backward compatibility with MVC
  @DeleteMapping("/{id}/delete")
  @ResponseBody
  public ResponseEntity<?> apiCancelBookingLegacy(
    @PathVariable("id") String id
  ) {
    try {
      RentalBooking canceled = bookingService.cancelBooking(id);
      return ResponseEntity.ok(
        Map.of("status", "success", "id", canceled.getId())
      );
    } catch (BadRequestException e) {
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(Map.of("status", "error", "message", e.getMessage()));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("status", "error", "message", e.getMessage()));
    }
  }
}

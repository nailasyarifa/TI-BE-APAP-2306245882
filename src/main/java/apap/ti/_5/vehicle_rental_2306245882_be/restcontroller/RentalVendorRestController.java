package apap.ti._5.vehicle_rental_2306245882_be.restcontroller;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.service.RentalVendorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class RentalVendorRestController {

  private final RentalVendorService vendorService;

  public RentalVendorRestController(RentalVendorService vendorService) {
    this.vendorService = vendorService;
  }

  @GetMapping
  public ResponseEntity<BaseResponseDTO<?>> getAllVendors() {
    try {
      List<RentalVendor> vendors = vendorService.getAll();
      return ResponseEntity.ok(BaseResponseDTO.success(vendors));
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
          new BaseResponseDTO<>(
            500,
            "Error fetching vendors: " + e.getMessage(),
            null
          )
        );
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<BaseResponseDTO<?>> getVendorById(@PathVariable Long id) {
    try {
      RentalVendor vendor = vendorService.getById(id);
      return ResponseEntity.ok(BaseResponseDTO.success(vendor));
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(
          new BaseResponseDTO<>(404, "Vendor not found: " + e.getMessage(), null)
        );
    }
  }
}


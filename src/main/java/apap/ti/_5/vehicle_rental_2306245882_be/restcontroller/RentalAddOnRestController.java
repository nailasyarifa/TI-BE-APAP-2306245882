package apap.ti._5.vehicle_rental_2306245882_be.restcontroller;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.service.RentalAddOnService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/addons")
public class RentalAddOnRestController {

  private final RentalAddOnService addOnService;

  public RentalAddOnRestController(RentalAddOnService addOnService) {
    this.addOnService = addOnService;
  }

  @GetMapping
  public ResponseEntity<BaseResponseDTO<?>> getAllAddOns() {
    try {
      List<RentalAddOn> addOns = addOnService.getAll();
      return ResponseEntity.ok(BaseResponseDTO.success(addOns));
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
          new BaseResponseDTO<>(
            500,
            "Error fetching add-ons: " + e.getMessage(),
            null
          )
        );
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<BaseResponseDTO<?>> getAddOnById(@PathVariable UUID id) {
    try {
      RentalAddOn addOn = addOnService.getById(id);
      if (addOn == null) {
        return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body(new BaseResponseDTO<>(404, "Add-on not found", null));
      }
      return ResponseEntity.ok(BaseResponseDTO.success(addOn));
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
          new BaseResponseDTO<>(
            500,
            "Error fetching add-on: " + e.getMessage(),
            null
          )
        );
    }
  }
}


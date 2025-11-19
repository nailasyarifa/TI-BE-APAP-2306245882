package apap.ti._5.vehicle_rental_2306245882_be.restcontroller;

import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleRestController {

  @Autowired
  private VehicleService vehicleService;

  @GetMapping
  public ResponseEntity<BaseResponseDTO<?>> getAll(
    @RequestParam(required = false) String type,
    @RequestParam(required = false) String search,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    var pageRes = vehicleService.getAllVehicles(type, search, pageable);
    return ResponseEntity.ok(BaseResponseDTO.success(pageRes.getContent()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BaseResponseDTO<?>> getVehicleById(
    @PathVariable String id
  ) {
    var vehicle = vehicleService.getById(id);
    return ResponseEntity.ok(new BaseResponseDTO<>(200, "Success", vehicle));
  }

  @PostMapping
  public ResponseEntity<BaseResponseDTO<?>> createVehicle(
    @RequestBody CreateVehicleRequestDTO request
  ) {
    try {
      VehicleResponseDTO created = vehicleService.create(request);
      return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(BaseResponseDTO.success(created));
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(
          new BaseResponseDTO<>(
            400,
            "Error creating vehicle: " + e.getMessage(),
            null
          )
        );
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<BaseResponseDTO<?>> updateVehicle(
    @PathVariable String id,
    @RequestBody UpdateVehicleRequestDTO request
  ) {
    try {
      VehicleResponseDTO updated = vehicleService.update(id, request);
      return ResponseEntity.ok(BaseResponseDTO.success(updated));
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(
          new BaseResponseDTO<>(
            400,
            "Error updating vehicle: " + e.getMessage(),
            null
          )
        );
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<BaseResponseDTO<?>> deleteVehicle(
    @PathVariable String id
  ) {
    try {
      vehicleService.delete(id);
      return ResponseEntity.ok(
        new BaseResponseDTO<>(200, "Vehicle deleted successfully", null)
      );
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(
          new BaseResponseDTO<>(
            400,
            "Error deleting vehicle: " + e.getMessage(),
            null
          )
        );
    }
  }
}

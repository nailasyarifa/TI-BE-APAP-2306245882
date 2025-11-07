package apap.ti._5.vehicle_rental_2306245882_be.restcontroller;

import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<BaseResponseDTO<?>> getVehicleById(@PathVariable String id) {
        var vehicle = vehicleService.getById(id);
        return ResponseEntity.ok(new BaseResponseDTO<>(200, "Success", vehicle));
    }
}

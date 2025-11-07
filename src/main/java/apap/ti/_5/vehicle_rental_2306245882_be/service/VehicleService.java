package apap.ti._5.vehicle_rental_2306245882_be.service;

import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.VehicleResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleService {
    Page<VehicleResponseDTO> getAllVehicles(String type, String search, Pageable pageable);
    VehicleResponseDTO getById(String id);
    VehicleResponseDTO create(CreateVehicleRequestDTO req);
    VehicleResponseDTO update(String id, UpdateVehicleRequestDTO req);
    void delete(String id);
    
}

package apap.ti._5.vehicle_rental_2306245882_be.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseDTO {
    private String id;
    private String type;
    private String brand;
    private String model;
    private Integer capacity;
    private String status;
    private Double price;
    private String licensePlate;
    private String location;
    private Integer productionYear;
    private String transmission;
    private String fuelType;   
    private String vendorName; 
}

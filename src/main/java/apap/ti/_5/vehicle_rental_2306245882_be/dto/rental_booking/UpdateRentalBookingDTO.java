package apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class UpdateRentalBookingDTO {
    private String id;
    private String vehicleId;
    private LocalDateTime pickUpTime;
    private LocalDateTime dropOffTime;
    private String pickUpLocation;
    private String dropOffLocation;
    private Integer capacityNeeded;
    private String transmission;
    private Boolean includeDriver;
    private BigDecimal totalPrice;
    private List<String> addonIds;

    public UpdateRentalBookingDTO() {}

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public LocalDateTime getPickUpTime() { return pickUpTime; }
    public void setPickUpTime(LocalDateTime pickUpTime) { this.pickUpTime = pickUpTime; }

    public LocalDateTime getDropOffTime() { return dropOffTime; }
    public void setDropOffTime(LocalDateTime dropOffTime) { this.dropOffTime = dropOffTime; }

    public String getPickUpLocation() { return pickUpLocation; }
    public void setPickUpLocation(String pickUpLocation) { this.pickUpLocation = pickUpLocation; }

    public String getDropOffLocation() { return dropOffLocation; }
    public void setDropOffLocation(String dropOffLocation) { this.dropOffLocation = dropOffLocation; }

    public Integer getCapacityNeeded() { return capacityNeeded; }
    public void setCapacityNeeded(Integer capacityNeeded) { this.capacityNeeded = capacityNeeded; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public Boolean getIncludeDriver() { return includeDriver; }
    public void setIncludeDriver(Boolean includeDriver) { this.includeDriver = includeDriver; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public List<String> getAddonIds() { return addonIds; }
    public void setAddonIds(List<String> addonIds) { this.addonIds = addonIds; }
}

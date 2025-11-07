package apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking;

import java.time.LocalDateTime;

/**
 * DTO untuk menampilkan daftar booking di halaman /bookings
 * Berisi data ringkas saja (tidak semua field RentalBooking).
 */
public class BookingListItem {

    private String id;
    private String vehicleId;
    private LocalDateTime pickUpTime;
    private LocalDateTime dropOffTime;
    private String pickUpLocation;
    private String status;
    private Double totalPrice;

    // --- Constructor kosong (dibutuhkan oleh Thymeleaf dan Spring) ---
    public BookingListItem() {}

    // --- Constructor lengkap untuk mapping cepat dari entity ---
    public BookingListItem(String id, String vehicleId,
                           LocalDateTime pickUpTime,
                           LocalDateTime dropOffTime,
                           String pickUpLocation,
                           String status,
                           Double totalPrice) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.pickUpTime = pickUpTime;
        this.dropOffTime = dropOffTime;
        this.pickUpLocation = pickUpLocation;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    // --- Getter dan Setter ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDateTime getPickUpTime() {
        return pickUpTime;
    }

    public void setPickUpTime(LocalDateTime pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    public LocalDateTime getDropOffTime() {
        return dropOffTime;
    }

    public void setDropOffTime(LocalDateTime dropOffTime) {
        this.dropOffTime = dropOffTime;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

package apap.ti._5.vehicle_rental_2306245882_be.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rental_booking")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RentalBooking {

    @Id
    @Column(name = "id", length = 16)
    
    private String id; // VR000001 format

    // selected vehicle id (FK to vehicle.id)
    @Column(name = "vehicle_id", length = 64)
    private String vehicleId;

    @Column(name = "pick_up_time")
    private LocalDateTime pickUpTime;

    @Column(name = "drop_of_time")
    private LocalDateTime dropOffTime;

    @Column(name = "pick_up_location")
    private String pickUpLocation;

    @Column(name = "drop_of_location")
    private String dropOffLocation;

    @Column(name = "capacity_needed")
    private Integer capacityNeeded;

    @Column(name = "transmission_needed")
    private String transmissionNeeded; // Manual / Automatic

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "include_driver")
    private Boolean includeDriver;

    @Enumerated(EnumType.STRING)
    private BookingStatus status; // UPCOMING, ONGOING, DONE

    // Many-to-many mapping to addon via join table
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "booking_addons",
        joinColumns = @JoinColumn(name = "booking_id"),
        inverseJoinColumns = @JoinColumn(name = "addon_id"))
    private List<RentalAddOn> listOfAddOns;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        if (id == null) id = generateId();
        if (createdAt == null) createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate(){ updatedAt = LocalDateTime.now(); }

    private String generateId(){
        // simple: VR + timestamp-based - in production, ensure sequential format VR000001
        return "VR" + String.valueOf(Math.abs(UUID.randomUUID().getMostSignificantBits())).replace("-","");
    }

    public enum BookingStatus { UPCOMING, ONGOING, DONE }
}

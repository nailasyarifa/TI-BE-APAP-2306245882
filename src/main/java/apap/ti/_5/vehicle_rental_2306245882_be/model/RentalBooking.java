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

    @Column(name = "drop_off_time")
    private LocalDateTime dropOffTime;

    @Column(name = "pick_up_location")
    private String pickUpLocation;

    @Column(name = "drop_off_location")
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
    @JoinTable(name = "booking_addon",
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
        long suffix = System.currentTimeMillis() % 1_000_000L;
        return String.format("VR%06d", suffix); // ex: VR123456
    }

    public enum BookingStatus { UPCOMING, ONGOING, DONE }

    // @Column(name="deleted", nullable=false)

    // private boolean deleted = false;    
    // public boolean isDeleted(){ return deleted; }
    // public void setDeleted(boolean d){ this.deleted = d; }

}

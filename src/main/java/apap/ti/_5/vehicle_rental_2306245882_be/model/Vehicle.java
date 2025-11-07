package apap.ti._5.vehicle_rental_2306245882_be.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vehicle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @Column(name = "id", length = 64)
    private String id; // e.g. "VEH0001" or UUID fallback

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_vendor") // sesuai nama kolom di DB (int4)
    private RentalVendor rentalVendor;

    @Column(name = "type")
    private String type; // Sedan, SUV, MPV, Luxury

    // pastikan mapping ke TEXT agar Hibernate memetakan ke tipe karakter di Postgres
    @Column(name= "brand", nullable = false, columnDefinition = "text")
    private String brand;

    @Column(name= "model", nullable = false, columnDefinition = "text")
    private String model;

    // kolom di DB: production_ye (typo di DB)
    @Column(name = "production_year")
    private Integer productionYear;

    @Column(name = "location")
    private String location;

    @Column(name = "license_plate", unique = true)
    private String licensePlate;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "transmission")
    private String transmission; // Manual / Automatic

    @Column(name = "fuel_type")
    private String fuelType; // Bensin / Diesel / Hybrid / Listrik

    @Column(name = "price")
    private Double price;

    @Column(name = "status")
    private String status; // Available / In Use / Unavailable

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // gunakan @Builder.Default agar nilai default dipertahankan saat menggunakan Lombok @Builder
    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @PrePersist
    public void prePersist() {
        if (id == null || id.isBlank()) id = UUID.randomUUID().toString();
        if (createdAt == null) createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

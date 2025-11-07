package apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking;

import java.io.Serializable;

public class SearchResultVehicleDTO implements Serializable {
    private String id;
    private String type;
    private String brand;
    private String model;
    private Integer capacity;
    private String transmission;
    private Double pricePerDay;
    private Double totalPrice; // pricePerDay * days (+ driver if included)

    public SearchResultVehicleDTO() {}

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    public Double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(Double pricePerDay) { this.pricePerDay = pricePerDay; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}

package apap.ti._5.vehicle_rental_2306245882_be.restdto.response;

public class CancelBookingResponseDTO {
    private String id;
    private String status;
    private Double totalPrice;
    private String message;

    public CancelBookingResponseDTO() {}

    public CancelBookingResponseDTO(String id, String status, Double totalPrice, String message) {
        this.id = id;
        this.status = status;
        this.totalPrice = totalPrice;
        this.message = message;
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
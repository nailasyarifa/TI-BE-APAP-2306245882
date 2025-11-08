package apap.ti._5.vehicle_rental_2306245882_be.restdto.request;

public class UpdateStatusRequestDTO {
    private String id;
    private String status;

    public UpdateStatusRequestDTO() {}

    public UpdateStatusRequestDTO(String id, String status) {
        this.id = id;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

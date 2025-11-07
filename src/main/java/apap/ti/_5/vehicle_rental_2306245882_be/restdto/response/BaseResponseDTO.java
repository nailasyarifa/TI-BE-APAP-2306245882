package apap.ti._5.vehicle_rental_2306245882_be.restdto.response;

import java.time.OffsetDateTime;

public class BaseResponseDTO<T> {
    private int status;
    private String message;
    private OffsetDateTime timestamp;
    private T data;

    public BaseResponseDTO() {}

    public BaseResponseDTO(int status, String message, OffsetDateTime timestamp, T data) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.data = data;
    }

    // Convenience constructors
    public BaseResponseDTO(int status, String message, T data) {
        this(status, message, OffsetDateTime.now(), data);
    }

    // Static factory helpers (use these from controllers)
    public static <T> BaseResponseDTO<T> success(T data) {
        return new BaseResponseDTO<>(200, "Success", OffsetDateTime.now(), data);
    }

    public static <T> BaseResponseDTO<T> error(int status, String message) {
        return new BaseResponseDTO<>(status, message, OffsetDateTime.now(), null);
    }

    // getters + setters
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}

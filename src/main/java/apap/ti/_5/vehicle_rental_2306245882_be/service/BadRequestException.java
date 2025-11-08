package apap.ti._5.vehicle_rental_2306245882_be.service;

public class BadRequestException extends RuntimeException {
    public BadRequestException() { super(); }
    public BadRequestException(String message) { super(message); }
    public BadRequestException(String message, Throwable cause) { super(message, cause); }
    public BadRequestException(Throwable cause) { super(cause); }
}

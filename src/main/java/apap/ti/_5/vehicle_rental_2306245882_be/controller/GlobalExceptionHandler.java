package apap.ti._5.vehicle_rental_2306245882_be.controller;

import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.service.BadRequestException;
import apap.ti._5.vehicle_rental_2306245882_be.service.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler to return BaseResponseDTO in error cases.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponseDTO<?>> handleNotFound(ResourceNotFoundException ex) {
        logger.warn("ResourceNotFound: {}", ex.getMessage());
        BaseResponseDTO<?> resp = BaseResponseDTO.error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseResponseDTO<?>> handleBadRequest(BadRequestException ex) {
        logger.warn("BadRequest: {}", ex.getMessage());
        BaseResponseDTO<?> resp = BaseResponseDTO.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponseDTO<?>> handleValidation(MethodArgumentNotValidException ex) {
        // collect field errors
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing // keep first if duplicates
                ));

        String msg = "Validation failed for fields: " + String.join(", ", fieldErrors.keySet());
        logger.warn("Validation error: {} -> {}", msg, fieldErrors);

        BaseResponseDTO<Map<String, String>> resp = BaseResponseDTO.error(HttpStatus.BAD_REQUEST.value(), msg);
        resp.setData(fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDTO<?>> handleGeneric(Exception ex) {
        logger.error("Unhandled exception", ex);
        String msg = "Internal server error";
        BaseResponseDTO<?> resp = BaseResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}

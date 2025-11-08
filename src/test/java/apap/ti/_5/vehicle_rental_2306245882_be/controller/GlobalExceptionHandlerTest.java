package apap.ti._5.vehicle_rental_2306245882_be.controller;

import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.service.BadRequestException;
import apap.ti._5.vehicle_rental_2306245882_be.service.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        
        ResponseEntity<BaseResponseDTO<?>> response = handler.handleNotFound(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Resource not found", response.getBody().getMessage());
    }

    @Test
    void testHandleBadRequest() {
        BadRequestException ex = new BadRequestException("Bad request");
        
        ResponseEntity<BaseResponseDTO<?>> response = handler.handleBadRequest(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad request", response.getBody().getMessage());
    }

    @Test
    void testHandleValidation() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError error1 = new FieldError("object", "field1", "error1");
        FieldError error2 = new FieldError("object", "field2", "error2");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(error1, error2));
        
        ResponseEntity<BaseResponseDTO<?>> response = handler.handleValidation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Validation failed"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) response.getBody().getData();
        assertNotNull(data);
        assertEquals(2, data.size());
    }

    @Test
    void testHandleValidationWithDuplicateFields() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError error1 = new FieldError("object", "field1", "first error");
        FieldError error2 = new FieldError("object", "field1", "second error");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(error1, error2));
        
        ResponseEntity<BaseResponseDTO<?>> response = handler.handleValidation(ex);
        
        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) response.getBody().getData();
        assertEquals(1, data.size());
        assertEquals("first error", data.get("field1"));
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Generic error");
        
        ResponseEntity<BaseResponseDTO<?>> response = handler.handleGeneric(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal server error", response.getBody().getMessage());
    }

    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Runtime error");
        
        ResponseEntity<BaseResponseDTO<?>> response = handler.handleGeneric(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
    }
}
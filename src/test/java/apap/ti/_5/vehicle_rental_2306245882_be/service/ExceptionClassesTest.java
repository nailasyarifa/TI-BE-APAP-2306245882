package apap.ti._5.vehicle_rental_2306245882_be.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionClassesTest {

    @Test
    void testBadRequestExceptionNoArgs() {
        BadRequestException ex = new BadRequestException();
        assertNotNull(ex);
        assertNull(ex.getMessage());
    }

    @Test
    void testBadRequestExceptionWithMessage() {
        BadRequestException ex = new BadRequestException("Test message");
        assertEquals("Test message", ex.getMessage());
    }

    @Test
    void testBadRequestExceptionWithMessageAndCause() {
        Throwable cause = new IllegalArgumentException("Root cause");
        BadRequestException ex = new BadRequestException("Test message", cause);
        
        assertEquals("Test message", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void testBadRequestExceptionWithCause() {
        Throwable cause = new IllegalArgumentException("Root cause");
        BadRequestException ex = new BadRequestException(cause);
        
        assertEquals(cause, ex.getCause());
    }

    @Test
    void testBadRequestExceptionIsRuntimeException() {
        BadRequestException ex = new BadRequestException("Test");
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void testResourceNotFoundExceptionWithMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        assertEquals("Resource not found", ex.getMessage());
    }

    @Test
    void testResourceNotFoundExceptionIsRuntimeException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Test");
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void testExceptionsCanBeThrown() {
        assertThrows(BadRequestException.class, () -> {
            throw new BadRequestException("Bad request");
        });

        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("Not found");
        });
    }

    @Test
    void testExceptionsInheritance() {
        BadRequestException badRequest = new BadRequestException("test");
        ResourceNotFoundException notFound = new ResourceNotFoundException("test");

        assertTrue(badRequest instanceof Exception);
        assertTrue(notFound instanceof Exception);
        assertTrue(badRequest instanceof RuntimeException);
        assertTrue(notFound instanceof RuntimeException);
    }
}
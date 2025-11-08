package apap.ti._5.vehicle_rental_2306245882_be.service;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalVendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalVendorServiceTest {

    @Mock
    private RentalVendorRepository rentalVendorRepository;

    @InjectMocks
    private RentalVendorServiceImpl vendorService;

    private RentalVendor vendor1;
    private RentalVendor vendor2;

    @BeforeEach
    void setUp() {
        vendor1 = new RentalVendor();
        vendor1.setId(1);
        vendor1.setName("Best Rental");
        vendor1.setListOfLocations(Arrays.asList("Jakarta", "Bandung"));

        vendor2 = new RentalVendor();
        vendor2.setId(2);
        vendor2.setName("Top Rental");
        vendor2.setListOfLocations(Arrays.asList("Surabaya", "Malang"));
    }

    @Test
    void testGetAll() {
        when(rentalVendorRepository.findAll()).thenReturn(Arrays.asList(vendor1, vendor2));

        List<RentalVendor> result = vendorService.getAll();

        assertEquals(2, result.size());
        assertEquals("Best Rental", result.get(0).getName());
        assertEquals("Top Rental", result.get(1).getName());
        verify(rentalVendorRepository, times(1)).findAll();
    }

    @Test
    void testGetAllEmpty() {
        when(rentalVendorRepository.findAll()).thenReturn(Arrays.asList());

        List<RentalVendor> result = vendorService.getAll();

        assertTrue(result.isEmpty());
        verify(rentalVendorRepository, times(1)).findAll();
    }

    @Test
    void testGetByIdFound() {
        when(rentalVendorRepository.findById(1L)).thenReturn(Optional.of(vendor1));

        RentalVendor result = vendorService.getById(1L);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Best Rental", result.getName());
        assertEquals(2, result.getListOfLocations().size());
        verify(rentalVendorRepository, times(1)).findById(1L);
    }

    @Test
    void testGetByIdNotFound() {
        when(rentalVendorRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vendorService.getById(999L);
        });

        assertTrue(exception.getMessage().contains("Vendor with ID 999 not found"));
        verify(rentalVendorRepository, times(1)).findById(999L);
    }

    @Test
    void testGetByIdWithDifferentVendor() {
        when(rentalVendorRepository.findById(2L)).thenReturn(Optional.of(vendor2));

        RentalVendor result = vendorService.getById(2L);

        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("Top Rental", result.getName());
        assertTrue(result.getListOfLocations().contains("Surabaya"));
        verify(rentalVendorRepository, times(1)).findById(2L);
    }

    @Test
    void testGetAllWithMultipleLocations() {
        RentalVendor vendor3 = new RentalVendor();
        vendor3.setId(3);
        vendor3.setName("Premium Rental");
        vendor3.setListOfLocations(Arrays.asList("Jakarta", "Bandung", "Surabaya", "Bali"));

        when(rentalVendorRepository.findAll()).thenReturn(Arrays.asList(vendor1, vendor2, vendor3));

        List<RentalVendor> result = vendorService.getAll();

        assertEquals(3, result.size());
        assertEquals(4, result.get(2).getListOfLocations().size());
        verify(rentalVendorRepository, times(1)).findAll();
    }

    @Test
    void testGetAllMultipleCalls() {
        when(rentalVendorRepository.findAll()).thenReturn(Arrays.asList(vendor1, vendor2));

        List<RentalVendor> result1 = vendorService.getAll();
        List<RentalVendor> result2 = vendorService.getAll();

        assertEquals(2, result1.size());
        assertEquals(2, result2.size());
        verify(rentalVendorRepository, times(2)).findAll();
    }

    @Test
    void testGetByIdZero() {
        when(rentalVendorRepository.findById(0L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vendorService.getById(0L);
        });

        assertTrue(exception.getMessage().contains("Vendor with ID 0 not found"));
        verify(rentalVendorRepository, times(1)).findById(0L);
    }

    @Test
    void testGetByIdNegative() {
        when(rentalVendorRepository.findById(-1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vendorService.getById(-1L);
        });

        assertTrue(exception.getMessage().contains("Vendor with ID -1 not found"));
        verify(rentalVendorRepository, times(1)).findById(-1L);
    }
}
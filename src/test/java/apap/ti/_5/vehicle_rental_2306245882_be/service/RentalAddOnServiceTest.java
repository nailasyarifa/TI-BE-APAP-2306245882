package apap.ti._5.vehicle_rental_2306245882_be.service;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalAddOnRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalAddOnServiceTest {

    @Mock
    private RentalAddOnRepository addonRepository;

    @InjectMocks
    private RentalAddOnServiceImpl addonService;

    private RentalAddOn addon1;
    private RentalAddOn addon2;
    private UUID uuid1;
    private UUID uuid2;

    @BeforeEach
    void setUp() {
        uuid1 = UUID.randomUUID();
        uuid2 = UUID.randomUUID();

        addon1 = new RentalAddOn();
        addon1.setId(uuid1);
        addon1.setName("GPS Navigator");
        addon1.setPrice(50000.0);

        addon2 = new RentalAddOn();
        addon2.setId(uuid2);
        addon2.setName("Child Seat");
        addon2.setPrice(25000.0);
    }

    @Test
    void testGetAll() {
        when(addonRepository.findAll()).thenReturn(Arrays.asList(addon1, addon2));

        List<RentalAddOn> result = addonService.getAll();

        assertEquals(2, result.size());
        assertEquals("GPS Navigator", result.get(0).getName());
        assertEquals("Child Seat", result.get(1).getName());
        verify(addonRepository, times(1)).findAll();
    }

    @Test
    void testGetAllEmpty() {
        when(addonRepository.findAll()).thenReturn(Arrays.asList());

        List<RentalAddOn> result = addonService.getAll();

        assertTrue(result.isEmpty());
        verify(addonRepository, times(1)).findAll();
    }

    @Test
    void testGetByIdFound() {
        when(addonRepository.findById(uuid1)).thenReturn(Optional.of(addon1));

        RentalAddOn result = addonService.getById(uuid1);

        assertNotNull(result);
        assertEquals(uuid1, result.getId());
        assertEquals("GPS Navigator", result.getName());
        verify(addonRepository, times(1)).findById(uuid1);
    }

    @Test
    void testGetByIdNotFound() {
        when(addonRepository.findById(uuid1)).thenReturn(Optional.empty());

        RentalAddOn result = addonService.getById(uuid1);

        assertNull(result);
        verify(addonRepository, times(1)).findById(uuid1);
    }

    @Test
    void testSave() {
        when(addonRepository.save(any(RentalAddOn.class))).thenReturn(addon1);

        RentalAddOn result = addonService.save(addon1);

        assertNotNull(result);
        assertEquals(uuid1, result.getId());
        assertEquals("GPS Navigator", result.getName());
        verify(addonRepository, times(1)).save(addon1);
    }

    @Test
    void testSaveNewAddOn() {
        RentalAddOn newAddon = new RentalAddOn();
        newAddon.setName("WiFi Hotspot");
        newAddon.setPrice(30000.0);

        UUID newUuid = UUID.randomUUID();
        newAddon.setId(newUuid);

        when(addonRepository.save(any(RentalAddOn.class))).thenReturn(newAddon);

        RentalAddOn result = addonService.save(newAddon);

        assertNotNull(result);
        assertEquals("WiFi Hotspot", result.getName());
        assertEquals(30000.0, result.getPrice());
        verify(addonRepository, times(1)).save(newAddon);
    }

    @Test
    void testDeleteById() {
        doNothing().when(addonRepository).deleteById(uuid1);

        addonService.deleteById(uuid1);

        verify(addonRepository, times(1)).deleteById(uuid1);
    }

    @Test
    void testDeleteByIdNonExistent() {
        doNothing().when(addonRepository).deleteById(uuid1);

        assertDoesNotThrow(() -> addonService.deleteById(uuid1));
        verify(addonRepository, times(1)).deleteById(uuid1);
    }

    @Test
    void testSaveWithNullPrice() {
        RentalAddOn addon = new RentalAddOn();
        addon.setName("Free Item");
        addon.setPrice(null);

        when(addonRepository.save(any(RentalAddOn.class))).thenReturn(addon);

        RentalAddOn result = addonService.save(addon);

        assertNotNull(result);
        assertNull(result.getPrice());
        verify(addonRepository, times(1)).save(addon);
    }

    @Test
    void testGetAllMultipleCalls() {
        when(addonRepository.findAll()).thenReturn(Arrays.asList(addon1, addon2));

        List<RentalAddOn> result1 = addonService.getAll();
        List<RentalAddOn> result2 = addonService.getAll();

        assertEquals(2, result1.size());
        assertEquals(2, result2.size());
        verify(addonRepository, times(2)).findAll();
    }
}
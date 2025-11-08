package apap.ti._5.vehicle_rental_2306245882_be.service;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245882_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.VehicleResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private RentalVendorRepository rentalVendorRepository;

    @Mock
    private RentalBookingRepository rentalBookingRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Vehicle vehicle1;
    private Vehicle vehicle2;
    private RentalVendor vendor;

    @BeforeEach
    void setUp() {
        vendor = new RentalVendor();
        vendor.setId(1);
        vendor.setName("Best Rental");

        vehicle1 = new Vehicle();
        vehicle1.setId("VEH0001");
        vehicle1.setType("SUV");
        vehicle1.setBrand("Toyota");
        vehicle1.setModel("Fortuner");
        vehicle1.setCapacity(7);
        vehicle1.setStatus("Available");
        vehicle1.setPrice(500000.0);
        vehicle1.setLicensePlate("B1234XYZ");
        vehicle1.setLocation("Jakarta");
        vehicle1.setProductionYear(2022);
        vehicle1.setTransmission("Automatic");
        vehicle1.setFuelType("Diesel");
        vehicle1.setRentalVendor(vendor);
        vehicle1.setDeleted(false);

        vehicle2 = new Vehicle();
        vehicle2.setId("VEH0002");
        vehicle2.setType("Sedan");
        vehicle2.setBrand("Honda");
        vehicle2.setModel("Civic");
        vehicle2.setCapacity(5);
        vehicle2.setStatus("Available");
        vehicle2.setPrice(350000.0);
        vehicle2.setDeleted(false);
    }

    @Test
    void testGetAllVehiclesWithoutFilters() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id").ascending());
        Page<Vehicle> page = new PageImpl<>(Arrays.asList(vehicle1, vehicle2));
        
        when(vehicleRepository.searchVehicles(isNull(), isNull(), any(Pageable.class))).thenReturn(page);

        Page<VehicleResponseDTO> result = vehicleService.getAllVehicles(null, null, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("VEH0001", result.getContent().get(0).getId());
        assertEquals("Toyota", result.getContent().get(0).getBrand());
        verify(vehicleRepository, times(1)).searchVehicles(isNull(), isNull(), any(Pageable.class));
    }

    @Test
    void testGetAllVehiclesWithTypeFilter() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Vehicle> page = new PageImpl<>(Arrays.asList(vehicle1));
        
        when(vehicleRepository.searchVehicles(eq("SUV"), isNull(), any(Pageable.class))).thenReturn(page);

        Page<VehicleResponseDTO> result = vehicleService.getAllVehicles("SUV", null, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("SUV", result.getContent().get(0).getType());
        verify(vehicleRepository, times(1)).searchVehicles(eq("SUV"), isNull(), any(Pageable.class));
    }

    @Test
    void testGetAllVehiclesWithSearchQuery() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Vehicle> page = new PageImpl<>(Arrays.asList(vehicle1));
        
        when(vehicleRepository.searchVehicles(isNull(), eq("Toyota"), any(Pageable.class))).thenReturn(page);

        Page<VehicleResponseDTO> result = vehicleService.getAllVehicles(null, "Toyota", pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Toyota", result.getContent().get(0).getBrand());
        verify(vehicleRepository, times(1)).searchVehicles(isNull(), eq("Toyota"), any(Pageable.class));
    }

    @Test
    void testGetAllVehiclesWithNullPageable() {
        Page<Vehicle> page = new PageImpl<>(Arrays.asList(vehicle1, vehicle2));
        
        when(vehicleRepository.searchVehicles(isNull(), isNull(), any(Pageable.class))).thenReturn(page);

        Page<VehicleResponseDTO> result = vehicleService.getAllVehicles(null, null, null);

        assertEquals(2, result.getContent().size());
        verify(vehicleRepository, times(1)).searchVehicles(isNull(), isNull(), any(Pageable.class));
    }

    @Test
    void testGetByIdFound() {
        when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle1));

        VehicleResponseDTO result = vehicleService.getById("VEH0001");

        assertNotNull(result);
        assertEquals("VEH0001", result.getId());
        assertEquals("Toyota", result.getBrand());
        assertEquals("Best Rental", result.getVendorName());
        verify(vehicleRepository, times(1)).findById("VEH0001");
    }

    @Test
    void testGetByIdNotFound() {
        when(vehicleRepository.findById("VEHXXX")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.getById("VEHXXX");
        });

        verify(vehicleRepository, times(1)).findById("VEHXXX");
    }

    @Test
    void testCreateVehicleWithoutVendor() {
        CreateVehicleRequestDTO req = new CreateVehicleRequestDTO();
        req.setType("MPV");
        req.setBrand("Suzuki");
        req.setModel("Ertiga");
        req.setProductionYear(2021);
        req.setLocation("Bandung");
        req.setLicensePlate("D5678ABC");
        req.setCapacity(7);
        req.setTransmission("Manual");
        req.setFuelType("Petrol");
        req.setPrice(300000.0);
        req.setStatus("Available");

        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId("VEH0003");
        savedVehicle.setType(req.getType());
        savedVehicle.setBrand(req.getBrand());
        savedVehicle.setModel(req.getModel());

        when(vehicleRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(vehicle2));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        VehicleResponseDTO result = vehicleService.create(req);

        assertNotNull(result);
        assertEquals("VEH0003", result.getId());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testCreateVehicleWithVendor() {
        CreateVehicleRequestDTO req = new CreateVehicleRequestDTO();
        req.setType("SUV");
        req.setBrand("Toyota");
        req.setModel("Fortuner");
        req.setProductionYear(2022);
        req.setPrice(500000.0);
        req.setRentalVendorId(1L);

        when(vehicleRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(vehicle2));
        when(rentalVendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle1);

        VehicleResponseDTO result = vehicleService.create(req);

        assertNotNull(result);
        verify(rentalVendorRepository, times(1)).findById(1L);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testCreateVehicleWithInvalidVendor() {
        CreateVehicleRequestDTO req = new CreateVehicleRequestDTO();
        req.setType("SUV");
        req.setBrand("Toyota");
        req.setModel("Fortuner");
        req.setPrice(500000.0);
        req.setRentalVendorId(999L);

        when(vehicleRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(vehicle2));
        when(rentalVendorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            vehicleService.create(req);
        });
    }

    @Test
    void testCreateFirstVehicle() {
        CreateVehicleRequestDTO req = new CreateVehicleRequestDTO();
        req.setType("SUV");
        req.setBrand("Toyota");
        req.setModel("Fortuner");
        req.setProductionYear(2022);
        req.setPrice(500000.0);

        Vehicle newVehicle = new Vehicle();
        newVehicle.setId("VEH0021");

        when(vehicleRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(newVehicle);

        VehicleResponseDTO result = vehicleService.create(req);

        assertNotNull(result);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testUpdateVehicle() {
        UpdateVehicleRequestDTO req = new UpdateVehicleRequestDTO();
        req.setType("SUV");
        req.setBrand("Toyota");
        req.setModel("Fortuner Updated");
        req.setProductionYear(2023);
        req.setLocation("Jakarta");
        req.setLicensePlate("B1234NEW");
        req.setCapacity(7);
        req.setTransmission("Automatic");
        req.setFuelType("Diesel");
        req.setPrice(550000.0);
        req.setStatus("Available");

        when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle1));
        when(rentalBookingRepository.existsActiveBookingForVehicle("VEH0001")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle1);

        VehicleResponseDTO result = vehicleService.update("VEH0001", req);

        assertNotNull(result);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testUpdateVehicleNotFound() {
        UpdateVehicleRequestDTO req = new UpdateVehicleRequestDTO();
        req.setType("SUV");
        req.setBrand("Toyota");
        req.setModel("Fortuner");
        req.setPrice(500000.0);

        when(vehicleRepository.findById("VEHXXX")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.update("VEHXXX", req);
        });
    }

    @Test
    void testUpdateVehicleWithActiveBooking() {
        UpdateVehicleRequestDTO req = new UpdateVehicleRequestDTO();
        req.setType("SUV");
        req.setBrand("Toyota");
        req.setModel("Fortuner");
        req.setPrice(500000.0);

        when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle1));
        when(rentalBookingRepository.existsActiveBookingForVehicle("VEH0001")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            vehicleService.update("VEH0001", req);
        });
    }

    @Test
    void testUpdateVehicleWithNewVendor() {
        UpdateVehicleRequestDTO req = new UpdateVehicleRequestDTO();
        req.setType("SUV");
        req.setBrand("Toyota");
        req.setModel("Fortuner");
        req.setPrice(500000.0);
        req.setRentalVendorId(2L);

        RentalVendor newVendor = new RentalVendor();
        newVendor.setId(2);
        newVendor.setName("New Rental");

        when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle1));
        when(rentalBookingRepository.existsActiveBookingForVehicle("VEH0001")).thenReturn(false);
        when(rentalVendorRepository.findById(2L)).thenReturn(Optional.of(newVendor));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle1);

        VehicleResponseDTO result = vehicleService.update("VEH0001", req);

        assertNotNull(result);
        verify(rentalVendorRepository, times(1)).findById(2L);
    }

    @Test
    void testUpdateVehicleWithInvalidVendor() {
        UpdateVehicleRequestDTO req = new UpdateVehicleRequestDTO();
        req.setType("SUV");
        req.setBrand("Toyota");
        req.setModel("Fortuner");
        req.setPrice(500000.0);
        req.setRentalVendorId(999L);

        when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle1));
        when(rentalBookingRepository.existsActiveBookingForVehicle("VEH0001")).thenReturn(false);
        when(rentalVendorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            vehicleService.update("VEH0001", req);
        });
    }

    @Test
    void testDeleteVehicle() {
        when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle1));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle1);

        vehicleService.delete("VEH0001");

        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testDeleteVehicleNotFound() {
        when(vehicleRepository.findById("VEHXXX")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.delete("VEHXXX");
        });
    }

    @Test
    void testGetByIdWithoutVendor() {
        vehicle2.setRentalVendor(null);
        when(vehicleRepository.findById("VEH0002")).thenReturn(Optional.of(vehicle2));

        VehicleResponseDTO result = vehicleService.getById("VEH0002");

        assertNotNull(result);
        assertNull(result.getVendorName());
    }

    @Test
    void testCreateVehicleWithInvalidIdFormat() {
        CreateVehicleRequestDTO req = new CreateVehicleRequestDTO();
        req.setType("SUV");
        req.setBrand("Toyota");
        req.setModel("Fortuner");
        req.setPrice(500000.0);

        Vehicle vehicleWithBadId = new Vehicle();
        vehicleWithBadId.setId("BADFORMAT");

        when(vehicleRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(vehicleWithBadId));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> {
            Vehicle v = invocation.getArgument(0);
            v.setId("VEH0021");
            return v;
        });

        VehicleResponseDTO result = vehicleService.create(req);

        assertNotNull(result);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }
}
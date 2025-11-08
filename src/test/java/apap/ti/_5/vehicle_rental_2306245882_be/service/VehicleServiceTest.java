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
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    VehicleRepository vehicleRepository;

    @Mock
    RentalVendorRepository rentalVendorRepository;

    @Mock
    RentalBookingRepository rentalBookingRepository;

    @InjectMocks
    VehicleServiceImpl vehicleService;

    @Test
    void create_success_withVendor() {
        CreateVehicleRequestDTO req = new CreateVehicleRequestDTO();
        req.setType("SUV");
        req.setBrand("TestBrand");
        req.setModel("TestModel");
        req.setProductionYear(2020);
        req.setLocation("Jakarta");
        req.setLicensePlate("B 1 TEST");
        req.setCapacity(5);
        req.setTransmission("Automatic");
        req.setFuelType("Bensin");
        req.setPrice(500000.0);
        req.setStatus("Available");
        req.setRentalVendorId(1L);

        RentalVendor vendor = new RentalVendor();
        vendor.setId(Integer.valueOf(1));
        vendor.setName("Vendor A");

        when(vehicleRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());
        when(rentalVendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        ArgumentCaptor<Vehicle> cap = ArgumentCaptor.forClass(Vehicle.class);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));

        VehicleResponseDTO res = vehicleService.create(req);

        assertNotNull(res);
        assertEquals("TestBrand", res.getBrand());
        verify(vehicleRepository).save(cap.capture());
        Vehicle saved = cap.getValue();
        assertTrue(saved.getId().startsWith("VEH"));
        assertEquals(vendor.getName(), res.getVendorName());
    }

    @Test
    void create_vendorNotFound_throws() {
        CreateVehicleRequestDTO req = new CreateVehicleRequestDTO();
        req.setType("Sedan");
        req.setBrand("A");
        req.setModel("M");
        req.setProductionYear(2019);
        req.setLicensePlate("B 2");
        req.setTransmission("Manual");
        req.setFuelType("Bensin");
        req.setPrice(100000.0);
        req.setRentalVendorId(99L);

        when(vehicleRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());
        when(rentalVendorRepository.findById(99L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> vehicleService.create(req));
        assertTrue(ex.getMessage().contains("rentalVendorId not found"));
    }

    @Test
    void update_whenActiveBooking_throws() {
        String id = "VEH0001";
        UpdateVehicleRequestDTO req = new UpdateVehicleRequestDTO();
        req.setType("SUV");
        req.setBrand("B");
        req.setModel("M");
        req.setProductionYear(2020);
        req.setLocation("L");
        req.setLicensePlate("X");
        req.setCapacity(4);
        req.setTransmission("Automatic");
        req.setFuelType("Bensin");
        req.setPrice(200000.0);
        req.setStatus("Available");

        Vehicle v = new Vehicle();
        v.setId(id);

        when(vehicleRepository.findById(id)).thenReturn(Optional.of(v));
        when(rentalBookingRepository.existsActiveBookingForVehicle(id)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> vehicleService.update(id, req));
        assertTrue(ex.getMessage().toLowerCase().contains("sedang disewa"));
    }

    @Test
    void delete_setsDeletedTrue() {
        String id = "VEHX";
        Vehicle v = new Vehicle();
        v.setId(id);
        v.setDeleted(false);

        when(vehicleRepository.findById(id)).thenReturn(Optional.of(v));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));

        vehicleService.delete(id);

        assertTrue(v.isDeleted());
        verify(vehicleRepository).save(v);
    }

    @Test
    void getAllVehicles_callsRepo() {
        Pageable p = PageRequest.of(0, 10);
        Vehicle veh = new Vehicle();
        veh.setId("VEH0001");
        Page<Vehicle> page = new PageImpl<>(java.util.List.of(veh));
        when(vehicleRepository.searchVehicles(null, null, p)).thenReturn(page);

        Page<VehicleResponseDTO> res = vehicleService.getAllVehicles(null, null, p);
        assertEquals(1, res.getTotalElements());
        assertEquals("VEH0001", res.getContent().get(0).getId());
    }
}

package apap.ti._5.vehicle_rental_2306245882_be.restcontroller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.SearchResultVehicleDTO;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.service.BadRequestException;
import apap.ti._5.vehicle_rental_2306245882_be.service.RentalBookingService;
import apap.ti._5.vehicle_rental_2306245882_be.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
  controllers = {
    HomeRestController.class,
    VehicleRestController.class,
    RentalBookingRestController.class,
  }
)
class RestControllersTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private VehicleRepository vehicleRepository;

  @MockitoBean
  private RentalVendorRepository rentalVendorRepository;

  @MockitoBean
  private RentalBookingRepository rentalBookingRepository;

  @MockitoBean
  private VehicleService vehicleService;

  @MockitoBean
  private RentalBookingService bookingService;

  private VehicleResponseDTO vehicleDTO;
  private RentalBooking booking;

  @BeforeEach
  void setUp() {
    vehicleDTO = new VehicleResponseDTO();
    vehicleDTO.setId("VEH0001");
    vehicleDTO.setType("SUV");
    vehicleDTO.setBrand("Toyota");
    vehicleDTO.setModel("Fortuner");
    vehicleDTO.setCapacity(7);
    vehicleDTO.setStatus("Available");
    vehicleDTO.setPrice(500000.0);

    booking = new RentalBooking();
    booking.setId("VR000001");
    booking.setVehicleId("VEH0001");
    booking.setStatus(RentalBooking.BookingStatus.Upcoming);
    booking.setTotalPrice(1000000.0);
  }

  // ========== HOME REST CONTROLLER TESTS ==========
  @Test
  void testGetHomeStats() throws Exception {
    when(vehicleRepository.count()).thenReturn(10L);
    when(rentalVendorRepository.count()).thenReturn(5L);
    when(rentalBookingRepository.count()).thenReturn(20L);

    mockMvc
      .perform(get("/api/home"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(200))
      .andExpect(jsonPath("$.message").value("Success"))
      .andExpect(jsonPath("$.data.totalVehicles").value(10))
      .andExpect(jsonPath("$.data.totalVendors").value(5))
      .andExpect(jsonPath("$.data.totalBookings").value(20));
  }

  // ========== VEHICLE REST CONTROLLER TESTS ==========
  @Test
  void testGetAllVehiclesAPI() throws Exception {
    Page<VehicleResponseDTO> page = new PageImpl<>(Arrays.asList(vehicleDTO));
    when(vehicleService.getAllVehicles(any(), any(), any(Pageable.class)))
      .thenReturn(page);

    mockMvc
      .perform(get("/api/vehicles").param("page", "0").param("size", "20"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(200))
      .andExpect(jsonPath("$.data[0].id").value("VEH0001"))
      .andExpect(jsonPath("$.data[0].brand").value("Toyota"));
  }

  @Test
  void testGetAllVehiclesWithFilters() throws Exception {
    Page<VehicleResponseDTO> page = new PageImpl<>(Arrays.asList(vehicleDTO));
    when(
      vehicleService.getAllVehicles(
        eq("SUV"),
        eq("Toyota"),
        any(Pageable.class)
      )
    )
      .thenReturn(page);

    mockMvc
      .perform(
        get("/api/vehicles").param("type", "SUV").param("search", "Toyota")
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data[0].type").value("SUV"));
  }

  @Test
  void testGetVehicleByIdAPI() throws Exception {
    when(vehicleService.getById("VEH0001")).thenReturn(vehicleDTO);

    mockMvc
      .perform(get("/api/vehicles/VEH0001"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(200))
      .andExpect(jsonPath("$.data.id").value("VEH0001"))
      .andExpect(jsonPath("$.data.brand").value("Toyota"));
  }

  // ========== RENTAL BOOKING REST CONTROLLER TESTS ==========
  @Test
  void testSearchVehiclesAPI() throws Exception {
    SearchResultVehicleDTO searchResult = new SearchResultVehicleDTO();
    searchResult.setId("VEH0001");
    searchResult.setBrand("Toyota");
    searchResult.setModel("Fortuner");
    searchResult.setTotalPrice(1000000.0);

    CreateRentalBookingRequestDTO req = new CreateRentalBookingRequestDTO();
    req.setPickUpTime(LocalDateTime.now().plusDays(1));
    req.setDropOffTime(LocalDateTime.now().plusDays(3));
    req.setPickUpLocation("Jakarta");
    req.setDropOffLocation("Bandung");

    when(
      bookingService.findAvailableVehicles(
        any(CreateRentalBookingRequestDTO.class)
      )
    )
      .thenReturn(Arrays.asList(searchResult));

    mockMvc
      .perform(
        post("/api/bookings/search")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(req))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value("VEH0001"))
      .andExpect(jsonPath("$[0].totalPrice").value(1000000.0));
  }

  @Test
  void testGetBookingByIdAPI() throws Exception {
    when(bookingService.getById("VR000001")).thenReturn(booking);

    mockMvc
      .perform(get("/api/bookings/VR000001"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value("VR000001"))
      .andExpect(jsonPath("$.vehicleId").value("VEH0001"));
  }

  @Test
  void testApiCancelBookingSuccess() throws Exception {
    when(bookingService.cancelBooking("VR000001")).thenReturn(booking);

    mockMvc
      .perform(
        delete("/api/bookings/VR000001/delete")
          .header("Accept", "application/json")
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status", is("success")))
      .andExpect(jsonPath("$.id", is("VR000001")));
  }

  @Test
  void testApiCancelBookingBadRequest() throws Exception {
    when(bookingService.cancelBooking("VR000001"))
      .thenThrow(new BadRequestException("Cannot cancel booking"));

    mockMvc
      .perform(
        delete("/api/bookings/VR000001/delete")
          .header("Accept", "application/json")
      )
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status", is("error")))
      .andExpect(jsonPath("$.message", is("Cannot cancel booking")));
  }

  @Test
  void testApiCancelBookingInternalError() throws Exception {
    when(bookingService.cancelBooking("VR000001"))
      .thenThrow(new RuntimeException("Internal error"));

    mockMvc
      .perform(
        delete("/api/bookings/VR000001/delete")
          .header("Accept", "application/json")
      )
      .andExpect(status().isInternalServerError())
      .andExpect(jsonPath("$.status", is("error")));
  }

  @Test
  void testGetAllVehiclesWithDefaultPagination() throws Exception {
    Page<VehicleResponseDTO> page = new PageImpl<>(Arrays.asList(vehicleDTO));
    when(vehicleService.getAllVehicles(any(), any(), any(Pageable.class)))
      .thenReturn(page);

    mockMvc
      .perform(get("/api/vehicles"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(200))
      .andExpect(jsonPath("$.data").isArray());
  }

  @Test
  void testSearchVehiclesEmptyResult() throws Exception {
    CreateRentalBookingRequestDTO req = new CreateRentalBookingRequestDTO();
    req.setPickUpTime(LocalDateTime.now().plusDays(1));
    req.setDropOffTime(LocalDateTime.now().plusDays(3));

    when(
      bookingService.findAvailableVehicles(
        any(CreateRentalBookingRequestDTO.class)
      )
    )
      .thenReturn(Arrays.asList());

    mockMvc
      .perform(
        post("/api/bookings/search")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(req))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void testGetHomeStatsWithZeroCounts() throws Exception {
    when(vehicleRepository.count()).thenReturn(0L);
    when(rentalVendorRepository.count()).thenReturn(0L);
    when(rentalBookingRepository.count()).thenReturn(0L);

    mockMvc
      .perform(get("/api/home"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.totalVehicles").value(0))
      .andExpect(jsonPath("$.data.totalVendors").value(0))
      .andExpect(jsonPath("$.data.totalBookings").value(0));
  }
}

package apap.ti._5.vehicle_rental_2306245882_be.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class RentalBookingTest {

  @Test
  void testRentalBookingGettersSetters() {
    RentalBooking booking = new RentalBooking();
    booking.setId("VR000001");
    booking.setVehicleId("VEH0001");
    booking.setPickUpTime(LocalDateTime.now());
    booking.setDropOffTime(LocalDateTime.now().plusDays(2));
    booking.setPickUpLocation("Jakarta");
    booking.setDropOffLocation("Bandung");
    booking.setCapacityNeeded(5);
    booking.setTransmissionNeeded("Automatic");
    booking.setTotalPrice(1000000.0);
    booking.setIncludeDriver(true);
    booking.setStatus(RentalBooking.BookingStatus.Upcoming);
    booking.setDeleted(false);

    assertEquals("VR000001", booking.getId());
    assertEquals("VEH0001", booking.getVehicleId());
    assertEquals("Jakarta", booking.getPickUpLocation());
    assertEquals("Bandung", booking.getDropOffLocation());
    assertEquals(5, booking.getCapacityNeeded());
    assertEquals("Automatic", booking.getTransmissionNeeded());
    assertEquals(1000000.0, booking.getTotalPrice());
    assertTrue(booking.getIncludeDriver());
    assertEquals(RentalBooking.BookingStatus.Upcoming, booking.getStatus());
    assertFalse(booking.isDeleted());
  }

  @Test
  void testRentalBookingBuilder() {
    RentalBooking booking = RentalBooking
      .builder()
      .id("VR000002")
      .vehicleId("VEH0002")
      .totalPrice(500000.0)
      .status(RentalBooking.BookingStatus.Ongoing)
      .deleted(false)
      .build();

    assertEquals("VR000002", booking.getId());
    assertEquals("VEH0002", booking.getVehicleId());
    assertEquals(500000.0, booking.getTotalPrice());
    assertEquals(RentalBooking.BookingStatus.Ongoing, booking.getStatus());
  }

  @Test
  void testPrePersist() {
    RentalBooking booking = new RentalBooking();
    booking.prePersist();

    assertNotNull(booking.getId());
    assertNotNull(booking.getCreatedAt());
    assertNotNull(booking.getUpdatedAt());
  }

  @Test
  void testPreUpdate() {
    RentalBooking booking = new RentalBooking();
    booking.setCreatedAt(LocalDateTime.now().minusDays(1));
    LocalDateTime oldUpdated = booking.getUpdatedAt();

    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {}

    booking.preUpdate();

    if (oldUpdated != null) {
      assertTrue(
        booking.getUpdatedAt().isAfter(oldUpdated) ||
        booking.getUpdatedAt().equals(oldUpdated)
      );
    }
  }
}

// ==================== VEHICLE MODEL TEST ====================
class VehicleTest {

  @Test
  void testVehicleGettersSetters() {
    Vehicle vehicle = new Vehicle();
    vehicle.setId("VEH0001");
    vehicle.setType("SUV");
    vehicle.setBrand("Toyota");
    vehicle.setModel("Fortuner");
    vehicle.setProductionYear(2022);
    vehicle.setLocation("Jakarta");
    vehicle.setLicensePlate("B1234XYZ");
    vehicle.setCapacity(7);
    vehicle.setTransmission("Automatic");
    vehicle.setFuelType("Diesel");
    vehicle.setPrice(500000.0);
    vehicle.setStatus("Available");
    vehicle.setDeleted(false);

    assertEquals("VEH0001", vehicle.getId());
    assertEquals("SUV", vehicle.getType());
    assertEquals("Toyota", vehicle.getBrand());
    assertEquals("Fortuner", vehicle.getModel());
    assertEquals(2022, vehicle.getProductionYear());
    assertEquals("Jakarta", vehicle.getLocation());
    assertEquals("B1234XYZ", vehicle.getLicensePlate());
    assertEquals(7, vehicle.getCapacity());
    assertEquals("Automatic", vehicle.getTransmission());
    assertEquals("Diesel", vehicle.getFuelType());
    assertEquals(500000.0, vehicle.getPrice());
    assertEquals("Available", vehicle.getStatus());
    assertFalse(vehicle.isDeleted());
  }

  @Test
  void testVehicleBuilder() {
    Vehicle vehicle = Vehicle
      .builder()
      .id("VEH0002")
      .type("Sedan")
      .brand("Honda")
      .model("Civic")
      .price(350000.0)
      .deleted(false)
      .build();

    assertEquals("VEH0002", vehicle.getId());
    assertEquals("Sedan", vehicle.getType());
    assertEquals("Honda", vehicle.getBrand());
    assertFalse(vehicle.isDeleted());
  }

  @Test
  void testPrePersist() {
    Vehicle vehicle = new Vehicle();
    vehicle.prePersist();

    assertNotNull(vehicle.getId());
    assertNotNull(vehicle.getCreatedAt());
  }

  @Test
  void testPreUpdate() {
    Vehicle vehicle = new Vehicle();
    vehicle.preUpdate();

    assertNotNull(vehicle.getUpdatedAt());
  }
}

// ==================== RENTAL ADDON TEST ====================
class RentalAddOnTest {

  @Test
  void testRentalAddOnGettersSetters() {
    RentalAddOn addon = new RentalAddOn();
    addon.setName("GPS");
    addon.setPrice(50000.0);

    assertEquals("GPS", addon.getName());
    assertEquals(50000.0, addon.getPrice());
  }

  @Test
  void testRentalAddOnBuilder() {
    RentalAddOn addon = RentalAddOn
      .builder()
      .name("Child Seat")
      .price(30000.0)
      .build();

    assertEquals("Child Seat", addon.getName());
    assertEquals(30000.0, addon.getPrice());
  }

  @Test
  void testPrePersist() {
    RentalAddOn addon = new RentalAddOn();
    addon.prePersist();

    assertNotNull(addon.getCreatedAt());
  }

  @Test
  void testPreUpdate() {
    RentalAddOn addon = new RentalAddOn();
    addon.preUpdate();

    assertNotNull(addon.getUpdatedAt());
  }
}

// ==================== RENTAL VENDOR TEST ====================
class RentalVendorTest {

  @Test
  void testRentalVendorGettersSetters() {
    RentalVendor vendor = new RentalVendor();
    vendor.setId(1);
    vendor.setName("Best Rental");
    vendor.setEmail("best@rental.com");
    vendor.setPhone("08123456789");

    assertEquals(1, vendor.getId());
    assertEquals("Best Rental", vendor.getName());
    assertEquals("best@rental.com", vendor.getEmail());
    assertEquals("08123456789", vendor.getPhone());
  }

  @Test
  void testRentalVendorBuilder() {
    RentalVendor vendor = RentalVendor
      .builder()
      .id(2)
      .name("Super Rental")
      .email("super@rental.com")
      .phone("08198765432")
      .build();

    assertEquals(2, vendor.getId());
    assertEquals("Super Rental", vendor.getName());
  }
}

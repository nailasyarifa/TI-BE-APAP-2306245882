package apap.ti._5.vehicle_rental_2306245882_be.service;

import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.BookingListItem;
import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.SearchResultVehicleDTO;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245882_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateRentalBookingRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class RentalBookingServiceImpl implements RentalBookingService {

    private static final double DRIVER_COST_PER_DAY = 100_000.0;

    private final VehicleRepository vehicleRepository;
    private final RentalVendorRepository rentalVendorRepository;
    private final RentalAddOnRepository addOnRepository;
    private final RentalBookingRepository bookingRepository;

    @Autowired
    public RentalBookingServiceImpl(VehicleRepository vehicleRepository,
                                    RentalVendorRepository rentalVendorRepository,
                                    RentalAddOnRepository addOnRepository,
                                    RentalBookingRepository bookingRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalVendorRepository = rentalVendorRepository;
        this.addOnRepository = addOnRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public RentalBooking createRentalBooking(CreateRentalBookingRequestDTO req) {
        // validasi vehicle exists
        Vehicle vehicle = vehicleRepository.findById(req.vehicleId())
                .orElseThrow(() -> new BadRequestException("Vehicle not found: " + req.vehicleId()));

        // cek status available
        if (vehicle.getStatus() == null || !"Available".equalsIgnoreCase(vehicle.getStatus())) {
            throw new BadRequestException("Vehicle not available");
        }

        // cek kapasitas
        if (req.capacityNeeded() != null && vehicle.getCapacity() != null &&
            req.capacityNeeded() > vehicle.getCapacity()) {
            throw new BadRequestException("Vehicle capacity smaller than needed");
        }

        // cek transmission
        if (req.transmissionNeeded() != null && vehicle.getTransmission() != null &&
            !req.transmissionNeeded().equalsIgnoreCase(vehicle.getTransmission())) {
            throw new BadRequestException("Vehicle transmission doesn't match requirement");
        }

        // cek vendor locations (jika ada vendor)
        if (vehicle.getRentalVendor() != null) {
            RentalVendor vendor = vehicle.getRentalVendor();
            List<String> locations = vendor.getListOfLocations() == null ? Collections.emptyList() : vendor.getListOfLocations();
            if (!locations.contains(req.pickUpLocation()) || !locations.contains(req.dropOffLocation())) {
                throw new BadRequestException("Vendor doesn't operate in given pickup/dropoff locations");
            }
        }

        // cek overlapping booking (pastikan repository punya method ini)
        List<RentalBooking> overlapping = bookingRepository.findOverlappingBookings(req.vehicleId(), req.pickUpTime(), req.dropOffTime());
        if (overlapping != null && !overlapping.isEmpty()) {
            throw new BadRequestException("Vehicle already booked for chosen period");
        }

        // hitung durasi (round up ke hari)
        long hours = Duration.between(req.pickUpTime(), req.dropOffTime()).toHours();
        if (hours <= 0) throw new BadRequestException("Drop off must be after pick up");
        long days = (hours + 23) / 24; // round up

        double basePrice = vehicle.getPrice() == null ? 0.0 : vehicle.getPrice();
        double vehicleCost = days * basePrice;

        double driverCost = (req.includeDriver() != null && req.includeDriver()) ? (days * DRIVER_COST_PER_DAY) : 0.0;

        // load addons dan hitung harga addon per hari * days
        List<RentalAddOn> selectedAddOns = new ArrayList<>();
        double addonsCost = 0.0;
        if (req.addonIds() != null && !req.addonIds().isEmpty()) {
            // konversi List<String> -> List<UUID> sebelum memanggil repository
            List<UUID> addonUuids;
            try {
                addonUuids = req.addonIds().stream()
                        .filter(Objects::nonNull)
                        .map(UUID::fromString)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("One or more addonIds are not valid UUIDs");
            }

            // repository harus menyediakan findAllById(Iterable<UUID>)
            selectedAddOns = addOnRepository.findAllById(addonUuids);
            for (RentalAddOn a : selectedAddOns) {
                addonsCost += (a.getPrice() == null ? 0.0 : a.getPrice()) * days;
            }
        }

        double total = vehicleCost + driverCost + addonsCost;

        RentalBooking booking = RentalBooking.builder()
                .vehicleId(req.vehicleId())
                .pickUpTime(req.pickUpTime())
                .dropOffTime(req.dropOffTime())
                .pickUpLocation(req.pickUpLocation())
                .dropOffLocation(req.dropOffLocation())
                .capacityNeeded(req.capacityNeeded())
                .transmissionNeeded(req.transmissionNeeded())
                .includeDriver(req.includeDriver() == null ? false : req.includeDriver())
                .listOfAddOns(selectedAddOns)
                .totalPrice(total)
                .status(RentalBooking.BookingStatus.UPCOMING)
                .build();

        return bookingRepository.save(booking);
    }

    @Override
    public List<SearchResultVehicleDTO> findAvailableVehicles(CreateRentalBookingRequestDTO req) {
        if (req.pickUpTime() == null || req.dropOffTime() == null) {
            throw new BadRequestException("Pick-up and Drop-off time required");
        }
        if (!req.pickUpTime().isBefore(req.dropOffTime())) {
            throw new BadRequestException("Pick-up must be before drop-off");
        }

        long hours = Duration.between(req.pickUpTime(), req.dropOffTime()).toHours();
        long days = (hours <= 0) ? 0 : ((hours + 23) / 24);
        if (days <= 0) throw new BadRequestException("Duration must be at least 1 day");

        List<Vehicle> all = vehicleRepository.findAll();
        List<SearchResultVehicleDTO> out = new ArrayList<>();

        for (Vehicle v : all) {
            if (v.getStatus() == null || !v.getStatus().equalsIgnoreCase("Available")) continue;
            if (req.capacityNeeded() != null && (v.getCapacity() == null || v.getCapacity() < req.capacityNeeded())) continue;
            if (req.transmissionNeeded() != null && v.getTransmission() != null &&
                    !v.getTransmission().equalsIgnoreCase(req.transmissionNeeded())) continue;
            // vendor locations
            if (v.getRentalVendor() != null) {
                List<String> vendorLocs = v.getRentalVendor().getListOfLocations();
                if (vendorLocs == null || !vendorLocs.contains(req.pickUpLocation()) || !vendorLocs.contains(req.dropOffLocation())) {
                    continue;
                }
            }
            // overlapping bookings
            List<RentalBooking> overlapping = bookingRepository.findOverlappingBookings(v.getId(), req.pickUpTime(), req.dropOffTime());
            if (overlapping != null && !overlapping.isEmpty()) continue;

            double base = v.getPrice() == null ? 0.0 : v.getPrice();
            double vehicleCost = days * base;
            double driverCost = (req.includeDriver() != null && req.includeDriver()) ? (days * DRIVER_COST_PER_DAY) : 0.0;
            double total = vehicleCost + driverCost;

            SearchResultVehicleDTO dto = new SearchResultVehicleDTO();
            dto.setId(v.getId());
            dto.setBrand(v.getBrand());
            dto.setModel(v.getModel());
            dto.setType(v.getType());
            dto.setCapacity(v.getCapacity());
            dto.setTransmission(v.getTransmission());
            dto.setPricePerDay(base);
            dto.setTotalPrice(total);

            out.add(dto);
        }

        // sort ascending by totalPrice
        out.sort(Comparator.comparing(SearchResultVehicleDTO::getTotalPrice));
        return out;
    }

    @Override
    public List<BookingListItem> getAllBookingsForList() {
        List<RentalBooking> all = bookingRepository.findAll();
        // urutkan descending berdasarkan createdAt jika ada, jika tidak, skip
        all.sort(Comparator.comparing(RentalBooking::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        List<BookingListItem> list = new ArrayList<>();
        for (RentalBooking b : all) {
            BookingListItem item = new BookingListItem();
            item.setId(b.getId());
            item.setVehicleId(b.getVehicleId());
            item.setPickUpTime(b.getPickUpTime());
            item.setDropOffTime(b.getDropOffTime());
            item.setPickUpLocation(b.getPickUpLocation());
            item.setStatus(b.getStatus() != null ? b.getStatus().name() : null);
            item.setTotalPrice(b.getTotalPrice());
            list.add(item);
        }
        return list;
    }

    @Override
    public RentalBooking getById(String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalBooking not found with id: " + id));
    }
}

package apap.ti._5.vehicle_rental_2306245882_be.service;

import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.BookingListItem;
import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.SearchResultVehicleDTO;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking.BookingStatus;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245882_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.UpdateRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateRentalBookingRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RentalBookingServiceImpl implements RentalBookingService {

    private static final double DRIVER_COST_PER_DAY = 100_000.0;

    private final VehicleRepository vehicleRepository;
    private final RentalVendorRepository rentalVendorRepository;
    private final RentalAddOnRepository addOnRepository;

    @Autowired
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
        // ----- sanitize vehicleId: handle cases like "VEH0003,VEH0003" or ",VEH0003" -----
        if (req.getVehicleId() == null || req.getVehicleId().isBlank()) {
            throw new BadRequestException("Vehicle id is required");
        }
        String rawVehicleId = req.getVehicleId();
    
        // use AtomicReference so variable can be mutated inside loops/lambdas without "effectively final" error
        java.util.concurrent.atomic.AtomicReference<String> chosenVehicleRef = new java.util.concurrent.atomic.AtomicReference<>(null);
    
        String[] parts = rawVehicleId.split(",");
        for (String p : parts) {
            String t = p == null ? "" : p.trim();
            if (t.isEmpty()) continue;
            // prefer the first id that actually exists in repo
            if (vehicleRepository.existsById(t)) {
                chosenVehicleRef.set(t);
                break;
            }
            // fallback to first non-empty if repo doesn't have any matches
            if (chosenVehicleRef.get() == null) chosenVehicleRef.set(t);
        }
    
        String chosenVehicleId = chosenVehicleRef.get();
        if (chosenVehicleId == null || chosenVehicleId.isBlank()) {
            throw new BadRequestException("Vehicle id is invalid: " + rawVehicleId);
        }
    
        // validasi vehicle exists (pakai chosenVehicleId yang sudah disanitasi)
        Vehicle vehicle = vehicleRepository.findById(chosenVehicleId)
                .orElseThrow(() -> new BadRequestException("Vehicle not found: " + chosenVehicleId));
    
        // cek status available
        if (vehicle.getStatus() == null || !"Available".equalsIgnoreCase(vehicle.getStatus())) {
            throw new BadRequestException("Vehicle not available");
        }
    
        // cek kapasitas
        if (req.getCapacityNeeded() != null && vehicle.getCapacity() != null &&
            req.getCapacityNeeded() > vehicle.getCapacity()) {
            throw new BadRequestException("Vehicle capacity smaller than needed");
        }
    
        // cek transmission
        if (req.getTransmissionNeeded() != null && vehicle.getTransmission() != null &&
            !req.getTransmissionNeeded().equalsIgnoreCase(vehicle.getTransmission())) {
            throw new BadRequestException("Vehicle transmission doesn't match requirement");
        }
    
        // cek vendor locations (jika ada vendor)
        if (vehicle.getRentalVendor() != null) {
            RentalVendor vendor = vehicle.getRentalVendor();
            List<String> locations = vendor.getListOfLocations() == null ? Collections.emptyList() : vendor.getListOfLocations();
            if (!locations.contains(req.getPickUpLocation()) || !locations.contains(req.getDropOffLocation())) {
                throw new BadRequestException("Vendor doesn't operate in given pickup/dropoff locations");
            }
        }
    
        // cek overlapping booking (pakai chosenVehicleId)
        List<RentalBooking> overlapping = bookingRepository.findOverlappingBookings(chosenVehicleId, req.getPickUpTime(), req.getDropOffTime());
        if (overlapping != null && !overlapping.isEmpty()) {
            throw new BadRequestException("Vehicle already booked for chosen period");
        }
    
        // hitung durasi (round up ke hari)
        long hours = Duration.between(req.getPickUpTime(), req.getDropOffTime()).toHours();
        if (hours <= 0) throw new BadRequestException("Drop off must be after pick up");
        long days = (hours + 23) / 24; // round up
    
        double basePrice = vehicle.getPrice() == null ? 0.0 : vehicle.getPrice();
        double vehicleCost = days * basePrice;
        double driverCost = (req.getIncludeDriver() != null && req.getIncludeDriver()) ? (days * DRIVER_COST_PER_DAY) : 0.0;
    
        // load addons dan hitung harga addon per hari * days
        List<RentalAddOn> selectedAddOns = new ArrayList<>();
        double addonsCost = 0.0;
        if (req.getAddonIds() != null && !req.getAddonIds().isEmpty()) {
            List<UUID> addonUuids;
            try {
                addonUuids = req.getAddonIds().stream()
                        .filter(Objects::nonNull)
                        .map(UUID::fromString)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("One or more addonIds are not valid UUIDs");
            }
    
            selectedAddOns = addOnRepository.findAllById(addonUuids);
            for (RentalAddOn a : selectedAddOns) {
                addonsCost += (a.getPrice() == null ? 0.0 : a.getPrice()) * days;
            }
        }
    
        double total = vehicleCost + driverCost + addonsCost;
    
        RentalBooking booking = RentalBooking.builder()
                .vehicleId(chosenVehicleId) // gunakan yang sudah disanitasi
                .pickUpTime(req.getPickUpTime())
                .dropOffTime(req.getDropOffTime())
                .pickUpLocation(req.getPickUpLocation())
                .dropOffLocation(req.getDropOffLocation())
                .capacityNeeded(req.getCapacityNeeded())
                .transmissionNeeded(req.getTransmissionNeeded())
                .includeDriver(req.getIncludeDriver() == null ? false : req.getIncludeDriver())
                .listOfAddOns(selectedAddOns)
                .totalPrice(total)
                .status(RentalBooking.BookingStatus.UPCOMING)
                .build();
    
        // ----- set booking id sesuai requirement "VRxxxxxx" agar muat di varchar(16) -----
        long next = bookingRepository.count() + 1; // simple untuk tugas/demo
        String formatted = String.format("VR%06d", next); // VR000001
        booking.setId(formatted);
    
        return bookingRepository.save(booking);
    }
    


    @Override
    public List<SearchResultVehicleDTO> findAvailableVehicles(CreateRentalBookingRequestDTO req) {
        if (req.getPickUpTime() == null || req.getDropOffTime() == null) {
            throw new BadRequestException("Pick-up and Drop-off time required");
        }
        if (!req.getPickUpTime().isBefore(req.getDropOffTime())) {
            throw new BadRequestException("Pick-up must be before drop-off");
        }

        long hours = Duration.between(req.getPickUpTime(), req.getDropOffTime()).toHours();
        long days = (hours <= 0) ? 0 : ((hours + 23) / 24);
        if (days <= 0) throw new BadRequestException("Duration must be at least 1 day");

        List<Vehicle> all = vehicleRepository.findAll();
        List<SearchResultVehicleDTO> out = new ArrayList<>();

        for (Vehicle v : all) {
            if (v.getStatus() == null || !v.getStatus().equalsIgnoreCase("Available")) continue;
            if (req.getCapacityNeeded() != null && (v.getCapacity() == null || v.getCapacity() < req.getCapacityNeeded())) continue;
            if (req.getTransmissionNeeded() != null && v.getTransmission() != null &&
                    !v.getTransmission().equalsIgnoreCase(req.getTransmissionNeeded())) continue;
            // vendor locations
            if (v.getRentalVendor() != null) {
                List<String> vendorLocs = v.getRentalVendor().getListOfLocations();
                if (vendorLocs == null || !vendorLocs.contains(req.getPickUpLocation()) || !vendorLocs.contains(req.getDropOffLocation())) {
                    continue;
                }
            }
            // overlapping bookings
            List<RentalBooking> overlapping = bookingRepository.findOverlappingBookings(v.getId(), req.getPickUpTime(), req.getDropOffTime());
            if (overlapping != null && !overlapping.isEmpty()) continue;

            double base = v.getPrice() == null ? 0.0 : v.getPrice();
            double vehicleCost = days * base;
            double driverCost = (req.getIncludeDriver() != null && req.getIncludeDriver()) ? (days * DRIVER_COST_PER_DAY) : 0.0;
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
        // List<RentalBooking> all = bookingRepository.findAll();
        // all.sort(Comparator.comparing(RentalBooking::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        List<RentalBooking> all = bookingRepository.findAllActive();
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

    @Transactional
    @Override
    public RentalBooking updateBookingDetails(UpdateRentalBookingRequestDTO dto) throws BadRequestException {
        Optional<RentalBooking> ob = bookingRepository.findById(dto.getId());
        if (ob.isEmpty()) {
            throw new BadRequestException("Booking tidak ditemukan: " + dto.getId());
        }
        RentalBooking booking = ob.get();

        String status = "";
        try { status = booking.getStatus() == null ? "" : booking.getStatus().toString(); } catch (Exception ignored) {}
        if (!"Upcoming".equalsIgnoreCase(status)) {
            throw new BadRequestException("Booking hanya dapat diubah ketika status 'Upcoming'.");
        }

        // 3) validasi waktu
        if (dto.getPickUpTime() == null || dto.getDropOffTime() == null || !dto.getPickUpTime().isBefore(dto.getDropOffTime())) {
            throw new BadRequestException("Pick-up time harus sebelum Drop-off time.");
        }

        // 4) cek vehicle exists
        if (dto.getVehicleId() == null || dto.getVehicleId().isBlank()) {
            throw new BadRequestException("Pilih kendaraan.");
        }
        Optional<Vehicle> ov = vehicleRepository.findById(dto.getVehicleId());
        if (ov.isEmpty()) throw new BadRequestException("Vehicle tidak ditemukan: " + dto.getVehicleId());
        Vehicle vehicle = ov.get();

        // 5) cek overlapping bookings (pakai repo yang ada, lalu exclude current booking)
        List<RentalBooking> overlaps = bookingRepository.findOverlappingBookings(dto.getVehicleId(), dto.getPickUpTime(), dto.getDropOffTime());
        if (overlaps != null && !overlaps.isEmpty()) {
            // remove this booking itself (if present) and check again
            overlaps.removeIf(b -> b.getId().equals(booking.getId()));
            if (!overlaps.isEmpty()) {
                throw new BadRequestException("Kendaraan sudah dipesan pada rentang waktu yang dipilih.");
            }
        }

        // 6) hitung days (ceil hours/24)
        long hours = ChronoUnit.HOURS.between(dto.getPickUpTime(), dto.getDropOffTime());
        long days = (hours > 0) ? ((hours + 23) / 24) : 0;

        // 7) ambil price per day dari vehicle (pakai vehicle.getPrice())
        double pricePerDay = vehicle.getPrice() == null ? 0.0 : vehicle.getPrice();
        double vehiclePrice = pricePerDay * days;

        // 8) hitung addons total (pakai addOnRepository)
        double addonsTotal = 0.0;
        List<RentalAddOn> newAddons = new ArrayList<>();
        if (dto.getAddonIds() != null && !dto.getAddonIds().isEmpty()) {
            List<UUID> addonUuids = new ArrayList<>();
            for (String aid : dto.getAddonIds()) {
                try {
                    addonUuids.add(UUID.fromString(aid));
                } catch (IllegalArgumentException ex) {
                    // skip invalid uuid strings
                }
            }
            if (!addonUuids.isEmpty()) {
                List<RentalAddOn> found = addOnRepository.findAllById(addonUuids);
                newAddons.addAll(found);
                for (RentalAddOn a : found) {
                    double p = a.getPrice() == null ? 0.0 : a.getPrice();
                    addonsTotal += p * days; // note: in create you multiplied addon price by days
                }
            }
        }

        // driver cost if included (consistent with create)
        double driverCost = (dto.getIncludeDriver() != null && dto.getIncludeDriver()) ? (days * DRIVER_COST_PER_DAY) : 0.0;

        double total = vehiclePrice + addonsTotal + driverCost;

        // 9) update booking fields (defensive)
        // set vehicleId (entity uses vehicleId string as in create)
        try { booking.setVehicleId(dto.getVehicleId()); } catch (Exception ignored) {}

        booking.setPickUpTime(dto.getPickUpTime());
        booking.setDropOffTime(dto.getDropOffTime());
        booking.setPickUpLocation(dto.getPickUpLocation());
        booking.setDropOffLocation(dto.getDropOffLocation());
        booking.setCapacityNeeded(dto.getCapacityNeeded());
        booking.setIncludeDriver(dto.getIncludeDriver() == null ? false : dto.getIncludeDriver());
        // set transmissionNeeded (entity uses transmissionNeeded in create)
        try { booking.setTransmissionNeeded(dto.getTransmission()); } catch (Exception ignored) {}
        // totalPrice in entity is double (as used in create), so set double
        try { booking.setTotalPrice(total); } catch (Exception ignored) {}

        // 10) update addons relation: clear & add new list (entity keeps listOfAddOns)
        try {
            if (booking.getListOfAddOns() == null) {
                booking.setListOfAddOns(new ArrayList<>());
            } else {
                booking.getListOfAddOns().clear();
            }
            if (!newAddons.isEmpty()) {
                booking.getListOfAddOns().addAll(newAddons);
            }
        } catch (Exception ignored) {}
        
        RentalBooking saved = bookingRepository.save(booking);
        return saved;
    }

    @Transactional
    @Override
    public RentalBooking updateBookingStatus(String id, String newStatus) {
        RentalBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Booking tidak ditemukan: " + id));

        BookingStatus current = booking.getStatus();
        if (current == null) {
            throw new BadRequestException("Booking memiliki status yang tidak valid.");
        }

        BookingStatus target;
        try {
            target = BookingStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Status target tidak dikenal: " + newStatus);
        }

        // Allowed transitions (no CANCELLED)
        boolean allowed = false;
        switch (current) {
            case UPCOMING:
                if (target == BookingStatus.ONGOING) allowed = true;
                break;
            case ONGOING:
                if (target == BookingStatus.DONE) allowed = true;
                break;
            case DONE:
                allowed = false;
                break;
            default:
                allowed = false;
        }

        if (!allowed) {
            throw new BadRequestException("Transisi status tidak diizinkan: " + current + " -> " + target);
        }

        // Optional business check: cannot set ONGOING before pickUpTime
        if (target == BookingStatus.ONGOING && booking.getPickUpTime() != null) {
            if (booking.getPickUpTime().isAfter(java.time.LocalDateTime.now())) {
                throw new BadRequestException("Tidak bisa set ONGOING sebelum waktu pick-up.");
            }
        }

        booking.setStatus(target);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public RentalBooking updateAddOns(UpdateRentalBookingRequestDTO dto) throws BadRequestException {
        return updateBookingDetails(dto);
    }

    @Transactional
    @Override
    public RentalBooking cancelBooking(String id) throws BadRequestException {
        Optional<RentalBooking> ob = bookingRepository.findById(id);
        if (ob.isEmpty()) {
            throw new BadRequestException("Booking tidak ditemukan: " + id);
        }
        RentalBooking booking = ob.get();

        // hanya boleh cancel jika status UPCOMING
        if (booking.getStatus() == null || booking.getStatus() != RentalBooking.BookingStatus.UPCOMING) {
            throw new BadRequestException("Booking hanya dapat dibatalkan ketika status 'Upcoming'.");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pickUp = booking.getPickUpTime();

        // Jika pembatalan sebelum pickUpTime -> totalPrice = 0.0
        if (pickUp != null && now.isBefore(pickUp)) {
            booking.setTotalPrice(0.0);
        }
        // jika pickUpTime sudah lewat -> totalPrice tetap

        // set status ke DONE, tandai deleted true
        booking.setStatus(RentalBooking.BookingStatus.DONE);
        booking.setDeleted(true);

        // set vehicle kembali menjadi Available (langsung)
        if (booking.getVehicleId() != null && !booking.getVehicleId().isBlank()) {
            try {
                vehicleRepository.findById(booking.getVehicleId()).ifPresent(v -> {
                    v.setStatus("Available"); // sesuaikan jika Vehicle menggunakan enum
                    vehicleRepository.save(v);
                });
            } catch (Exception ignored) {
            }
        }

        RentalBooking saved = bookingRepository.save(booking);
        return saved;
    }

    @Override
    public Map<String, Object> getBookingStats(String period, int year) {
        // normalize period
        String p = (period == null) ? "monthly" : period.trim().toLowerCase();
        if (!p.equals("monthly") && !p.equals("quarterly")) {
            p = "monthly";
        }

        List<String> labels = new ArrayList<>();
        int[] counts;

        if (p.equals("monthly")) {
            labels = Arrays.asList("January","February","March","April","May","June","July","August","September","October","November","December");
            counts = new int[12];
        } else {
            labels = Arrays.asList("Q1","Q2","Q3","Q4");
            counts = new int[4];
        }
        
        List<RentalBooking> all = bookingRepository.findAllActive();

        for (RentalBooking b : all) {
            LocalDateTime created = b.getCreatedAt();
            // fallback: jika createdAt null, gunakan pickUpTime sebagai last resort
            if (created == null) created = b.getPickUpTime();
            if (created == null) continue;

            int y = created.getYear();
            if (y != year) continue;

            int month = created.getMonthValue(); // 1..12
            if (p.equals("monthly")) {
                counts[month - 1] += 1;
            } else {
                int qIndex = (month - 1) / 3; // 0..3
                counts[qIndex] += 1;
            }
        }

        List<Integer> data = new ArrayList<>();
        for (int c : counts) data.add(c);

        Map<String, Object> out = new HashMap<>();
        out.put("labels", labels);
        out.put("data", data);
        out.put("period", p);
        out.put("year", year);
        return out;
    }
}

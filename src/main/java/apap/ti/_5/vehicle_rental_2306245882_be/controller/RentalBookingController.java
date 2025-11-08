package apap.ti._5.vehicle_rental_2306245882_be.controller;

import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.BookingListItem;
import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.SearchResultVehicleDTO;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245882_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.UpdateRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.UpdateStatusRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245882_be.service.BadRequestException;
import apap.ti._5.vehicle_rental_2306245882_be.service.RentalAddOnService;
import apap.ti._5.vehicle_rental_2306245882_be.service.RentalBookingService;
import apap.ti._5.vehicle_rental_2306245882_be.service.RentalVendorService;
import apap.ti._5.vehicle_rental_2306245882_be.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/bookings")
public class RentalBookingController {

    private final RentalBookingService bookingService;
    private final RentalVendorService rentalVendorService;
    private final RentalAddOnService addOnService;
    private final VehicleService vehicleService;
    private final VehicleRepository vehicleRepository; // optional, used for selectedVehicle

    public RentalBookingController(RentalBookingService bookingService,
                                   RentalVendorService rentalVendorService,
                                   RentalAddOnService addOnService,
                                   VehicleService vehicleService,
                                   VehicleRepository vehicleRepository) {
        this.bookingService = bookingService;
        this.rentalVendorService = rentalVendorService;
        this.addOnService = addOnService;
        this.vehicleService = vehicleService;
        this.vehicleRepository = vehicleRepository;
    }

    // ----------------- LIST + DETAIL -----------------
    @GetMapping({"", "/"})
    public String viewAllBookings(Model model) {
        List<BookingListItem> bookings = bookingService.getAllBookingsForList();
        model.addAttribute("bookings", bookings);
        return "bookings/bookings";
    }

    @GetMapping("/{id}")
    public String viewBookingDetail(@PathVariable("id") String id, Model model) {
        RentalBooking booking = bookingService.getById(id);
        model.addAttribute("booking", booking);
        return "bookings/booking-details";
    }

    // ----------------- SHOW CREATE (halaman 1) -----------------
    @GetMapping("/create")
    public String showCreateForm(Model model,
                                 @ModelAttribute("createReq") CreateRentalBookingRequestDTO flashedReq) {
        // populate locations from vendors
        List<RentalVendor> vendors = rentalVendorService.getAll();
        Set<String> locations = vendors.stream()
                .filter(Objects::nonNull)
                .flatMap(v -> v.getListOfLocations() == null ? Stream.<String>empty() : v.getListOfLocations().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        model.addAttribute("locations", locations);

        // add-ons list (for second page)
        model.addAttribute("addOns", addOnService.getAll());

        // placeholder vehicles until user searches
        model.addAttribute("vehicles", Collections.emptyList());

        // use flashed createReq if present; otherwise create default DTO
        if (flashedReq != null) {
            model.addAttribute("createReq", flashedReq);
        } else if (!model.containsAttribute("createReq")) {
            CreateRentalBookingRequestDTO defaultReq = new CreateRentalBookingRequestDTO(
                    "", // vehicleId
                    null, // pickUpTime
                    null, // dropOffTime
                    locations.stream().findFirst().orElse("DKI Jakarta"), // pickUpLocation
                    locations.stream().findFirst().orElse("DKI Jakarta"), // dropOffLocation
                    0, // capacityNeeded
                    "Automatic", // transmissionNeeded
                    false, // includeDriver
                    Collections.emptyList() // addonIds
            );
            model.addAttribute("createReq", defaultReq);
        }

        // model.addAttribute("info");
        return "bookings/create";
    }

    // ----------------- POST create -> SEARCH or FINAL CREATE -----------------
    @PostMapping("/create")
    public String handleCreateOrSearch(@ModelAttribute("createReq") CreateRentalBookingRequestDTO req,
                                       Model model,
                                       RedirectAttributes ra) {
        // repopulate shared view data
        List<RentalVendor> vendors = rentalVendorService.getAll();
        Set<String> locations = vendors.stream()
                .filter(Objects::nonNull)
                .flatMap(v -> v.getListOfLocations() == null ? Stream.<String>empty() : v.getListOfLocations().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        model.addAttribute("locations", locations);
        model.addAttribute("addOns", addOnService.getAll());

        // CASE: SEARCH (vehicleId belum dipilih)
        if (req.getVehicleId() == null || req.getVehicleId().isBlank()) {
            try {
                List<SearchResultVehicleDTO> results = bookingService.findAvailableVehicles(req);
                model.addAttribute("vehicles", results);

                long rentalDays = 0;
                if (req.getPickUpTime() != null && req.getDropOffTime() != null) {
                    long hours = Duration.between(req.getPickUpTime(), req.getDropOffTime()).toHours();
                    rentalDays = (hours > 0) ? ((hours + 23) / 24) : 0;
                }
                model.addAttribute("rentalDays", rentalDays);
                model.addAttribute("createReq", req);
                return "bookings/create";
            } catch (BadRequestException e) {
                ra.addFlashAttribute("error", e.getMessage());
                ra.addFlashAttribute("createReq", req);
                return "redirect:/bookings/create";
            } catch (Exception e) {
                ra.addFlashAttribute("error", "Kesalahan saat mencari kendaraan: " + e.getMessage());
                ra.addFlashAttribute("createReq", req);
                return "redirect:/bookings/create";
            }
        }

        // CASE: FINAL CREATE (vehicleId sudah dipilih)
        try {
            RentalBooking saved = bookingService.createRentalBooking(req);
            ra.addFlashAttribute("success", "Booking berhasil dibuat: " + saved.getId());
            return "redirect:/bookings";
        } catch (BadRequestException e) {
            ra.addFlashAttribute("error", e.getMessage());
            ra.addFlashAttribute("createReq", req);
            return "redirect:/bookings/create";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Kesalahan saat menyimpan booking: " + e.getMessage());
            ra.addFlashAttribute("createReq", req);
            return "redirect:/bookings/create";
        }
    }

    // ----------------- Proceed to Add-Ons (store createReq to flash and redirect) -----------------
    @PostMapping("/create/proceed")
    public String proceedToAddOns(@ModelAttribute("createReq") CreateRentalBookingRequestDTO req,
                                RedirectAttributes ra) {
        String vid = req.getVehicleId();
        if (vid == null || vid.isBlank()) {
            ra.addFlashAttribute("error", "Pilih kendaraan terlebih dahulu sebelum melanjutkan.");
            ra.addFlashAttribute("createReq", req);
            return "redirect:/bookings/create";
        }

        // Sanitasi: jika ada multiple values concatenated (VEH1,VEH1) -> ambil token pertama
        if (vid.contains(",")) {
            String first = vid.split(",")[0].trim();
            // buat new record dengan vehicleId yang sudah disanitasi (CreateRentalBookingRequestDTO adalah record)
            CreateRentalBookingRequestDTO fixed = new CreateRentalBookingRequestDTO(
                    first,
                    req.getPickUpTime(),
                    req.getDropOffTime(),
                    req.getPickUpLocation(),
                    req.getDropOffLocation(),
                    req.getCapacityNeeded(),
                    req.getTransmissionNeeded(),
                    req.getIncludeDriver(),
                    req.getAddonIds()
            );
            ra.addFlashAttribute("createReq", fixed);
        } else {
            ra.addFlashAttribute("createReq", req);
        }

        return "redirect:/bookings/create/addons";
    }

    // ----------------- Add-Ons page (halaman 2) -----------------
    @GetMapping("/create/addons")
    public String showAddOns(Model model, @ModelAttribute("createReq") CreateRentalBookingRequestDTO flashedReq) {
        if (flashedReq == null) {
            return "redirect:/bookings/create";
        }
        model.addAttribute("createReq", flashedReq);
        model.addAttribute("addOns", addOnService.getAll());

        // optional: show selected vehicle summary if available
        if (flashedReq.getVehicleId() != null && !flashedReq.getVehicleId().isBlank()) {
            try {
                Optional<Vehicle> vopt = vehicleRepository.findById(flashedReq.getVehicleId());
                vopt.ifPresent(v -> model.addAttribute("selectedVehicle", v));
            } catch (Exception ignored) { /* not fatal */ }
        }
        return "bookings/addons";
    }

    // ----------------- Save booking from Add-Ons -----------------
    @PostMapping("/create/save")
    public String saveBooking(@ModelAttribute("createReq") CreateRentalBookingRequestDTO req,
                              RedirectAttributes ra) {
        try {
            RentalBooking saved = bookingService.createRentalBooking(req);
            ra.addFlashAttribute("success", "Booking berhasil dibuat: " + saved.getId());
            return "redirect:/bookings";
        } catch (BadRequestException e) {
            ra.addFlashAttribute("error", e.getMessage());
            ra.addFlashAttribute("createReq", req);
            return "redirect:/bookings/create/addons";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Kesalahan saat menyimpan booking: " + e.getMessage());
            ra.addFlashAttribute("createReq", req);
            return "redirect:/bookings/create/addons";
        }
    }

    // ----------------- EDIT Booking Details (GET form) -----------------
    @GetMapping("/{id}/update-details")
    public String showUpdateDetailsForm(@PathVariable("id") String id, Model model, RedirectAttributes ra) {
        try {
            RentalBooking booking = bookingService.getById(id);
            // hanya Upcoming
            RentalBooking.BookingStatus st = booking.getStatus();
            if (st == null || st != RentalBooking.BookingStatus.UPCOMING) {
                ra.addFlashAttribute("error", "Booking hanya dapat diubah ketika status Upcoming.");
                return "redirect:/bookings/" + id;
            }

            UpdateRentalBookingRequestDTO dto = new UpdateRentalBookingRequestDTO();
            dto.setId(booking.getId());
            dto.setVehicleId(booking.getVehicleId());
            dto.setPickUpTime(booking.getPickUpTime());
            dto.setDropOffTime(booking.getDropOffTime());
            dto.setPickUpLocation(booking.getPickUpLocation());
            dto.setDropOffLocation(booking.getDropOffLocation());
            dto.setCapacityNeeded(booking.getCapacityNeeded());
            dto.setTransmission(booking.getTransmissionNeeded());
            dto.setIncludeDriver(booking.getIncludeDriver());
            if (booking.getListOfAddOns() != null) {
                List<String> aid = booking.getListOfAddOns().stream()
                        .filter(Objects::nonNull)
                        .map(a -> a.getId().toString())
                        .collect(Collectors.toList());
                dto.setAddonIds(aid);
            }
            List<RentalVendor> vendors = rentalVendorService.getAll();
            Set<String> locations = vendors.stream()
                    .filter(Objects::nonNull)
                    .flatMap(v -> v.getListOfLocations() == null ? Stream.<String>empty() : v.getListOfLocations().stream())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            model.addAttribute("locations", locations);
            model.addAttribute("addOns", addOnService.getAll());
            model.addAttribute("updateReq", dto);

            // optionally include available vehicles initially empty; the page can implement "Search for Vehicles"
            model.addAttribute("vehicles", Collections.emptyList());

            // include booking for reference (e.g. show current vehicle id)
            model.addAttribute("booking", booking);

            return "bookings/update-details";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Tidak dapat membuka form update: " + e.getMessage());
            return "redirect:/bookings/" + id;
        }
    }

    // ----------------- HANDLE Update Details SUBMIT (PUT or POST) -----------------
    @RequestMapping(value = "/update-details", method = {RequestMethod.PUT, RequestMethod.POST})
    public String handleUpdateDetails(@ModelAttribute("updateReq") UpdateRentalBookingRequestDTO dto,
                                    RedirectAttributes ra) {
        try {
            RentalBooking saved = bookingService.updateBookingDetails(dto);
            ra.addFlashAttribute("success", "Booking berhasil diperbarui: " + saved.getId());
            return "redirect:/bookings/" + saved.getId();
        } catch (BadRequestException e) {
            ra.addFlashAttribute("error", e.getMessage());
            ra.addFlashAttribute("updateReq", dto);
            // redirect back to form for that id
            return "redirect:/bookings/" + dto.getId() + "/update-details";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Kesalahan saat menyimpan perubahan: " + e.getMessage());
            ra.addFlashAttribute("updateReq", dto);
            return "redirect:/bookings/" + dto.getId() + "/update-details";
        }
    }

    // ----------------- SHOW Update Status FORM -----------------
    @GetMapping("/{id}/status")
    public String showUpdateStatusForm(@PathVariable("id") String id, Model model) {
        RentalBooking booking = bookingService.getById(id);
        model.addAttribute("booking", booking);

        List<String> allowed = new ArrayList<>();
        if (booking.getStatus() != null) {
            String cur = booking.getStatus().name();
            if ("UPCOMING".equalsIgnoreCase(cur)) {
                allowed.add("ONGOING"); 
            } else if ("ONGOING".equalsIgnoreCase(cur)) {
                allowed.add("DONE"); 
            }
        }
        model.addAttribute("statuses", allowed);
        return "bookings/update-status";
    }

    @PostMapping("/{id}/status")
    public String handleUpdateStatus(@PathVariable("id") String id,
                                    @RequestParam("status") String status,
                                    RedirectAttributes ra) {
        if (status == null || status.isBlank()) {
            ra.addFlashAttribute("error", "Pilih status baru terlebih dahulu.");
            return "redirect:/bookings/" + id + "/status";
        }
        try {
            RentalBooking updated = bookingService.updateBookingStatus(id, status);
            ra.addFlashAttribute("success", "Status berhasil diubah menjadi: " + (updated.getStatus() != null ? updated.getStatus().name() : status));
        } catch (BadRequestException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/bookings/" + id + "/status";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Kesalahan: " + e.getMessage());
            return "redirect:/bookings/" + id + "/status";
        }
        return "redirect:/bookings/" + id;
    }

    @RequestMapping(value = "/status", method = {RequestMethod.PUT, RequestMethod.POST})
    public String handleUpdateStatus(@ModelAttribute("statusReq") UpdateStatusRequestDTO dto,
                                    RedirectAttributes ra) {
        try {
            RentalBooking updated = bookingService.updateBookingStatus(dto.getId(), dto.getStatus());
            ra.addFlashAttribute("success", "Status booking diperbarui: " + updated.getStatus());
            return "redirect:/bookings/" + dto.getId();
        } catch (BadRequestException e) {
            ra.addFlashAttribute("error", e.getMessage());
            ra.addFlashAttribute("statusReq", dto);
            return "redirect:/bookings/" + dto.getId() + "/status";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Kesalahan saat memperbarui status: " + e.getMessage());
            ra.addFlashAttribute("statusReq", dto);
            return "redirect:/bookings/" + dto.getId() + "/status";
        }
    }

    @GetMapping("/{id}/update-addons")
    public String showUpdateAddOnsForm(@PathVariable("id") String id, Model model, RedirectAttributes ra) {
        RentalBooking booking = bookingService.getById(id);
        if (booking == null) {
            ra.addFlashAttribute("error", "Booking tidak ditemukan.");
            return "redirect:/bookings";
        }

        // hanya Upcoming boleh mengubah
        if (booking.getStatus() != RentalBooking.BookingStatus.UPCOMING) {
            ra.addFlashAttribute("error", "Hanya booking dengan status UPCOMING yang bisa mengubah Add-Ons.");
            return "redirect:/bookings/" + id;
        }

        // buat DTO untuk form
        UpdateRentalBookingRequestDTO dto = new UpdateRentalBookingRequestDTO();
        dto.setId(booking.getId());
        dto.setVehicleId(booking.getVehicleId());
        dto.setPickUpTime(booking.getPickUpTime());
        dto.setDropOffTime(booking.getDropOffTime());
        dto.setPickUpLocation(booking.getPickUpLocation());
        dto.setDropOffLocation(booking.getDropOffLocation());
        dto.setCapacityNeeded(booking.getCapacityNeeded());
        dto.setTransmission(booking.getTransmissionNeeded());
        dto.setIncludeDriver(booking.getIncludeDriver());
        if (booking.getListOfAddOns() != null) {
            List<String> addonIds = booking.getListOfAddOns().stream()
                    .filter(Objects::nonNull)
                    .map(a -> a.getId().toString())
                    .collect(Collectors.toList());
            dto.setAddonIds(addonIds);
        }

        model.addAttribute("booking", booking);
        model.addAttribute("updateReq", dto);
        model.addAttribute("addOns", addOnService.getAll());

        return "bookings/update-addons";
    }

    // ----------------- HANDLE Update Add-Ons SUBMIT -----------------
    @PostMapping("/update-addons")
    public String handleUpdateAddOns(
            @RequestParam("id") String id,
            @RequestParam("vehicleId") String vehicleId,
            @RequestParam(value = "pickUpTime", required = false) String pickUpTimeStr,
            @RequestParam(value = "dropOffTime", required = false) String dropOffTimeStr,
            @RequestParam("pickUpLocation") String pickUpLocation,
            @RequestParam("dropOffLocation") String dropOffLocation,
            @RequestParam(value = "capacityNeeded", required = false) Integer capacityNeeded,
            @RequestParam(value = "transmission", required = false) String transmission,
            @RequestParam(value = "includeDriver", required = false) Boolean includeDriver,
            @RequestParam(value = "addonIds", required = false) List<String> addonIds,
            RedirectAttributes ra) {

        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME; 

        // Parse datetimes manually
        LocalDateTime pickUpTime = null;
        LocalDateTime dropOffTime = null;
        try {
            if (pickUpTimeStr != null && !pickUpTimeStr.isBlank()) {
                pickUpTime = LocalDateTime.parse(pickUpTimeStr, fmt);
            }
            if (dropOffTimeStr != null && !dropOffTimeStr.isBlank()) {
                dropOffTime = LocalDateTime.parse(dropOffTimeStr, fmt);
            }
        } catch (DateTimeParseException ex) {
            ra.addFlashAttribute("error", "Format tanggal/waktu tidak valid: " + ex.getMessage());
            return "redirect:/bookings/" + id + "/update-addons";
        }
        
        UpdateRentalBookingRequestDTO dto = new UpdateRentalBookingRequestDTO();
        dto.setId(id);
        dto.setVehicleId(vehicleId);
        dto.setPickUpTime(pickUpTime);
        dto.setDropOffTime(dropOffTime);
        dto.setPickUpLocation(pickUpLocation);
        dto.setDropOffLocation(dropOffLocation);
        dto.setCapacityNeeded(capacityNeeded);
        dto.setTransmission(transmission);
        dto.setIncludeDriver(includeDriver == null ? false : includeDriver);
        dto.setAddonIds(addonIds == null ? new ArrayList<>() : addonIds);

        try {
            RentalBooking updated = bookingService.updateBookingDetails(dto);
            ra.addFlashAttribute("success", "Add-ons berhasil disimpan.");
            return "redirect:/bookings/" + updated.getId();
        } catch (BadRequestException e) {
            ra.addFlashAttribute("error", e.getMessage());
            // keep form data so user can try again
            ra.addFlashAttribute("updateReq", dto);
            return "redirect:/bookings/" + id + "/update-addons";
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Terjadi kesalahan saat menyimpan: " + ex.getMessage());
            ra.addFlashAttribute("updateReq", dto);
            return "redirect:/bookings/" + id + "/update-addons";
        }
    }

    @GetMapping("/{id}/delete")
    public String cancelBookingView(@PathVariable("id") String id, Model model) {
        try {
            RentalBooking canceled = bookingService.cancelBooking(id);

            // update model untuk feedback
            model.addAttribute("success", "Booking berhasil dibatalkan: " + canceled.getId());
            model.addAttribute("bookingId", canceled.getId());
            model.addAttribute("status", "success");
        } catch (BadRequestException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("status", "error");
        } catch (Exception e) {
            model.addAttribute("error", "Terjadi kesalahan internal: " + e.getMessage());
            model.addAttribute("status", "error");
        }

        // arahkan ke halaman feedback (HTML)
        return "bookings/delete-result";
    }

    @GetMapping("/chart")
    public String viewBookingChart(Model model,
                                @RequestParam(name="period", required=false, defaultValue="monthly") String period,
                                @RequestParam(name="year", required=false) Integer year) {
        int currentYear = LocalDateTime.now().getYear();
        if (year == null) year = currentYear;

        // buat pilihan tahun (mis. dari currentYear-5 .. currentYear)
        List<Integer> years = new ArrayList<>();
        for (int y = currentYear; y >= currentYear - 5; y--) years.add(y);

        model.addAttribute("selectedPeriod", period);
        model.addAttribute("selectedYear", year);
        model.addAttribute("years", years);
        return "bookings/chart";
    }
}

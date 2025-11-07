package apap.ti._5.vehicle_rental_2306245882_be.controller;

import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.BookingListItem;
import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.SearchResultVehicleDTO;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245882_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateRentalBookingRequestDTO;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * RentalBookingController - handle bookings flow:
 *  GET  /bookings             -> list all bookings
 *  GET  /bookings/{id}        -> detail booking
 *  GET  /bookings/create      -> show the search/create form
 *  POST /bookings/create      -> if vehicleId empty => SEARCH (render results)
 *                              -> if vehicleId present => FINAL CREATE (save booking)
 *  POST /bookings/create/proceed -> move to add-ons page (flash createReq)
 *  GET  /bookings/create/addons  -> show add-ons page
 *  POST /bookings/create/save     -> save booking with selected add-ons
 */
@Controller
@RequestMapping("/bookings")
public class RentalBookingController {

    private final RentalBookingService bookingService;
    private final RentalVendorService rentalVendorService;
    private final RentalAddOnService addOnService;
    private final VehicleService vehicleService;

    @Autowired
    private VehicleRepository vehicleRepository; // optional, used for showing selected vehicle details

    @Autowired
    public RentalBookingController(RentalBookingService bookingService,
                                   RentalVendorService rentalVendorService,
                                   RentalAddOnService addOnService,
                                   VehicleService vehicleService) {
        this.bookingService = bookingService;
        this.rentalVendorService = rentalVendorService;
        this.addOnService = addOnService;
        this.vehicleService = vehicleService;
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
        return "bookings/booking-detail";
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

        model.addAttribute("info");
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
        if (req.vehicleId() == null || req.vehicleId().isBlank()) {
            try {
                List<SearchResultVehicleDTO> results = bookingService.findAvailableVehicles(req);
                model.addAttribute("vehicles", results);

                long rentalDays = 0;
                if (req.pickUpTime() != null && req.dropOffTime() != null) {
                    long hours = Duration.between(req.pickUpTime(), req.dropOffTime()).toHours();
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
        if (req.vehicleId() == null || req.vehicleId().isBlank()) {
            ra.addFlashAttribute("error", "Pilih kendaraan terlebih dahulu sebelum melanjutkan.");
            ra.addFlashAttribute("createReq", req);
            return "redirect:/bookings/create";
        }
        ra.addFlashAttribute("createReq", req);
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
        if (flashedReq.vehicleId() != null && !flashedReq.vehicleId().isBlank()) {
            try {
                Optional<Vehicle> vopt = vehicleRepository.findById(flashedReq.vehicleId());
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
}

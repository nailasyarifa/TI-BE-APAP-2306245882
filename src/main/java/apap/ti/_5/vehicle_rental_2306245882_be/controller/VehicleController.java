package apap.ti._5.vehicle_rental_2306245882_be.controller;

import apap.ti._5.vehicle_rental_2306245882_be.dto.vehicle.UpdateVehicleDTO;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306245882_be.service.BadRequestException;
import apap.ti._5.vehicle_rental_2306245882_be.service.RentalVendorService;
import apap.ti._5.vehicle_rental_2306245882_be.service.VehicleService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@Controller
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private RentalVendorService rentalVendorService;

    // ALL VEHICLES (single mapping)
    @GetMapping("/vehicles")
    public String viewAllVehicles(@RequestParam(required = false) String type,
                                  @RequestParam(required = false) String search,
                                  Model model) {
        var page = vehicleService.getAllVehicles(type, search, PageRequest.of(0, 100));
        List<VehicleResponseDTO> vehicles = page.getContent();
        model.addAttribute("vehicles", vehicles);
        return "vehicles/vehicles";
    }

    // DETAIL
    @GetMapping("/vehicles/{id}")
    public String viewVehicleDetail(@PathVariable String id, Model model) {
        VehicleResponseDTO v = vehicleService.getById(id);
        model.addAttribute("vehicle", v);
        return "vehicles/vehicle-detail";
    }

    // CREATE form
    @GetMapping("/vehicles/create")
    public String showCreateForm(Model model) {
        List<RentalVendor> vendors = rentalVendorService.getAll();
        model.addAttribute("vendors", vendors);
        model.addAttribute("vehicle", new CreateVehicleRequestDTO());
        return "vehicles/create";
    }

    // CREATE vehicle
    @PostMapping("/vehicles/create")
    public String createVehicle(@ModelAttribute CreateVehicleRequestDTO req, Model model) {
        int currentYear = Year.now().getValue();
        if (req.getProductionYear() != null && req.getProductionYear() > currentYear) {
            model.addAttribute("error", "Tahun keluaran tidak boleh melebihi tahun saat ini.");
            return "vehicles/create";
        }

        vehicleService.create(req);
        model.addAttribute("success", "Kendaraan berhasil ditambahkan!");
        return "redirect:/vehicles";
    }

    // SHOW EDIT form
    @GetMapping("/vehicles/{id}/update")
    public String showEditForm(@PathVariable String id, Model model) {
        VehicleResponseDTO vehicle = vehicleService.getById(id);
        model.addAttribute("vehicle", vehicle);

        UpdateVehicleDTO updateDto = UpdateVehicleDTO.builder()
                .type(vehicle.getType())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .productionYear(vehicle.getProductionYear())
                .location(vehicle.getLocation())
                .licensePlate(vehicle.getLicensePlate())
                .capacity(vehicle.getCapacity())
                .transmission(vehicle.getTransmission())
                .fuelType(vehicle.getFuelType())
                .price(vehicle.getPrice())
                .status(vehicle.getStatus())
                .rentalVendorId(null) // kalau response tak punya vendorId; kalau ada, isi
                .build();

        model.addAttribute("updateVehicle", updateDto);
        model.addAttribute("vendors", rentalVendorService.getAll());
        return "vehicles/update";
    }

    // HANDLE EDIT submit
    @PostMapping("/vehicles/{id}/update")
    public String updateVehicle(
            @PathVariable String id,
            @Valid @ModelAttribute("updateVehicle") UpdateVehicleDTO dto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vendors", rentalVendorService.getAll());
            return "vehicles/update";
        }

        UpdateVehicleRequestDTO req = new UpdateVehicleRequestDTO(
            dto.getType(),
            dto.getBrand(),
            dto.getModel(),
            dto.getProductionYear(),
            dto.getLocation(),
            dto.getLicensePlate(),
            dto.getCapacity(),
            dto.getTransmission(),
            dto.getFuelType(),
            dto.getPrice(),
            dto.getStatus(),
            dto.getRentalVendorId() // sekarang Integer
        );

        vehicleService.update(id, req);
        return "redirect:/vehicles";
    }

    // DELETE vehicle
    @PostMapping("/vehicles/{id}/delete")
    public String deleteVehicle(@PathVariable String id, Model model) {
        vehicleService.delete(id);
        return "redirect:/vehicles";
    }
}

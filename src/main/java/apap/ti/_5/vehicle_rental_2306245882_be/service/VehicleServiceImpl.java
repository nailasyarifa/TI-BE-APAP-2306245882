package apap.ti._5.vehicle_rental_2306245882_be.service;

import apap.ti._5.vehicle_rental_2306245882_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245882_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.response.VehicleResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final RentalVendorRepository rentalVendorRepository;
    private final RentalBookingRepository rentalBookingRepository;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              RentalVendorRepository rentalVendorRepository, 
                              RentalBookingRepository rentalBookingRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalVendorRepository = rentalVendorRepository;
        this.rentalBookingRepository = rentalBookingRepository;
    }

    @Override
    public Page<VehicleResponseDTO> getAllVehicles(String type, String search, Pageable pageable) {
        // Force sort by id ascending if not specified
        Pageable pageReq = pageable;
        if (pageable == null) {
            pageReq = PageRequest.of(0, 20, Sort.by("id").ascending());
        } else if (pageable.getSort().isUnsorted()) {
            pageReq = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").ascending());
        }

        String q = (search == null || search.isBlank()) ? null : search;
        Page<Vehicle> page = vehicleRepository.searchVehicles(type, q, pageReq);
        return page.map(this::toDto);
    }

    @Override
    public VehicleResponseDTO getById(String id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id " + id));
        return toDto(v);
    }

    @Override
    public VehicleResponseDTO create(CreateVehicleRequestDTO req) {
        // Ambil kendaraan terakhir berdasarkan ID (descending)
        Optional<Vehicle> lastVehicleOpt = vehicleRepository.findTopByOrderByIdDesc();
        String newId;

        if (lastVehicleOpt.isPresent()) {
            String lastId = lastVehicleOpt.get().getId();
            // Pastikan ID berformat VEHxxxx
            if (lastId.startsWith("VEH") && lastId.length() >= 7) {
                try {
                    int lastNum = Integer.parseInt(lastId.substring(3));
                    newId = String.format("VEH%04d", lastNum + 1);
                } catch (NumberFormatException e) {
                    // fallback ke VEH0021 kalau parsing gagal
                    newId = "VEH0021";
                }
            } else {
                newId = "VEH0021";
            }
        } else {
            // Jika belum ada data sama sekali
            newId = "VEH0021";
        }

        Vehicle v = new Vehicle();
        v.setId(newId); // assign custom ID di sini
        v.setType(req.getType());
        v.setBrand(req.getBrand());
        v.setModel(req.getModel());
        v.setProductionYear(req.getProductionYear());
        v.setLocation(req.getLocation());
        v.setLicensePlate(req.getLicensePlate());
        v.setCapacity(req.getCapacity());
        v.setTransmission(req.getTransmission());
        v.setFuelType(req.getFuelType());
        v.setPrice(req.getPrice());
        v.setStatus(req.getStatus() == null ? "Available" : req.getStatus());
        v.setDeleted(false);

        // Handle vendor
        if (req.getRentalVendorId() != null) {
            Long vendorId = req.getRentalVendorId();
            Optional<RentalVendor> maybeVendor = rentalVendorRepository.findById(vendorId);

            if (maybeVendor.isPresent()) {
                v.setRentalVendor(maybeVendor.get());
            } else {
                throw new BadRequestException("rentalVendorId not found: " + vendorId);
            }
        }

        Vehicle saved = vehicleRepository.save(v);
        return toDto(saved);
    }


    @Override
    @Transactional
    public VehicleResponseDTO update(String id, UpdateVehicleRequestDTO req) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id " + id));

        boolean hasActive = rentalBookingRepository.existsActiveBookingForVehicle(id);
        if (hasActive) {
            throw new BadRequestException("Kendaraan sedang disewa. Tidak dapat mengubah data sementara.");
        }

        v.setType(req.getType());
        v.setBrand(req.getBrand());
        v.setModel(req.getModel());
        v.setProductionYear(req.getProductionYear());
        v.setLocation(req.getLocation());
        v.setLicensePlate(req.getLicensePlate());
        v.setCapacity(req.getCapacity());
        v.setTransmission(req.getTransmission());
        v.setFuelType(req.getFuelType());
        v.setPrice(req.getPrice());
        v.setStatus(req.getStatus());

        if (req.getRentalVendorId() != null) {
            Long vendorId = req.getRentalVendorId();
            Optional<RentalVendor> maybeVendor = rentalVendorRepository.findById(vendorId);
            if (maybeVendor.isPresent()) {
                v.setRentalVendor(maybeVendor.get());
            } else {
                throw new BadRequestException("rentalVendorId not found: " + vendorId);
            }
        }

        Vehicle updated = vehicleRepository.save(v);
        return toDto(updated);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id " + id));
        v.setDeleted(true);
        vehicleRepository.save(v);
    }

    private VehicleResponseDTO toDto(Vehicle v) {
        String vendorName = null;
        if (v.getRentalVendor() != null) {
            vendorName = v.getRentalVendor().getName();
        }
        return new VehicleResponseDTO(
            v.getId(),
            v.getType(),
            v.getBrand(),
            v.getModel(),
            v.getCapacity(),
            v.getStatus(),
            v.getPrice(),
            v.getLicensePlate(),
            v.getLocation(),
            v.getProductionYear(),
            v.getTransmission(),
            v.getFuelType(),
            vendorName
        );
    }
}

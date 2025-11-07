package apap.ti._5.vehicle_rental_2306245882_be.service;

import apap.ti._5.vehicle_rental_2306245882_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245882_be.repository.RentalAddOnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RentalAddOnServiceImpl implements RentalAddOnService {

    private final RentalAddOnRepository addonRepository;

    @Autowired
    public RentalAddOnServiceImpl(RentalAddOnRepository addonRepository) {
        this.addonRepository = addonRepository;
    }

    @Override
    public List<RentalAddOn> getAll() {
        return addonRepository.findAll();
    }

    @Override
    public RentalAddOn getById(UUID id) {
        return addonRepository.findById(id).orElse(null); 
    }

    @Override
    public RentalAddOn save(RentalAddOn addon) {
        return addonRepository.save(addon);
    }

    @Override
    public void deleteById(UUID id) {
        addonRepository.deleteById(id);
    }
}

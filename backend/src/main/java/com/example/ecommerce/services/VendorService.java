package com.example.ecommerce.services;

import com.example.ecommerce.models.Vendor;
import com.example.ecommerce.repositories.VendorRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VendorService {
    private static final Logger log =
            LoggerFactory.getLogger(VendorService.class);
    private final VendorRepository vendorRepository;

    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    public List<Vendor> findAll() {
        List<Vendor> vendors = vendorRepository.findAll();
        log.debug("Loaded {} vendors", vendors.size());
        return vendors;
    }

    public Vendor findById(Long id) {
        log.debug("Looking up vendor id={}", id);
        Optional<Vendor> result = vendorRepository.findById(id);
        if (result.isEmpty()) {
            log.debug("Vendor id={} was not found", id);
            return null;
        }
        return result.get();
    }

    public Vendor create(Vendor input) {
        validate(input);
        Vendor vendor = new Vendor(input.getName(), input.getEmail());
        Vendor saved = vendorRepository.save(vendor);
        log.info("Created vendor id={}", saved.getId());
        return saved;
    }

    public Vendor update(Long id, Vendor input) {
        validate(input);
        Vendor existing = findById(id);
        if (existing == null) {
            return null;
        }
        existing.setName(input.getName());
        existing.setEmail(input.getEmail());
        Vendor saved = vendorRepository.save(existing);
        log.info("Updated vendor id={}", saved.getId());
        return saved;
    }

    public boolean delete(Long id) {
        if (!vendorRepository.existsById(id)) {
            log.debug("Delete skipped: vendor id={} was not found", id);
            return false;
        }
        try {
            vendorRepository.deleteById(id);
            log.info("Deleted vendor id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            log.warn("Delete blocked for vendor id={}: database constraint", id);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Remove this vendor's products before deleting it.");
        }
    }

    private void validate(Vendor input) {
        if (input.getName() == null || input.getName().isBlank()
                || input.getName().length() > 100
                || input.getEmail() == null || input.getEmail().isBlank()
                || input.getEmail().length() > 150) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide a name (up to 100 characters) and email (up to 150).");
        }
    }
}

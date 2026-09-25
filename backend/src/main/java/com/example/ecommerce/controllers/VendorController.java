package com.example.ecommerce.controllers;

import com.example.ecommerce.models.Vendor;
import com.example.ecommerce.services.VendorService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {
    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    public List<Vendor> findAll() {
        return vendorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vendor> findById(@PathVariable("id") Long id) {
        Vendor vendor = vendorService.findById(id);
        if (vendor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vendor);
    }

    @PostMapping
    public ResponseEntity<Vendor> create(@RequestBody Vendor input) {
        Vendor saved = vendorService.create(input);
        return ResponseEntity.created(URI.create("/api/vendors/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vendor> update(
            @PathVariable("id") Long id, @RequestBody Vendor input) {
        Vendor updated = vendorService.update(id, input);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        if (!vendorService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}

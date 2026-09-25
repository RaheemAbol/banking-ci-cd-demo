package com.example.ecommerce.repositories;

import com.example.ecommerce.models.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
}

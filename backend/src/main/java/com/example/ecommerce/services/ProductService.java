package com.example.ecommerce.services;

import com.example.ecommerce.models.Product;
import com.example.ecommerce.models.Vendor;
import com.example.ecommerce.repositories.ProductRepository;
import com.example.ecommerce.repositories.VendorRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {
    private static final Logger log =
            LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;

    public ProductService(ProductRepository productRepository,
            VendorRepository vendorRepository) {
        this.productRepository = productRepository;
        this.vendorRepository = vendorRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        Optional<Product> result = productRepository.findById(id);
        if (result.isEmpty()) {
            return null;
        }
        return result.get();
    }

    public List<Product> findByVendorId(Long vendorId) {
        List<Product> products = productRepository.findByVendorId(vendorId);
        log.debug("Loaded {} products for vendor id={}", products.size(), vendorId);
        return products;
    }

    public Product create(Product input) {
        validate(input);
        Optional<Vendor> result = vendorRepository.findById(input.getVendor().getId());
        if (result.isEmpty()) {
            log.debug("Product creation skipped: vendor id={} was not found",
                    input.getVendor().getId());
            return null;
        }
        Product product = new Product(input.getName(), input.getCategory(),
                input.getPrice(), result.get());
        Product saved = productRepository.save(product);
        log.info("Created product id={} for vendor id={}",
                saved.getId(), saved.getVendor().getId());
        return saved;
    }

    public Product update(Long id, Product input) {
        validate(input);
        Product existing = findById(id);
        Optional<Vendor> result = vendorRepository.findById(input.getVendor().getId());
        if (existing == null || result.isEmpty()) {
            return null;
        }
        existing.setName(input.getName());
        existing.setCategory(input.getCategory());
        existing.setPrice(input.getPrice());
        existing.setVendor(result.get());
        return productRepository.save(existing);
    }

    public boolean delete(Long id) {
        if (!productRepository.existsById(id)) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }

    private void validate(Product input) {
        if (input.getName() == null || input.getName().isBlank()
                || input.getName().length() > 100
                || input.getCategory() == null || input.getCategory().isBlank()
                || input.getCategory().length() > 40 || input.getPrice() == null
                || input.getVendor() == null || input.getVendor().getId() == null
                || input.getVendor().getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide a name, category, price and positive vendor ID.");
        }
        if (input.getPrice().signum() < 0 || input.getPrice().scale() > 2
                || input.getPrice().compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Price must be 0 to 99999999.99, with at most two decimal places.");
        }
    }
}

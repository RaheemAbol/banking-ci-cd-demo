package com.example.ecommerce.shopping;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserEmailOrderByIdAsc(String email);
    Optional<CartItem> findByIdAndUserEmail(Long id, String email);
    Optional<CartItem> findByUserEmailAndProductId(String email, Long productId);
}

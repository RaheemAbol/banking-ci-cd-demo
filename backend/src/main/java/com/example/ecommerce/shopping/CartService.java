package com.example.ecommerce.shopping;

import com.example.ecommerce.models.AppUser;
import com.example.ecommerce.models.Product;
import com.example.ecommerce.repositories.AppUserRepository;
import com.example.ecommerce.repositories.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CartService {
    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    private final CartItemRepository items;
    private final AppUserRepository users;
    private final ProductRepository products;

    public CartService(CartItemRepository items, AppUserRepository users,
            ProductRepository products) {
        this.items = items;
        this.users = users;
        this.products = products;
    }

    public List<CartItem> findAll(String email) {
        return items.findByUserEmailOrderByIdAsc(email);
    }

    @Transactional
    public CartItem add(String email, Long productId, int quantity) {
        checkQuantity(quantity);
        AppUser user = users.findByEmail(email).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Product product = products.findById(productId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
        CartItem item = items.findByUserEmailAndProductId(email, productId)
                .orElse(new CartItem(user, product, 0));
        checkQuantity(item.getQuantity() + quantity);
        item.setQuantity(item.getQuantity() + quantity);
        return items.save(item);
    }

    @Transactional
    public CartItem update(String email, Long id, int quantity) {
        checkQuantity(quantity);
        CartItem item = ownedItem(email, id);
        item.setQuantity(quantity);
        return items.save(item);
    }

    public void remove(String email, Long id) {
        items.delete(ownedItem(email, id));
    }

    @Transactional
    public Map<String, Object> checkout(String email) {
        List<CartItem> cart = findAll(email);
        if (cart.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The cart is empty.");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart) {
            total = total.add(item.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        items.deleteAll(cart);
        log.info("Simulated checkout processed: {} cart lines", cart.size());
        return Map.of("message", "Demo checkout only. No payment was taken.",
                "total", total, "items", cart);
    }

    private CartItem ownedItem(String email, Long id) {
        return items.findByIdAndUserEmail(id, email).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private void checkQuantity(int quantity) {
        if (quantity < 1 || quantity > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Quantity must be between 1 and 20.");
        }
    }
}

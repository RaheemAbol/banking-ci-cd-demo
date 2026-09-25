package com.example.ecommerce.shopping;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService carts;

    public CartController(CartService carts) { this.carts = carts; }

    @GetMapping
    public List<CartItem> findAll(Authentication auth) {
        return carts.findAll(auth.getName());
    }

    @PostMapping("/items")
    public CartItem add(Authentication auth,
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") int quantity) {
        return carts.add(auth.getName(), productId, quantity);
    }

    @PutMapping("/items/{id}")
    public CartItem update(Authentication auth, @PathVariable("id") Long id,
            @RequestParam("quantity") int quantity) {
        return carts.update(auth.getName(), id, quantity);
    }

    @DeleteMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(Authentication auth, @PathVariable("id") Long id) {
        carts.remove(auth.getName(), id);
    }

    @PostMapping("/checkout")
    public Map<String, Object> checkout(Authentication auth) {
        return carts.checkout(auth.getName());
    }
}

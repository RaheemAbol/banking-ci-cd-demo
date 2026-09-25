package com.example.ecommerce.shopping;

import com.example.ecommerce.models.AppUser;
import com.example.ecommerce.models.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

// Supplied shopping extension. The main lesson builds the security classes.
@Entity
@Table(name = "cart_items", uniqueConstraints =
        @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(nullable = false)
    private int quantity;

    public CartItem() { }
    public CartItem(AppUser user, Product product, int quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

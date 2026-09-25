package com.example.ecommerce.shopping;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// Supplied extension: also covers product deletion while a cart references it.
@RestControllerAdvice
public class ShoppingConflictHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String conflict() {
        return "This change conflicts with related records. Refresh and try again.";
    }
}

package com.example.ecommerce.services;

import com.example.ecommerce.models.Vendor;
import com.example.ecommerce.repositories.VendorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Ready-made unit tests: run without Spring or MySQL.
class VendorServiceTest {
    private final VendorRepository repository = mock(VendorRepository.class);
    private final VendorService service = new VendorService(repository);

    @Test
    void createsValidVendor() {
        when(repository.save(any(Vendor.class))).thenAnswer(invocation -> {
            Vendor saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        Vendor saved = service.create(new Vendor("Northline", "office@example.test"));

        assertEquals(42L, saved.getId());
        assertEquals("Northline", saved.getName());
        assertEquals("office@example.test", saved.getEmail());
        verify(repository).save(any(Vendor.class));
    }

    @Test
    void rejectsBlankVendorNameBeforeSaving() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.create(new Vendor(" ", "office@example.test")));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verifyNoInteractions(repository);
    }
}

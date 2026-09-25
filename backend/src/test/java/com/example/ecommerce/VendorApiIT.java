package com.example.ecommerce;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Runs only with mvn -Pmysql verify. Uses real MySQL, not H2 or a mock repository.
// MockMvc exercises Spring's request handling without opening a network port.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VendorApiIT {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired ObjectMapper mapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateAndReadVendorFromMySql() throws Exception {
        String response = mvc.perform(post("/api/vendors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"CI Vendor","email":"ci@example.test"}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode created = mapper.readTree(response);
        long id = created.get("id").asLong();
        assertEquals("CI Vendor", jdbc.queryForObject(
                "SELECT name FROM vendors WHERE id = ?", String.class, id));

        mvc.perform(get("/api/vendors/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("CI Vendor"))
                .andExpect(jsonPath("$.email").value("ci@example.test"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void regularUserCannotCreateVendor() throws Exception {
        Integer before = jdbc.queryForObject("SELECT COUNT(*) FROM vendors", Integer.class);
        mvc.perform(post("/api/vendors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Blocked Vendor","email":"blocked@example.test"}
                                """))
                .andExpect(status().isForbidden());
        assertEquals(before, jdbc.queryForObject("SELECT COUNT(*) FROM vendors", Integer.class));
    }
}

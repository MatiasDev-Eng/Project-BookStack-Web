package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.request.AdminLoginRequest;
import com.example.bookstack_backend.services.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

//    @Test
//    void testAdminLogin_Success() throws Exception {
//        AdminLoginRequest request = new AdminLoginRequest();
//        request.setUsername("admin");
//        request.setPassword("password");
//
//        when(adminService.login("admin", "password")).thenReturn(true);
//
//        mockMvc.perform(post("/api/admin/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Admin login successful"));
//    }
//
//    @Test
//    void testAdminLogin_Failure() throws Exception {
//        AdminLoginRequest request = new AdminLoginRequest();
//        request.setUsername("admin");
//        request.setPassword("wrongpassword");
//
//        when(adminService.login("admin", "wrongpassword")).thenReturn(false);
//
//        mockMvc.perform(post("/api/admin/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Invalid admin credentials"));
//    }


}

//package com.hr.auth.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hr.auth.dto.AuthResponse;
//import com.hr.auth.dto.LoginRequest;
//import com.hr.auth.dto.RegisterRequest;
//import com.hr.auth.service.AuthService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
////@WebMvcTest(AuthController.class)
//@WebMvcTest(
//        controllers = AuthController.class,
//        properties = {
//                "jwt.secret=VGhpc0lzQVRlc3RTZWNyZXRLZXlGb3JIUzI1MkFsZ29yaXRobQ==",
//                "jwt.expiration-ms=3600000"
//        }
//)
//class AuthControllerTest {
//
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private AuthService authService;
//
//    @Test
//    void registerShouldReturnCreated() throws Exception {
//        RegisterRequest request = new RegisterRequest();
//        request.setName("Zok");
//        request.setEmail("zok@example.com");
//        request.setPassword("Password@123");
//
//        when(authService.register(any(RegisterRequest.class)))
//                .thenReturn(new AuthResponse(
//                        1L, "Zok", "zok@example.com", "EMPLOYEE"));
//
//        mockMvc.perform(post("/api/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.email").value("zok@example.com"))
//                .andExpect(jsonPath("$.role").value("EMPLOYEE"))
//                .andExpect(jsonPath("$.password").doesNotExist());
//    }
//
//    @Test
//    void loginShouldReturnToken() throws Exception {
//        LoginRequest request = new LoginRequest();
//        request.setEmail("zok@example.com");
//        request.setPassword("Password@123");
//
//        when(authService.login(any(LoginRequest.class)))
//                .thenReturn(new AuthResponse(
//                        1L, "Zok", "zok@example.com",
//                        "EMPLOYEE", "test-jwt-token"));
//
//        mockMvc.perform(post("/api/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("test-jwt-token"))
//                .andExpect(jsonPath("$.email").value("zok@example.com"));
//    }
//
//    @Test
//    void registerShouldRejectInvalidRequest() throws Exception {
//        RegisterRequest request = new RegisterRequest();
//        request.setName("");
//        request.setEmail("invalid-email");
//        request.setPassword("123");
//
//        mockMvc.perform(post("/api/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest());
//    }
//}
package com.hr.auth.service;

import com.hr.auth.dto.AuthResponse;
import com.hr.auth.dto.LoginRequest;
import com.hr.auth.dto.RegisterRequest;
import com.hr.auth.model.AppUser;
import com.hr.auth.Repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private AppUser user;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(1L);
        user.setName("Zok");
        user.setEmail("zok@example.com");
        user.setPassword("encodedPassword");
        user.setRole("EMPLOYEE");
    }

    @Test
    void registerShouldCreateUser() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Zok");
        request.setEmail("ZOK@example.com");
        request.setPassword("Password@123");

        when(appUserRepository.existsByEmail("zok@example.com"))
                .thenReturn(false);
        when(passwordEncoder.encode("Password@123"))
                .thenReturn("encodedPassword");
        when(appUserRepository.save(any(AppUser.class)))
                .thenReturn(user);

        AuthResponse response = authService.register(request);

        assertEquals("zok@example.com", response.getEmail());
        assertEquals("EMPLOYEE", response.getRole());
        verify(appUserRepository).save(any(AppUser.class));
    }

    @Test
    void registerShouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Zok");
        request.setEmail("zok@example.com");
        request.setPassword("Password@123");

        when(appUserRepository.existsByEmail("zok@example.com"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void loginShouldReturnTokenForValidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ZOK@example.com");
        request.setPassword("Password@123");

        when(appUserRepository.findByEmail("zok@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password@123", "encodedPassword"))
                .thenReturn(true);
        when(jwtService.generateToken("zok@example.com", "EMPLOYEE"))
                .thenReturn("test-jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("zok@example.com", response.getEmail());
        assertEquals("test-jwt-token", response.getToken());
    }

    @Test
    void loginShouldRejectWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("zok@example.com");
        request.setPassword("WrongPassword");

        when(appUserRepository.findByEmail("zok@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "encodedPassword"))
                .thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).generateToken(anyString(), anyString());
    }
}
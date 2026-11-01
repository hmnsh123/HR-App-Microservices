package com.hr.auth.service;

import com.hr.auth.Repository.AppUserRepository;
import com.hr.auth.dto.AuthResponse;
import com.hr.auth.dto.LoginRequest;
import com.hr.auth.dto.RegisterRequest;
import com.hr.auth.model.AppUser;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private AuthService(AppUserRepository appUserRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService){
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    public AuthResponse register(RegisterRequest request){
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (appUserRepository.existsByEmail(email)){
            throw new IllegalArgumentException("Email is already exist");
        }

        AppUser user = new AppUser();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("EMPLOYEE");

        AppUser savedUser = appUserRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );



    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }


}

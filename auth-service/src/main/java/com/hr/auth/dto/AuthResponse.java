package com.hr.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class AuthResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String token;


    public AuthResponse(Long id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}

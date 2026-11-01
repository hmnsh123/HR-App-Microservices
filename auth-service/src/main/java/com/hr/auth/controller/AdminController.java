package com.hr.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public Map<String, String> dashboard() {
        return Map.of(
                "message", "Welcome to Admin Dashboard",
                "role", "ADMIN"
        );
    }
}
package com.hr.auth.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jdk.jfr.DataAmount;
import lombok.Cleanup;
import lombok.Data;
import lombok.NonNull;

@Data
@Entity(name = "app_users")
public class AppUser {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "EMPLOYEE";

}

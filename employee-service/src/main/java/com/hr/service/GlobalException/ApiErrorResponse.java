package com.hr.service.GlobalException;

import lombok.Data;

import java.time.Instant;

@Data
public class ApiErrorResponse {

    private int status;
    private String message;
    private Instant timestamp;

    public ApiErrorResponse(int status, String message){
        this.message =message;
        this.status=status;
        this.timestamp = Instant.now();
    }
}

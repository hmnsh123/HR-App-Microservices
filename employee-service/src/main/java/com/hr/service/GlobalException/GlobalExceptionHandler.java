package com.hr.service.GlobalException;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@RestControllerAdvice
public class GlobalExceptionHandler extends Exception{

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(){
        return ResponseEntity.badRequest()
                .body("Something went wrong");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationError(MethodArgumentNotValidException ex){

        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(
                        error -> error.getField() + ": "
                        +error.getDefaultMessage()
                )
                .findFirst()
                .orElse("Validation Failed");

        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),message );

        return ResponseEntity.badRequest().body(response);
    }


    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateEmailError(DuplicateEmailException ex){

        String message = ex.getMessage();
       ApiErrorResponse response = new ApiErrorResponse(HttpStatus.CONFLICT.value(), message);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);


    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex
    ){
        String message = ex.getMessage();
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex){

        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }
}

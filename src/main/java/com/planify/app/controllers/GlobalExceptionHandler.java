package com.planify.app.controllers;

import com.planify.app.dtos.DtoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException validException){
        List<String> errors = validException.getBindingResult().getFieldErrors().stream()
                .map(error-> error.getDefaultMessage()).collect(Collectors.toList());

        return ResponseEntity.badRequest().body(DtoResponse.builder()
                .success(false)
                .response(null)
                .message(errors.toString()).build());
    }
}

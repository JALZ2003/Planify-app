package com.planify.app.controllers;

import com.planify.app.dtos.DtoScheduledSaving;
import com.planify.app.dtos.DtoTransaction;
import com.planify.app.servicies.ScheduledSavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/V1/notbook")
public class NotbookController {

    @Autowired
    private ScheduledSavingService service;

    @PostMapping("/created")
    public ResponseEntity<?> createNotbook(
            @RequestHeader("Authorization") String token,
            @RequestBody DtoScheduledSaving dtoScheduledSaving) {

        return service.createNotebook(token, dtoScheduledSaving);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNotbook(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody DtoScheduledSaving dto) {
        return service.updateNotbook(token, id, dto);
    }

    @GetMapping()
    public ResponseEntity<?> getAllNotbook(@RequestHeader("Authorization") String token) {
        return service.getAllNotbooks(token);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNotbookById(@RequestHeader("Authorization") String token,
                                            @PathVariable Long id) {
        return service.getNotbookById(token, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotbook(@RequestHeader("Authorization") String token,
                                           @PathVariable Long id) {
        return service.deleteNotbook(token, id);
    }
}

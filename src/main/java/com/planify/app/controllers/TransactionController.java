package com.planify.app.controllers;

import com.planify.app.dtos.DtoCategory;
import com.planify.app.dtos.DtoTransaction;
import com.planify.app.repositories.TransactionRepository;
import com.planify.app.servicies.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/V1/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/created")
    public ResponseEntity<?> createTransaction(
            @RequestHeader("Authorization") String token,
            @RequestBody DtoTransaction dtoTransaction) {

        return transactionService.createTransaction(token,dtoTransaction);
    }

}

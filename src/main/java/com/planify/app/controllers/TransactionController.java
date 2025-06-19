package com.planify.app.controllers;

import com.planify.app.dtos.DtoCategory;
import com.planify.app.dtos.DtoTransaction;
import com.planify.app.repositories.TransactionRepository;
import com.planify.app.servicies.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTransaction(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        return transactionService.deleteTransaction(token, id);
    }

    @GetMapping()
    public ResponseEntity<?> getAllTransactions(@RequestHeader("Authorization") String token, @RequestParam(required = false) LocalDate fecha) {
        return transactionService.getAllTransactionsForUser(token, fecha);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@RequestHeader("Authorization") String token,
                                                @PathVariable Long id) {
        return transactionService.getTransactionById(token, id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody DtoTransaction dtoTransaction) {

        return transactionService.updateTransaction( token, id,dtoTransaction);
    }

    @GetMapping("movement")
    public ResponseEntity<?> movements(@RequestHeader("Authorization") String token, @RequestParam(required = false) LocalDate date) {
        return transactionService.getUserFinancialSummary(token, date);
    }

}

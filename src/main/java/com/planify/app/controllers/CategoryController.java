package com.planify.app.controllers;

import com.planify.app.dtos.DtoCategory;
import com.planify.app.dtos.DtoCategoryBody;
import com.planify.app.models.Category;
import com.planify.app.models.FlowType;
import com.planify.app.servicies.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/V1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;




    @GetMapping()
    public ResponseEntity<?> getCombinedCategories(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam(required = false) Boolean isFixed) {

        return categoryService.getCombinedCategories(token, isFixed);
    }

    @GetMapping("/incomes")
    public ResponseEntity<?> getIncomeCategories(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return categoryService.getCategoriesByFlowType(token, 1L); // 1 = Ingreso
    }

    @GetMapping("/expenses")
    public ResponseEntity<?> getExpenseCategories(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return categoryService.getCategoriesByFlowType(token, 2L); // 2 = Gasto
    }

    @PostMapping("/created")
    public ResponseEntity<?> createUserCategory(
            @RequestHeader("Authorization") String token,
            @RequestBody DtoCategoryBody dtoCategory) {

        return categoryService.createUserCategory(token, dtoCategory);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody DtoCategory dtoCategory) {

        return categoryService.updateUserCategory(token, id, dtoCategory);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        return categoryService.deleteUserCategory(token, id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id){
        return categoryService.getCategoryById(token,id);
    }

}


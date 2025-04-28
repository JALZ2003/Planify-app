package com.planify.app.controllers;

import com.planify.app.dtos.DtoCategory;
import com.planify.app.servicies.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/created")
    public ResponseEntity<?> createCategory(@RequestHeader("Authorization") String token, @RequestBody DtoCategory dtoCategory) {
        return categoryService.createdCategory(token, dtoCategory);
    }
}

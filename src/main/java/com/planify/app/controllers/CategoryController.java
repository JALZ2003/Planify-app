package com.planify.app.controllers;

import com.planify.app.dtos.DtoCategory;
import com.planify.app.servicies.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/V1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

   /*
       @PostMapping("/created")
public ResponseEntity<?> createCategory(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody DtoCategory dtoCategory) {
    return categoryService.createdCategory(dtoCategory);
}

@GetMapping("/categorias")
public ResponseEntity<?> getAllCategories(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                              String token){
    return categoryService.getAllCategoriesByUser();
}

@GetMapping("/categoria/{id}")
public ResponseEntity<?> getCategoryById( @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                          @PathVariable Long id) {
    return categoryService.getCategoryById(id);
}

@GetMapping("/tipo-de-flujo/{flowTypeId}")
public ResponseEntity<?> getCategoriesByFlowType(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
        @PathVariable Long flowTypeId) {
    return categoryService.getCategoriesByFlowType(flowTypeId);
}

    */
}

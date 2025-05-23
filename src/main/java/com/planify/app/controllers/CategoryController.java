package com.planify.app.controllers;

import com.planify.app.dtos.DtoCategory;
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

    @GetMapping("/flowtypes")
    public ResponseEntity<List<FlowType>>getAllFlowtypes(){
       List<FlowType> flowTypes = categoryService.getAllFlowTypes();
        return ResponseEntity.ok(flowTypes);

    }

    @GetMapping("/flowtypes/{id}")
    public ResponseEntity<?> getFlowtypeById(@PathVariable Long id){
        return categoryService.getFlowTypeById(id);
    }

   /* @GetMapping()
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }*/
    /*@GetMapping("/user")
    public ResponseEntity<?> getAllCategoriesUser(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String name
    ) {
        DtoCategory filter = DtoCategory.builder().name(name).build();
        return categoryService.getAllCategoriesUser(token, filter);
    }*/


    @GetMapping("/combined")
    public ResponseEntity<?> getCombinedCategories(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam(required = false) Boolean isFixed) {

        return categoryService.getCombinedCategories(token, isFixed);
    }

    @PostMapping("/created")
    public ResponseEntity<?> createUserCategory(
            @RequestHeader("Authorization") String token,
            @RequestBody DtoCategory dtoCategory) {

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


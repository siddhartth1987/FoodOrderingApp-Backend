package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/* This Controller handles endpoints pertaining to Categories.
    getAllCategories
    getCategoryById
 */

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    // This method gets All Categories
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,path = "",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories(){

        // get list of all CategoryEntity.
        List<CategoryEntity> categoryEntities = categoryService.getAllCategoriesOrderedByName();

        if(!categoryEntities.isEmpty()) {
            // create List CategoryListResponses
            List<CategoryListResponse> categoryListResponses = new LinkedList<>();
            categoryEntities.forEach(categoryEntity -> {
                CategoryListResponse categoryListResponse = new CategoryListResponse()
                        .id(UUID.fromString(categoryEntity.getUuid()))
                        .categoryName(categoryEntity.getCategoryName());
                categoryListResponses.add(categoryListResponse);
            });

            //Generating response
            CategoriesListResponse categoriesListResponse = new CategoriesListResponse().categories(categoryListResponses);
            return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
        }else{
            return new ResponseEntity<CategoriesListResponse>(new CategoriesListResponse(),HttpStatus.OK);
        }
    }

    // This method gets Category by UUID
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,path = "/{category_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryById(@PathVariable(value = "category_id")final String categoryUuid) throws CategoryNotFoundException {

        // get CategoryEntity.
        CategoryEntity categoryEntity = categoryService.getCategoryById(categoryUuid);

        // create list of itemEntities
        List<ItemEntity> itemEntities = categoryEntity.getItems();

        // Create ItemLists
        List<ItemList> itemLists = new LinkedList<>();
        itemEntities.forEach(itemEntity -> {
            ItemList itemList = new ItemList()
                    .id(UUID.fromString(itemEntity.getUuid()))
                    .price(itemEntity.getPrice())
                    .itemName(itemEntity.getitemName())
                    .itemType(ItemList.ItemTypeEnum.fromValue(itemEntity.getType().getValue()));
            itemLists.add(itemList);
        });

        // generate response
        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse()
                .categoryName(categoryEntity.getCategoryName())
                .id(UUID.fromString(categoryEntity.getUuid()))
                .itemList(itemLists);
        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse,HttpStatus.OK);
    }

}

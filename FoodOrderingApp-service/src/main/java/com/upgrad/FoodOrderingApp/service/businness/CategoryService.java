package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantCategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

// This Class handles all services for Category.

@Service
public class CategoryService {


    @Autowired
    RestaurantCategoryDao restaurantCategoryDao;


    @Autowired
    RestaurantDao restaurantDao;

    @Autowired
    CategoryDao categoryDao;

    //  This method gets Categories By Restaurant

    public List<CategoryEntity> getCategoriesByRestaurant(String restaurantUuid){

        // get restaurant and restaurant category
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantUuid);
        List<RestaurantCategoryEntity> restaurantCategoryEntities =
                restaurantCategoryDao.getCategoriesByRestaurant(restaurantEntity);

        // list of the Category entity
        List<CategoryEntity> categoryEntities = new LinkedList<>();
        restaurantCategoryEntities.forEach(restaurantCategoryEntity -> {
            categoryEntities.add(restaurantCategoryEntity.getCategory());
        });

        return categoryEntities;
    }

    // This method is gets All Categories Ordered By Name

    public List<CategoryEntity> getAllCategoriesOrderedByName() {

        // get list of CategoryEntity
        List<CategoryEntity> categoryEntities = categoryDao.getAllCategoriesOrderedByName();
        return categoryEntities;

    }

    // This method gets Category By UUID

    public CategoryEntity getCategoryById(String categoryUuid) throws CategoryNotFoundException {

        if(categoryUuid == null || categoryUuid == "")
        {
            throw new CategoryNotFoundException("CNF-001","Category id field should not be empty");
        }

        // get CategoryEntity

        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryUuid);

        if(categoryEntity == null)
        {
            throw new CategoryNotFoundException("CNF-002","No category by this id");
        }

        return categoryEntity;
    }

}

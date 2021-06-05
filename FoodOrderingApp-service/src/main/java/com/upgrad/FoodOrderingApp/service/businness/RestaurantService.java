package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.UtilityProvider;
import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantCategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

// This Class handles all services for Restaurant.

@Service
public class RestaurantService {

    @Autowired
    RestaurantDao restaurantDao;

    @Autowired
    RestaurantCategoryDao restaurantCategoryDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    UtilityProvider utilityProvider;


    // This method gets restaurants By Rating

    public List<RestaurantEntity> restaurantsByRating(){

        //get restaurantsByRating
        List<RestaurantEntity> restaurantEntities = restaurantDao.restaurantsByRating();
        return restaurantEntities;
    }

    // This method gets restaurants By Name

    public List<RestaurantEntity> restaurantsByName(String restaurantName)throws RestaurantNotFoundException {

        if(restaurantName == null || restaurantName =="")
        {
            throw new RestaurantNotFoundException("RNF-003","Restaurant name field should not be empty");
        }

        // get list of RestaurantEntity by name
        List<RestaurantEntity> restaurantEntities = restaurantDao.restaurantsByName(restaurantName);
        return restaurantEntities;

    }


    // This method is to get restaurant By Category

    public List<RestaurantEntity> restaurantByCategory(String categoryId) throws CategoryNotFoundException {

        if(categoryId == null || categoryId == "")
        {
            throw new CategoryNotFoundException("CNF-001","Category id field should not be empty");
        }

        //  fetch list of CategoryEntity
        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryId);

        if(categoryEntity == null)
        {
            throw new CategoryNotFoundException("CNF-002","No category by this id");
        }

        // get list of RestaurantCategoryEntity
        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantCategoryDao.getRestaurantByCategory(categoryEntity);

        // Create new List and add only the restaurant based on category.
        List<RestaurantEntity> restaurantEntities = new LinkedList<>();
        restaurantCategoryEntities.forEach(restaurantCategoryEntity -> {
            restaurantEntities.add(restaurantCategoryEntity.getRestaurant());
        });
        return restaurantEntities;
    }


    // This method fetches  restaurant By UUID

    public RestaurantEntity restaurantByUUID(String restaurantUuid)throws RestaurantNotFoundException {

        if(restaurantUuid == null||restaurantUuid == "")
        {
            throw new RestaurantNotFoundException("RNF-002","Restaurant id field should not be empty");
        }

        // get RestaurantEntity
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantUuid);

        if (restaurantEntity == null)
        {
            throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
        }

        return restaurantEntity;

    }

    // This method updates Restaurant Rating

    @Transactional(propagation = Propagation.REQUIRED)
    public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity, Double customerRating) throws InvalidRatingException {

        if(!utilityProvider.isValidCustomerRating(customerRating.toString()))
        {
            throw new InvalidRatingException("IRE-001","Restaurant should be in the range of 1 to 5");
        }

        // updating customer rating

        DecimalFormat format = new DecimalFormat("##.0");
        double restaurantRating = restaurantEntity.getCustomerRating();
        Integer restaurantNoOfCustomerRated = restaurantEntity.getNumberCustomersRated();
        restaurantEntity.setNumberCustomersRated(restaurantNoOfCustomerRated+1);

        //calculating customer rating
        double newCustomerRating =
                (restaurantRating*(restaurantNoOfCustomerRated.doubleValue())+customerRating)/restaurantEntity.getNumberCustomersRated();

        restaurantEntity.setCustomerRating(Double.parseDouble(format.format(newCustomerRating)));

        // update restaurant rating
        RestaurantEntity updatedRestaurantEntity = restaurantDao.updateRestaurantRating(restaurantEntity);

        return updatedRestaurantEntity;

    }

}

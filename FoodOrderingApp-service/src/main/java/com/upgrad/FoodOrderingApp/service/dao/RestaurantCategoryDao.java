package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/*
    This Class contains methods for Category entity
 */

@Repository
public class RestaurantCategoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    // get RestaurantCategoryEntity list from db by restaurantEntity

    public List<RestaurantCategoryEntity> getCategoriesByRestaurant(RestaurantEntity restaurantEntity){
        try {
            List<RestaurantCategoryEntity> restaurantCategoryEntity =
                    entityManager.createNamedQuery("getCategoriesByRestaurant",RestaurantCategoryEntity.class)
                            .setParameter("restaurant",restaurantEntity).getResultList();
            return restaurantCategoryEntity;
        }catch (NoResultException nre){
            return null;
        }

    }

    // get RestaurantCategoryEntity list from db by categoryEntity

    public List<RestaurantCategoryEntity> getRestaurantByCategory(CategoryEntity categoryEntity) {
        try {
            List<RestaurantCategoryEntity> restaurantCategoryEntities =
                    entityManager.createNamedQuery("getRestaurantByCategory",RestaurantCategoryEntity.class)
                            .setParameter("category",categoryEntity).getResultList();
            return restaurantCategoryEntities;
        }catch (NoResultException nre){
            return null;
        }
    }

}

package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * DAO for Order entity.
 */
@Repository
public class OrderDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<OrderEntity> getOrdersByRestaurant(RestaurantEntity restaurantEntity) {
        try{
            List<OrderEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByRestaurant", OrderEntity.class).setParameter("restaurant",restaurantEntity).getResultList();
            return ordersEntities;
        }catch (NoResultException nre){
            return null;
        }
    }

    //To get all the order corresponding to the address
    public List<OrderEntity> getOrdersByAddress(AddressEntity addressEntity) {
        try{
            List<OrderEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByAddress", OrderEntity.class).setParameter("address",addressEntity).getResultList();
            return ordersEntities;
        }catch (NoResultException nre) {
            return null;
        }
    }

    public OrderEntity saveOrder(OrderEntity orderEntity) {
        entityManager.persist(orderEntity);
        return orderEntity;
    }

    public List<OrderEntity> getOrdersByCustomers(CustomerEntity customerEntity) {
        try {
            List<OrderEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByCustomers", OrderEntity.class).setParameter("customer",customerEntity).getResultList();
            return ordersEntities;
        }catch (NoResultException nre){
            return null;
        }
    }
}

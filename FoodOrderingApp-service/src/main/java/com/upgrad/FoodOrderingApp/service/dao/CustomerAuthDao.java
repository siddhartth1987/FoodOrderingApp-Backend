package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

//Class to perform Db operations on CustomerAuth
@Repository
public class CustomerAuthDao {

    @PersistenceContext
    private EntityManager entityManager;

    // Method returns CustomerAuthEntity based on AccessToken
    public CustomerAuthEntity getCustomerAuthByAccessToken(String accessToken){
        try{
            CustomerAuthEntity customerAuthEntity =
                    entityManager.createNamedQuery("getCustomerAuthByAccessToken",CustomerAuthEntity.class)
                            .setParameter("access_Token",accessToken).getSingleResult();
            return customerAuthEntity;
        }catch (NoResultException nre){
            return null;
        }
    }

    // Method saves CustomerAuthEntity
    public CustomerAuthEntity createCustomerAuth (CustomerAuthEntity customerAuthEntity){
        entityManager.persist(customerAuthEntity);
        return customerAuthEntity;
    }

    // Method updates CustomerAuthEntity
    public CustomerAuthEntity customerLogout (CustomerAuthEntity customerAuthEntity){
        entityManager.merge(customerAuthEntity);
        return customerAuthEntity;
    }
}

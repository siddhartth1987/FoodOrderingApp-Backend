package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

//Class to perform Db operations on CustomerAddress
@Repository
public class CustomerAddressDao {

    @PersistenceContext
    private EntityManager entityManager;

    // Method to save Customer Address
    public CustomerAddressEntity saveCustomerAddress(CustomerAddressEntity customerAddressEntity){
        entityManager.persist(customerAddressEntity);
        return customerAddressEntity;
    }

    // Method to get all saved Addresses for a customer
    public List<CustomerAddressEntity> getAllCustomerAddressByCustomer(CustomerEntity customerEntity){
        try{
            Integer active = 1;
            List <CustomerAddressEntity> customerAddressEntities =
                    entityManager.createNamedQuery("getAllCustomerAddressByCustomer",CustomerAddressEntity.class)
                    .setParameter("customer_entity",customerEntity)
                            .setParameter("active",active).getResultList();
            return customerAddressEntities;
        }catch (NoResultException nre){
            return null;
        }
    }

    // Method to get Customer Address By Address
    public CustomerAddressEntity getCustomerAddressByAddress(AddressEntity addressEntity){
        try {
            CustomerAddressEntity customerAddressEntity =
                    entityManager.createNamedQuery("getCustomerAddressByAddress",CustomerAddressEntity.class)
                            .setParameter("address_entity",addressEntity).getSingleResult();
            return customerAddressEntity;
        }catch (NoResultException nre){
            return null;
        }
    }
}

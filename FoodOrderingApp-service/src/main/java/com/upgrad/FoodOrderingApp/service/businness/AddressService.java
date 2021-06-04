package com.upgrad.FoodOrderingApp.service.businness;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.upgrad.FoodOrderingApp.service.common.UtilityProvider;
import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;

//This Class handles all service related to the address
@Service
public class AddressService {
    @Autowired
    AddressDao addressDao;

    @Autowired
    CustomerAuthDao customerAuthDao;

    @Autowired
    UtilityProvider utilityProvider;

    @Autowired
    StateDao stateDao;

    @Autowired
    CustomerAddressDao customerAddressDao;

    // method to save address
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity addressEntity,StateEntity stateEntity)throws SaveAddressException{

        // Checking if any field is empty in the address entity.
        if (addressEntity.getCity() == null || addressEntity.getFlatBuilNo() == null || addressEntity.getPincode() == null || addressEntity.getLocality() == null){
            throw new SaveAddressException("SAR-001","No field can be empty");
        }
        // validate format of pin code
        if(!utilityProvider.isPincodeValid(addressEntity.getPincode())){
            throw new SaveAddressException("SAR-002","Invalid pincode");
        }

        // set state
        addressEntity.setState(stateEntity);

        // save Address method and return  saved address.
        AddressEntity savedAddress = addressDao.saveAddress(addressEntity);
        return savedAddress;
    }

    // this method soft deletes address
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(AddressEntity addressEntity) {

        // set and update active status of address
        addressEntity.setActive(0);
        AddressEntity updatedAddressActiveStatus =  addressDao.updateAddressActiveStatus(addressEntity);
        return updatedAddressActiveStatus;
    }

    // this method fetches all address for a customer
    public List<AddressEntity> getAllAddress(CustomerEntity customerEntity) {

        List<AddressEntity> addressList = new LinkedList<>();

        // get Customer Address based on CustomerEntity and return Address List.
        List<CustomerAddressEntity> customerAddressEntities  = customerAddressDao.getAllCustomerAddressByCustomer(customerEntity);
        if(customerAddressEntities != null) { //Checking if CustomerAddressEntity is null else extracting address and adding to the addressEntites list.
            customerAddressEntities.forEach(customerAddressEntity -> {
                addressList.add(customerAddressEntity.getAddress());
            });
        }
        return addressList;
    }

    // this method returns state
    public StateEntity getStateByUUID (String uuid)throws AddressNotFoundException{
        // get State details and if not present throw error.
        StateEntity stateEntity = stateDao.getStateByUuid(uuid);
        if(stateEntity == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        return  stateEntity;
    }

    // this method returns all states present in the Db
    public List<StateEntity> getAllStates(){

        // fetch and return all States.
        List<StateEntity> stateEntities = stateDao.getAllStates();
        return stateEntities;
    }

    // This method is used to create and persist info in CustomerAddress table
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity saveCustomerAddressEntity(CustomerEntity customerEntity,AddressEntity addressEntity){

        // create and set data in new CustomerAddressEntity.
        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setCustomer(customerEntity);
        customerAddressEntity.setAddress(addressEntity);

        // save CustomerAddressEntity
        CustomerAddressEntity createdCustomerAddressEntity = customerAddressDao.saveCustomerAddress(customerAddressEntity);
        return createdCustomerAddressEntity;

    }

    // This method fetches address details based on UUID
    public AddressEntity getAddressByUUID(String addressUuid,CustomerEntity customerEntity)throws AuthorizationFailedException,AddressNotFoundException{

        if(addressUuid == null){
            throw new AddressNotFoundException("ANF-005","Address id can not be empty");
        }

        // get addressEntity
        AddressEntity addressEntity = addressDao.getAddressByUuid(addressUuid);
        if (addressEntity == null){
            throw new AddressNotFoundException("ANF-003","No address by this id");
        }

        // get CustomerAddressEntity
        CustomerAddressEntity customerAddressEntity = customerAddressDao.getCustomerAddressByAddress(addressEntity);

        // compare customerAddressEntity and CustomerEntity
        if(customerAddressEntity.getCustomer().getUuid() == customerEntity.getUuid()){
            return addressEntity;
        }else{
            throw new AuthorizationFailedException("ATHR-004","You are not authorized to view/update/delete any one else's address");
        }
    }
}
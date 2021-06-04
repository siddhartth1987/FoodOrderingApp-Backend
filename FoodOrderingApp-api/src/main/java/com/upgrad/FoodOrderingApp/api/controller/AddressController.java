package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/*Customer Controller handles endpoints pertaining to customers.
    saveAddress
    deleteSavedAddress
    getAllSavedAddress
    getAllStates
 */

@CrossOrigin
@RestController
@RequestMapping("/")
public class AddressController {

    @Autowired
    AddressService addressService;

    @Autowired
    CustomerService customerService;

    // this method saves address
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST,path = "/address",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization") final String authorization, @RequestBody(required = false)SaveAddressRequest saveAddressRequest)throws AuthorizationFailedException, AddressNotFoundException, SaveAddressException {

        // get accessToken from request Header
        String accessToken = authorization.split("Bearer ")[1];

        // check validity of the customer
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        // create instance of address entity to be saved
        AddressEntity addressEntity = new AddressEntity();

        addressEntity.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setUuid(UUID.randomUUID().toString());


        // get state entity
        StateEntity stateEntity = addressService.getStateByUUID(saveAddressRequest.getStateUuid());

        // save address
        AddressEntity savedAddress = addressService.saveAddress(addressEntity,stateEntity);

        // add entry in Customer Address table corresponding to the new saved address
        CustomerAddressEntity customerAddressEntity = addressService.saveCustomerAddressEntity(customerEntity,savedAddress);

        // generate response
        SaveAddressResponse saveAddressResponse = new SaveAddressResponse()
                .id(savedAddress.getUuid())
                .status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse,HttpStatus.CREATED);
    }

    // this method deletes address
    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE,path = "/address/{address_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteSavedAddress(@RequestHeader ("authorization") final String authorization,@PathVariable(value = "address_id")final String addressUuid)throws AuthorizationFailedException,AddressNotFoundException{

        // get accessToken from request Header
        String accessToken = authorization.split("Bearer ")[1];

        // check validity of the customer
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        // get the address based on UUID
        AddressEntity addressEntity = addressService.getAddressByUUID(addressUuid,customerEntity);

        // delete address from DB
        AddressEntity deletedAddressEntity = addressService.deleteAddress(addressEntity);

        // generate response
        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse()
                .id(UUID.fromString(deletedAddressEntity.getUuid()))
                .status("ADDRESS DELETED SUCCESSFULLY");

        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse,HttpStatus.OK);
    }

    // this method fetches all saved address
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,path = "/address/customer",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllSavedAddress(@RequestHeader("authorization")final String authorization)throws AuthorizationFailedException{

        // get accessToken from request Header
        String accessToken = authorization.split("Bearer ")[1];

        // check validity of the customer
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        // fetch list of all addresses and sort in desc order
        List<AddressEntity> addressEntities = addressService.getAllAddress(customerEntity);
        Collections.reverse(addressEntities);

        // create list for AddressListResponse
        List<AddressList> addressLists = new LinkedList<>();

        addressEntities.forEach(addressEntity -> {
            AddressListState addressListState = new AddressListState()
                    .stateName(addressEntity.getState().getStateName())
                    .id(UUID.fromString(addressEntity.getState().getStateUuid()));
            AddressList addressList = new AddressList()
                    .id(UUID.fromString(addressEntity.getUuid()))
                    .city(addressEntity.getCity())
                    .flatBuildingName(addressEntity.getFlatBuilNo())
                    .locality(addressEntity.getLocality())
                    .pincode(addressEntity.getPincode())
                    .state(addressListState);
            addressLists.add(addressList);
        });

        // generate response
        AddressListResponse addressListResponse = new AddressListResponse().addresses(addressLists);
        return new ResponseEntity<AddressListResponse>(addressListResponse,HttpStatus.OK);
    }


    // method to fetch all states.
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,path = "/states",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResponseEntity<StatesListResponse> getAllStates(){

        // fetch all states
        List<StateEntity> stateEntities = addressService.getAllStates();

        if(!stateEntities.isEmpty()) {

            // create list for StatesListResponse
            List<StatesList> statesLists = new LinkedList<>();

            stateEntities.forEach(stateEntity -> {
                StatesList statesList = new StatesList()
                        .id(UUID.fromString(stateEntity.getStateUuid()))
                        .stateName(stateEntity.getStateName());
                statesLists.add(statesList);
            });

            // generate response
            StatesListResponse statesListResponse = new StatesListResponse().states(statesLists);
            return new ResponseEntity<StatesListResponse>(statesListResponse, HttpStatus.OK);
        }else
            return new ResponseEntity<StatesListResponse>(new StatesListResponse(),HttpStatus.OK);
    }
}
package com.upgrad.FoodOrderingApp.service.common;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Common class to run generic validations
@Component
public class UtilityProvider {

        // validate format of password
        public boolean isValidPassword(String password){
            Boolean lowerCase = false;
            Boolean upperCase = false;
            Boolean number = false;
            Boolean specialCharacter = false;

            if(password.length() < 8){
                return false;
            }

            if(password.matches("(?=.*[0-9]).*")){
                number = true;
            }

            if(password.matches("(?=.*[a-z]).*")){
                lowerCase = true;
            }
            if(password.matches("(?=.*[A-Z]).*")){
                upperCase = true;
            }
            if(password.matches("(?=.*[#@$%&*!^]).*")){
                specialCharacter = true;
            }

            if(lowerCase && upperCase){
                if(specialCharacter && number){
                    return true;
                }
            }else{
                return false;
            }
            return false;
        }

        // validate email format
        public boolean isEmailValid(String email) {
            String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
            return email.matches(regex);
        }

        // validate contactNumber
        public boolean isContactValid(String contactNumber){
            Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");
            Matcher m = p.matcher(contactNumber);
            return (m.find() && m.group().equals(contactNumber));
        }

        // validate Pincode format
        public boolean isPincodeValid(String pincode){
            Pattern p = Pattern.compile("\\d{6}\\b");
            Matcher m = p.matcher(pincode);
            return (m.find() && m.group().equals(pincode));
        }

        // validate mandatory fields during Sign-Up
        public boolean isValidSignupRequest (CustomerEntity customerEntity)throws SignUpRestrictedException{
            if (customerEntity.getFirstName() == null || customerEntity.getFirstName() == ""){
                throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
            }
            if(customerEntity.getPassword() == null||customerEntity.getPassword() == ""){
                throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
            }
            if (customerEntity.getEmail() == null||customerEntity.getEmail() == ""){
                throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
            }
            if (customerEntity.getContactNumber() == null||customerEntity.getContactNumber() == ""){
                throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
            }
            return true;
        }

        // validate format of Basic authentication
        public boolean isValidAuthorizationFormat(String authorization)throws AuthenticationFailedException{
            try {
                byte[] decoded = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
                String decodedAuth = new String(decoded);
                String[] decodedArray = decodedAuth.split(":");
                String username = decodedArray[0];
                String password = decodedArray[1];
                return true;
            }catch (ArrayIndexOutOfBoundsException exc){
                throw new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
            }
        }

        // validate first name before customer update
        public boolean isValidUpdateCustomerRequest (String firstName)throws UpdateCustomerException {
            if (firstName == null || firstName == "") {
                throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
            }
            return true;
        }

        // validate old and new password fields before password update
        public boolean isValidUpdatePasswordRequest(String oldPassword,String newPassword) throws UpdateCustomerException{
            if (oldPassword == null || oldPassword == "") {
                throw new UpdateCustomerException("UCR-003", "No field should be empty");
            }
            if (newPassword == null || newPassword == "") {
                throw new UpdateCustomerException("UCR-003", "No field should be empty");
            }
            return true;
        }

        //To validate the Customer rating
        public boolean isValidCustomerRating(String cutomerRating){
            if(cutomerRating.equals("5.0")){
                return true;
            }
            Pattern p = Pattern.compile("[1-4].[0-9]");
            Matcher m = p.matcher(cutomerRating);
            return (m.find() && m.group().equals(cutomerRating));
        }

    // sort HashMap
    public Map<String,Integer> sortMapByValues(Map<String,Integer> map){

        // Create list from elements of itemCountMap
        List<Map.Entry<String,Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());

        // Sort list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue().compareTo(o1.getValue()));
            }
        });

        // create and return Sorted HashMap

        Map<String, Integer> sortedByValueMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> item : list) {
            sortedByValueMap.put(item.getKey(), item.getValue());
        }

        return sortedByValueMap;
    }
}
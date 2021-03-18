package ca.mcgill.ecse321.arms.service;
import ca.mcgill.ecse321.arms.ArmsApplication;
import ca.mcgill.ecse321.arms.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ca.mcgill.ecse321.arms.model.*;
import ca.mcgill.ecse321.arms.dao.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private BillRepository billRepository;

    @Transactional
    public Customer CreatAccount(String username, String password, String email, String phonenumber) throws IllegalArgumentException {
        String error = "";
        if (username == null || username.isEmpty()) {
            error = "The user name cannot be empty";
        } else if (password.length()<=8) {
            error = "The password cannot be less than 8 characters";
        } else if(!isValidEmailAddress(email)) {
            error = "The email is not valid";
        } else if(phonenumber.length()==0) {
            error = "The phone number is not valid";
        } else if(customerRepository.findCustomerByUsername(username)!=null){
            error = "The username already exists";
        }
        if (error.length() > 0) {
            throw new IllegalArgumentException(error.trim());
        }
        Customer customer = new Customer();
        customer.setPassword(password);
        customer.setUsername(username);
        customer.setEmail(email);
        customer.setPhoneNumber(phonenumber);
        customerRepository.save(customer);
        return customer;
    }

    @Transactional
    public Customer getCustomer(String username) throws IllegalArgumentException{
        String error = "";
        if (username == null || username.isEmpty()) {
            error = "The user name cannot be empty";
        } else if(customerRepository.findCustomerByUsername(username)==null){
            error = "The username doesn't exist";
        }
        if (error.length() > 0) {
            throw new IllegalArgumentException(error.trim());
        }
        return customerRepository.findCustomerByUsername(username);
    }

    @Transactional
    public Customer updateAccount(String username,String password, String email, String phonenumber) {
        String error = "";
        Customer customer=customerRepository.findCustomerByUsername(username);
        if (username == null ) {
            error = "The user name cannot be empty";
        } else if (email == null ) {
            error = "The email cannot be empty";
        } else if (phonenumber == null ) {
            error = "The phone number cannot be empty";
        } else if (password.length() <= 8) {
            error = "The password must be longer than 8 characters";
        }else if (customer == null) {
            error = "The username doesn't exist";
        } else if (ArmsApplication.getCurrentuser().equals(customer)) {
            error = "You can only update your own account";
        }
        if (error.length() > 0) {
            throw new IllegalArgumentException(error.trim());
        }
        customer.setPassword(password);
        customer.setEmail(email);
        customer.setPhoneNumber(phonenumber);
        customerRepository.save(customer);
        return customer;
    }

    @Transactional
    public Integer deleteAccount(){
        String error = "";
        Customer customer = (Customer) ArmsApplication.getCurrentuser();
        List<Bill> bills=billRepository.findBillsByCustomer(customer);
        if (bills!=null) {
            for (Bill bill : bills) {
                if (!bill.isIsPaid()) {
                    error = "You have an unpaid bill";
                    break;
                }
            }
        }
        if (error.length() > 0) {
            throw new IllegalArgumentException(error.trim());
        }
        Integer i = customerRepository.deleteCustomerByUsername(customer.getUsername());
        ArmsApplication.setCurrentuser(null);
        return i;
    }

    private static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }


}

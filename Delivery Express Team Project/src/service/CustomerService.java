package com.group11.assignment5.service;


import com.group11.assignment5.model.Customer;

public interface CustomerService {
    void makeCustomer(String customerId, String firstName, String lastName, String phoneNumber, int customerRating, double credit, double x, double y);
    void displayCustomers();
    void distributeCoupon(int highFreq, int lowFreq);
    void displayCoupon(Customer customer);
}


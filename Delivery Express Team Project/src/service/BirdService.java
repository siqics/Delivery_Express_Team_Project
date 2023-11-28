package com.group11.assignment5.service;


import com.group11.assignment5.exception.DeliveryServiceException;
import com.group11.assignment5.model.Bird;
import com.group11.assignment5.model.Customer;
import com.group11.assignment5.model.Store;


public interface BirdService {
    int getRandomIndex(int size);
    void reassignBirdCoordinate(Bird bird, Store currentStore, Customer currentCustomer) throws DeliveryServiceException;
    void makeBird(String birdId, int probAttack) throws DeliveryServiceException;
    void displayBirds();
}

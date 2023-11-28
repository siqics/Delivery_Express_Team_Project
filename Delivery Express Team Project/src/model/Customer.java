package com.group11.assignment5.model;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Document(collection = "Customer")
@Data
public class Customer {

    @Id
    private String customerId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int customerRating;
    private double credit;
    private double pendingTotalCost;
    private Coordinate coordinate;
    private List<Coupon> coupons = new LinkedList<>(); //mongodb does not support queue
    private Boolean hasBird;
    private Bird bird;
    private Boolean willAttack;
    private Date lastCheckoutTime;

    public Customer(String customerId, String firstName, String lastName, String phoneNumber, int customerRating, double credit, Coordinate coordinate){
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.customerRating = customerRating;
        this.credit = credit;
        this.coordinate = coordinate;
        this.hasBird = false;
        this.willAttack = false;
    }

    public String getFullName(){
        return this.firstName + "_" + this.lastName;
    }
    public void markBirdAtCustomer(Bird bird) {
        this.hasBird = true;
        this.bird = bird;
    }
    public void deleteBirdAtCustomer() {
        this.hasBird = false;
        this.bird = null;
        this.willAttack = false;
    }

}

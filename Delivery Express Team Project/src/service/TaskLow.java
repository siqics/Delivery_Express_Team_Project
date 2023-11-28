package com.group11.assignment5.service;

import com.group11.assignment5.config.ConstantConfig;
import com.group11.assignment5.model.Coupon;
import com.group11.assignment5.model.Customer;
import com.group11.assignment5.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
public class TaskLow implements Runnable{
    private final Customer customer;
    private final CustomerRepository customerRepository;

    @Override
    public void run() {
        //what job we need to do in task high (for high rating customer)
        for (int i = 0; i < ConstantConfig.NUM_OF_COUPON_FOR_LOW_RATING_CUSTOMER; i++) {
            customer.getCoupons().add(new Coupon(new Date()));
        }
        customerRepository.save(customer);
    }
}

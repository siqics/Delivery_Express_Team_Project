package com.group11.assignment5.service;

import com.group11.assignment5.config.ConstantConfig;
import com.group11.assignment5.model.Customer;
import com.group11.assignment5.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class TaskFactory {
    @Autowired
    private CustomerRepository customerRepository;
    public Runnable getTask(Customer customer) {
        if (customer.getCustomerRating() > ConstantConfig.CUSTOMER_RATING_THRESHOLD) {
            return new TaskHigh(customer, customerRepository);
        } else {
            return new TaskLow(customer, customerRepository);
        }
    }
}

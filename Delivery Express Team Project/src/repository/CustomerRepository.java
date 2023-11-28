package com.group11.assignment5.repository;

import com.group11.assignment5.model.Coordinate;
import com.group11.assignment5.model.Customer;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    Customer findOneByCustomerId(String customerId);
    Customer findOneByCoordinate(Coordinate coordinate);
    @Aggregation(pipeline ={"{$sort: {customerId: 1}}"})
    List<Customer> findAll();

}

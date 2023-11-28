package com.group11.assignment5.repository;

import com.group11.assignment5.model.Bird;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BirdRepository extends MongoRepository<Bird, String> {
    Bird findOneByBirdId(String birdId);

    @Aggregation(pipeline ={"{$sort: {birdId: 1}}"})
    List<Bird> findAll();
}
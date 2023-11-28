package com.group11.assignment5.repository;
import com.group11.assignment5.model.Coordinate;
import com.group11.assignment5.model.Store;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StoreRepository extends MongoRepository<Store, String> {
    Store findOneByStoreName(String storeName);
    Store findOneByCoordinate(Coordinate coordinate);

    @Aggregation(pipeline ={"{$sort: {storeName: 1}}"})
    List<Store> findAll();
}

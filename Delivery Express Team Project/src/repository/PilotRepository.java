package com.group11.assignment5.repository;

import com.group11.assignment5.model.Pilot;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PilotRepository extends MongoRepository<Pilot, String> {
    Pilot findOneByPilotId(String pilotId);
    Pilot findOneByLicenseId(String licenseId);

    @Aggregation(pipeline ={"{$sort: {pilotId: 1}}"})
    List<Pilot> findAll();
}

package com.group11.assignment5.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Pilot")
@Data
public class Pilot {
    @Id
    private String pilotId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String taxId;
    private String licenseId;
    private int numberOfDeliveries;
    private String[] currentStoreNameAndDroneId;// currentStoreNameAndDroneId[0] is the current storeName and currentStoreNameAndDroneId[1] is current droneId due to droneId is only unique per store
    private Boolean hasAssignedDrone = Boolean.FALSE;

    public Pilot (String pilotId, String firstName, String lastName, String phoneNumber, String taxId, String licenseId, int numberOfDeliveries){
        this.pilotId = pilotId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.taxId = taxId;
        this.licenseId = licenseId;
        this.numberOfDeliveries = numberOfDeliveries;
    }
    public String getFullName(){
        return this.firstName + "_" + this.lastName;
    }
}

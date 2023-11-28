package com.group11.assignment5.model;



import com.group11.assignment5.repository.StoreRepository;
import lombok.Data;

import java.util.*;

//from "Store" collection in db
@Data
public class Drone {
    private String droneId;
    private double weightCapacity;
    private double remainingCapacity; //decrease when requestItem, increase when cancelOrder or purchaseOrder ( < weightCapacity)
    private int tripsLeft; //fuel or max#OfDeliveries, decrease when purchaseOrder  ( > 0)
    private int numberOfOrders; //1) startOrder - increase; 2) purchaseOrder/cancelOrder -decrease; 3) transfer order - one drone increase, another drone decrease
    private String currentPilotAccountId;
    private Boolean hasAssignedPilot = Boolean.FALSE;
    private int overloadOrderCount;
    private int purchasedOrderCount;
    private int transferredOrderCount;
    private double droneSpeed;
    private DroneStatus droneStatus;
    private List<String> orderIds = new ArrayList<>();

    public Drone (String droneId, double weightCapacity, int tripsLeft, double droneSpeed) {
        this.droneId = droneId;
        this.weightCapacity = weightCapacity;
        this.remainingCapacity = weightCapacity;
        this.tripsLeft = tripsLeft;
        this.droneSpeed = droneSpeed;
        this.droneStatus = DroneStatus.IDLE;
    }

}

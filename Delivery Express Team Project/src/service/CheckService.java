package com.group11.assignment5.service;

import com.group11.assignment5.exception.DeliveryServiceException;
import com.group11.assignment5.model.*;



import java.util.Map;

public interface CheckService {
    void checkStoreExists(String storeName) throws DeliveryServiceException;
    void checkAddressExists(double x, double y) throws DeliveryServiceException;
    double checkNumber(String input) throws DeliveryServiceException;
    Store checkStoreNotExist(String storeName) throws DeliveryServiceException;
    void checkItemIdExistInStore(String itemName, Map<String, Item> items) throws DeliveryServiceException;
    void checkArgsCount(String[] tokens, int requiredCount) throws DeliveryServiceException;
    void checkPilotIdExists(String pilotId) throws DeliveryServiceException;
    void checkPilotLicenseExists(String licenseId) throws DeliveryServiceException;
    Order checkOrderIdNotExist(String orderId, Map<String, Order> orders)throws DeliveryServiceException;
    Item checkItemIdNotExistInStore(String itemId, Map<String, Item> items)throws DeliveryServiceException;
    void checkItemAlreadyOrdered(String itemName, Map<String, Line> lines) throws DeliveryServiceException;
    void checkCustomerCredit(String customerId, int quantity, double unitPrice) throws DeliveryServiceException;
    void checkDroneCapacity(Drone drone, double weight)throws DeliveryServiceException;
    void checkDroneCapacityWhenTransferOrder(Drone drone, double weight)throws DeliveryServiceException;
    void checkOrderExists(String orderId, Map<String, Order> orders) throws DeliveryServiceException;
    void checkDroneIdExists(String droneId, Map<String, Drone> drones) throws DeliveryServiceException;
    Customer checkCustomerNotExists(String customerId) throws DeliveryServiceException;
    Drone checkDroneIdNotExist(String droneId, Map<String, Drone> drones) throws DeliveryServiceException;
    Pilot checkPilotIdNotExist(String pilotId) throws DeliveryServiceException;
    void checkCustomerIdExists(String customerId) throws DeliveryServiceException;
    void checkDroneTripLeft(Drone drone, int numOfOrder) throws DeliveryServiceException;
    int checkDroneHasOrder(Drone drone) throws DeliveryServiceException;
    void checkDroneHasAssignedPilot(Drone drone) throws DeliveryServiceException;
    void checkAllOrderCheckedOut(Store store, Drone drone) throws DeliveryServiceException;
    void checkFrequencyValid(int highFreq, int lowFreq) throws DeliveryServiceException;
    void checkBirdIdExists(String birdId) throws DeliveryServiceException;
    void checkProbAttackRange(int probAttack) throws DeliveryServiceException;
    void checkStoreOrCustomerExist() throws DeliveryServiceException;
    void checkCommand (String token) throws DeliveryServiceException;
    void checkOrderAlreadyCheckedOut (Order order) throws DeliveryServiceException;
    void checkDroneNotInRepair(Drone drone) throws DeliveryServiceException;
    void checkCustomerToDistributeCoupons() throws DeliveryServiceException;
    Map<String, Order> getOrdersFromDrone(Store store, Drone drone);
    void checkOrderStatusIsCreated(Order order) throws DeliveryServiceException;

    void orderHasItems(Order order) throws DeliveryServiceException;
}

package com.group11.assignment5.service;
import com.group11.assignment5.model.*;
import com.group11.assignment5.exception.DeliveryServiceException;

import java.util.Date;
import java.util.Map;
import java.util.Queue;

public interface StoreService {
    void makeStore(String storeName, double initialRevenue, double x, double y);
    void displayStores();
    void sellItem(Store store, String itemName, double weight);
    void displayItems(Store store);
    void makeDrone(Store store, String droneId, double weightCapacity, int tripsLeft, double droneSpeed);
    void displayDrones(Store store);
    void makeOrder(Store store, String orderID, Drone drone, String customerId);
    void displayOrders(Store store);
    void requestItem(Store store, Order order, Item item, int quantity, double unitPrice);
    void checkOutOrder(Store store, Order order);
    void deliverOrders(Store store, Drone drone) throws DeliveryServiceException;
    void flyDrone(Store store, Drone drone, Pilot pilot);
    void deliverOneOrder(Store store, Drone drone, Pilot pilot, Customer customer, Order order, Date startDate, double duration);
    void cancelOrder(Store store, Order order);
    void transferOrder(Store store, Order order, Drone drone);
    void displayEfficiency();
    void transferOrdersToAnotherDrone(Store store, Drone oldDrone) throws DeliveryServiceException;

    void sendDroneToRepair(Drone drone, Store store);


}

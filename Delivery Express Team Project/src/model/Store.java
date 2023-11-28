package com.group11.assignment5.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.TreeMap;

@Document(collection = "Store")
@Data
public class Store {
    @Id
    private String storeName;
    private double revenue;
    private Map<String, Item> items = new TreeMap<>();
    private Map<String, Drone> drones = new TreeMap<>();
    private Map<String, Order> orders = new TreeMap<>();
    private Coordinate coordinate;
    private Boolean hasBird;
    private Bird bird;
    private Boolean willAttack;
    public Store (String storeName, double revenue, Coordinate coordinate) {
        this.storeName = storeName;
        this.revenue = revenue;
        this.coordinate = coordinate;
        this.hasBird = false;
        this.willAttack = false;
    }
    public void setItems(String itemName, double weight) {
        this.items.put(itemName, new Item(itemName, weight));
    }
    public void setDrones(String droneId, double weightCapacity, int tripsLeft, double droneSpeed) {
        this.drones.put(droneId, new Drone(droneId, weightCapacity, tripsLeft, droneSpeed));
    }
    public Order setOrders(String orderID, String droneID, String customerID){
        Order newOrder = new Order(orderID, droneID, customerID);
        this.orders.put(orderID, newOrder);
        return newOrder;
    }
    public void markBirdAtStore(Bird bird) {
        this.hasBird = true;
        this.bird = bird;
    }
    public void deleteBirdAtStore() {
        this.hasBird = false;
        this.bird = null;
        this.willAttack = false;
    }
    public void removeOrder(Order order){
        this.orders.remove(order.getOrderId());

    }


}

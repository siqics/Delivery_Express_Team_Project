package com.group11.assignment5.model;

import lombok.Data;


import java.util.Map;
import java.util.TreeMap;
import java.util.Date;

@Data
public class Order {
    private String orderId;
    private String droneId; //assign a drone when the order is created
    private String customerId;
    private Coupon coupon;
    private Map<String, Line> lines = new TreeMap<>();
    private OrderStatus orderStatus;
    //add checkoutTime, use for decide delivery sequence order.
    private Date checkoutTime;
    private Long deliverDuration = 0L;
    private Date deliveryTime;

    public Order (String orderId, String droneId, String customerId) {
        this.orderId = orderId;
        this.droneId = droneId;
        this.customerId = customerId;
        this.orderStatus = OrderStatus.CREATED;

    }
    public void setLines(Item item, int quantity, double unitPrice){
        this.lines.put(item.getItemName(), new Line(item, quantity,unitPrice));
    }
    public double getTotalCost() {
        double totalCost  = 0;
        for(Line line : this.lines.values()) {
            totalCost += line.getTotalCost();
        }
        return totalCost;
    }
public double getTotalWeight() {
        double totalWeight = 0;
        for(Line line : this.lines.values()) {
            totalWeight += line.getTotalWeight();
        }
        return totalWeight;
    }

}


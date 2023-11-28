package com.group11.assignment5.model;

import lombok.Data;


@Data
public class Line {
    private Item item;
    private int quantity;
    private double unitPrice;
    private double totalCost;
    private double totalWeight;
    public Line(Item item, int quantity, double unitPrice) {
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalCost = this.quantity * this.unitPrice;
        this.totalWeight = this.quantity * this.item.getWeight();
    }
}

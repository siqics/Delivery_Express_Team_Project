package com.group11.assignment5.model;

import lombok.AllArgsConstructor;
import lombok.Data;

//from "Store" collection in db
@Data
@AllArgsConstructor //constructor must have 2 args - itemName and weight
public class Item {
    private String itemName;
    private double weight;
}

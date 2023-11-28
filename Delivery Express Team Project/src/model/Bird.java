package com.group11.assignment5.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "Bird")
@Data
public class Bird {
    @Id
    private String birdId;
    private int probAttack;
    private Coordinate coordinate;

    public Bird(String birdId, int probAttack) {
        this.birdId = birdId;
        this.probAttack = probAttack;
    }
    public boolean doesBirdAttack() {
        int randProb = new Random().nextInt(100);
        return randProb < this.probAttack;
    }
}
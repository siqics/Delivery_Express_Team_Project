package com.group11.assignment5.model;
import java.util.Date;

import com.group11.assignment5.config.ConstantConfig;
import lombok.Data;
import java.util.UUID;

@Data
public class Coupon {
    private UUID couponId;
    private Date distributeDate;
    private Date expirationDate;
    private double discountRate = ConstantConfig.DISCOUNT_RATE;

    public Coupon(Date distributeDate){
        this.couponId = UUID.randomUUID();
        this.distributeDate = distributeDate;
        // add 7 days
        this.expirationDate = new Date(distributeDate.getTime() + ConstantConfig.COUPON_EXPIRE_DURATION);
    }
}

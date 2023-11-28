package com.group11.assignment5.service;

import com.group11.assignment5.config.ConstantConfig;
import com.group11.assignment5.message.OtherMessageType;
import com.group11.assignment5.model.*;
import com.group11.assignment5.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService{
    private CustomerRepository customerRepository;
    private SchedulerService schedulerService;
    private final TaskFactory taskFactory;
    private static Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Override
    public void makeCustomer(String customerId, String firstName, String lastName, String phoneNumber, int customerRating, double credit, double x, double y){
        Customer newCustomer = new Customer(customerId, firstName, lastName, phoneNumber, customerRating, credit, new Coordinate(x, y));
        customerRepository.save(newCustomer);
        logger.info(OtherMessageType.CHANGE_COMPLETED.getMessage());
    }
    @Override
    public void displayCustomers() {
        List<Customer> customers = customerRepository.findAll();
        for(Customer customer : customers) {
            logger.info("customerId:{},name:{},phone:{},rating:{},credit:{},rating:{},coordinate:[{},{}],hasBird:{},willAttack:{},couponsCount:{}, lastCheckoutTime:{}",customer.getCustomerId(),customer.getFullName(), customer.getPhoneNumber(), customer.getCustomerRating(), customer.getCredit(), customer.getCustomerRating(),
                    customer.getCoordinate().getX(), customer.getCoordinate().getY(), customer.getHasBird(), customer.getWillAttack(), customer.getCoupons().size(), customer.getLastCheckoutTime());
        }
        logger.info(OtherMessageType.DISPLAY_COMPLETED.getMessage());
    }
    @Override
    public void distributeCoupon(int highFreq, int lowFreq){
        logger.info("Start distributing coupons. Customer who has rating above {} will get {} coupon(s) every {} day(s), " +
                "and other customers will get {} coupon(s) every {} day(s)", ConstantConfig.CUSTOMER_RATING_THRESHOLD, ConstantConfig.NUM_OF_COUPON_FOR_HIGH_RATING_CUSTOMER, highFreq,
                ConstantConfig.NUM_OF_COUPON_FOR_LOW_RATING_CUSTOMER, lowFreq);
        //Call this function to distribute coupon to customer
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            Runnable task = taskFactory.getTask(customer);
            if(customer.getCustomerRating() > ConstantConfig.CUSTOMER_RATING_THRESHOLD) {
                schedulerService.scheduleTask(task, Duration.ofMinutes(highFreq));
            }
            else{
                schedulerService.scheduleTask(task, Duration.ofMinutes(lowFreq)); //the user input is for days, we use mins here for system demo
            }
        }

    }
    @Override
    public void displayCoupon(Customer customer) {
        for(Coupon coupon : customer.getCoupons()) {
            logger.info("coupon_id:{},coupon_expire_date:{},coupon_discount_rate:{}", coupon.getCouponId(), coupon.getExpirationDate(), coupon.getDiscountRate());
        }
        logger.info(OtherMessageType.DISPLAY_COMPLETED.getMessage());
    }


}

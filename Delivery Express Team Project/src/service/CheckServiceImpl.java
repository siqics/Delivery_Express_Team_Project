package com.group11.assignment5.service;

import com.group11.assignment5.exception.DeliveryServiceException;
import com.group11.assignment5.exception.ExceptionMessageType;
import com.group11.assignment5.model.*;
import com.group11.assignment5.repository.CustomerRepository;
import com.group11.assignment5.repository.PilotRepository;
import com.group11.assignment5.repository.StoreRepository;
import com.group11.assignment5.repository.BirdRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Service
public class CheckServiceImpl implements CheckService{
    private StoreRepository storeRepository;
    private PilotRepository pilotRepository;
    private CustomerRepository customerRepository;
    private BirdRepository birdRepository;

    private static Set<String> ALLOWED_COMMANDS = new HashSet<String>(){{
        add("make_store");
        add("display_stores");
        add("sell_item");
        add("display_items");
        add("make_pilot");
        add("display_pilots");
        add("make_drone");
        add("display_drones");
        add("fly_drone");
        add("make_customer");
        add("display_customers");
        add("help");
        add("create_bird");
        add("display_birds");
        add("start_order");
        add("display_orders");
        add("request_item");
        add("cancel_order");
        add("transfer_order");
        add("display_efficiency");
        add("checkout_order");
        add("deliver_orders");
        add("distribute_coupon");
        add("stop_distribute_coupon");
        add("display_coupon");
        add("stop");
    }};

    @Override
    public void checkStoreExists(String storeName) throws DeliveryServiceException {
        if (storeRepository.findOneByStoreName(storeName) != null) {
            throw new DeliveryServiceException(ExceptionMessageType.STORE_ID_EXISTS);
        }
    }
    @Override
    public void checkAddressExists(double x, double y) throws DeliveryServiceException {
        Coordinate coordinate = new Coordinate(x, y);
        if (storeRepository.findOneByCoordinate(coordinate) != null || customerRepository.findOneByCoordinate(coordinate) != null){
            throw new DeliveryServiceException(ExceptionMessageType.STORE_ADDRESS_ALREADY_EXISTS);
        }
    }
    @Override
    public double checkNumber(String input) throws DeliveryServiceException {
        if (input == null) {
            throw new DeliveryServiceException(ExceptionMessageType.INPUTS_SHOULD_BE_NUMBERS);
        }
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new DeliveryServiceException(ExceptionMessageType.INPUTS_SHOULD_BE_NUMBERS);
        }
    }
    @Override
    public Store checkStoreNotExist(String storeName) throws DeliveryServiceException {
        Store store = storeRepository.findOneByStoreName(storeName);
        if(store == null){
            throw new DeliveryServiceException(ExceptionMessageType.STORE_ID_NOT_EXIST);
        }
        return store;
    }
    @Override
    public void checkItemIdExistInStore(String itemName, Map<String, Item> items) throws DeliveryServiceException {
        if(items.containsKey(itemName)){
            throw new DeliveryServiceException(ExceptionMessageType.ITEM_ID_EXISTS);
        }
    }
    @Override
    public void checkArgsCount(String[] tokens, int requiredCount) throws DeliveryServiceException {
        if(tokens.length != requiredCount){
            throw new DeliveryServiceException(ExceptionMessageType.INCORRECT_ARGS_COUNT);
        }
    }
    @Override
    public void checkPilotIdExists(String pilotId) throws DeliveryServiceException {
        if (pilotRepository.findOneByPilotId(pilotId) != null) {
            throw new DeliveryServiceException(ExceptionMessageType.PILOT_ID_EXISTS);
        }
    }
    @Override
    public void checkPilotLicenseExists(String licenseId) throws DeliveryServiceException {
        if(pilotRepository.findOneByLicenseId(licenseId) != null){
            throw new DeliveryServiceException(ExceptionMessageType.PILOT_LICENSE_EXISTS);
        }
    }
    @Override
    public void checkDroneIdExists(String droneId, Map<String, Drone> drones) throws DeliveryServiceException {
        if (drones.containsKey(droneId)) {
            throw new DeliveryServiceException(ExceptionMessageType.DRONE_ID_EXISTS);
        }
    }
    @Override
    public Drone checkDroneIdNotExist(String droneId, Map<String, Drone> drones) throws DeliveryServiceException{
        if (!drones.containsKey(droneId)) {
            throw new DeliveryServiceException(ExceptionMessageType.DRONE_ID_NOT_EXIST);
        }
        return drones.get(droneId);
    }
    @Override
    public Order checkOrderIdNotExist(String orderId, Map<String, Order> orders)throws DeliveryServiceException{
        if(!orders.containsKey(orderId)){
            throw new DeliveryServiceException(ExceptionMessageType.ORDER_ID_NOT_EXIST);
        }
        return orders.get(orderId);
    }
    @Override
    public Item checkItemIdNotExistInStore(String itemId, Map<String, Item> items)throws DeliveryServiceException{
        if(!items.containsKey(itemId)){
            throw new DeliveryServiceException(ExceptionMessageType.ITEM_ID_NOT_EXIST);
        }
        return items.get(itemId);
    }
    @Override
    public void checkItemAlreadyOrdered(String itemName, Map<String, Line> lines) throws DeliveryServiceException {
        if (lines.containsKey(itemName)){
            throw new DeliveryServiceException(ExceptionMessageType.ITEM_ALREADY_ORDERED);
        }
    }
    @Override
    public void checkCustomerCredit(String customerId, int quantity, double unitPrice) throws DeliveryServiceException {
        Customer customer = customerRepository.findOneByCustomerId(customerId);
        double totalPendingCostForCustomer = customer.getPendingTotalCost() + quantity * unitPrice;
        if(customer.getCredit() < totalPendingCostForCustomer ) {
            throw new DeliveryServiceException(ExceptionMessageType.CUSTOMER_CANT_AFFORD);
        }
    }
    @Override
    public void checkDroneCapacity(Drone drone, double weight)throws DeliveryServiceException{
        //changed the item and quantity to weight instead so that this can be used later for Order weight too
        if(drone.getRemainingCapacity()<weight) {
            throw new DeliveryServiceException(ExceptionMessageType.DRONE_CANT_CARRY);
        }
    }
    @Override
    public void checkDroneCapacityWhenTransferOrder(Drone drone, double weight)throws DeliveryServiceException{
        //changed the item and quantity to weight instead so that this can be used later for Order weight too
        if(drone.getRemainingCapacity() < weight) {
            throw new DeliveryServiceException(ExceptionMessageType.DRONE_NOT_ENOUGH_CAPACITY);
        }
    }

    @Override
    public void checkOrderExists(String orderId, Map<String, Order> orders) throws DeliveryServiceException {
        if(orders.containsKey(orderId)){
            throw new DeliveryServiceException(ExceptionMessageType.ORDER_ID_EXISTS);
        }
    }

    @Override
    //this is for test use
    public Customer checkCustomerNotExists(String customerId) throws DeliveryServiceException {
        Customer customer = customerRepository.findOneByCustomerId(customerId);
        if (customer == null) {
            throw new DeliveryServiceException(ExceptionMessageType.CUSTOMER_ID_NOT_EXIST);
        }
        return customer;
    }
    @Override
    public Pilot checkPilotIdNotExist(String pilotId) throws DeliveryServiceException{
        Pilot pilot = pilotRepository.findOneByPilotId(pilotId);
        if (pilot == null) {
            throw new DeliveryServiceException(ExceptionMessageType.PILOT_ID_NOT_EXIST);
        }
        return pilot;
    }
    @Override
    public void checkCustomerIdExists(String customerId) throws DeliveryServiceException {
        if (customerRepository.findOneByCustomerId(customerId) != null) {
            throw new DeliveryServiceException(ExceptionMessageType.CUSTOMER_ID_EXISTS);
        }
    }
    @Override
    public void checkBirdIdExists(String birdId) throws DeliveryServiceException {
        if (birdRepository.findOneByBirdId(birdId) != null) {
            throw new DeliveryServiceException(ExceptionMessageType.BIRD_ID_EXISTS);
        }
    }
    @Override
    public void checkDroneTripLeft(Drone drone, int numOfOrder) throws DeliveryServiceException{
        if(drone.getTripsLeft() < numOfOrder){
            throw new DeliveryServiceException(ExceptionMessageType.DRONE_NEEDS_FUEL);
        }
    }
    @Override
    public int checkDroneHasOrder(Drone drone) throws DeliveryServiceException{
        if(drone.getNumberOfOrders() == 0){
            throw new DeliveryServiceException(ExceptionMessageType.DRONE_ORDER_NOT_EXIST);
        }
        return drone.getNumberOfOrders();
    }
    @Override
    public void checkDroneHasAssignedPilot(Drone drone) throws DeliveryServiceException{
        if(!drone.getHasAssignedPilot()){
            throw new DeliveryServiceException(ExceptionMessageType.DRONE_NEEDS_PILOT);
        }

    }
    @Override
    public void checkAllOrderCheckedOut(Store store, Drone drone) throws DeliveryServiceException {
        //if any of the orders on this drone has not been checked out
        boolean orderNeedCheckout = false;

        for(Order order : getOrdersFromDrone(store, drone).values()) {
            if (order.getOrderStatus() != OrderStatus.CHECKED_OUT) {
                orderNeedCheckout = true;
            }
        }
        if(orderNeedCheckout){
            throw new DeliveryServiceException(ExceptionMessageType.ORDER_NEEDS_CHECKOUT);
        }
    }
    @Override
    public void checkFrequencyValid(int highFreq, int lowFreq) throws DeliveryServiceException {
        if (highFreq >= lowFreq || highFreq <= 0) {
            throw new DeliveryServiceException(ExceptionMessageType.INVALID_FREQUENCY);
        }
    }
    @Override      
    public void checkProbAttackRange(int probAttack) throws DeliveryServiceException{
        if(probAttack < 0 || probAttack > 100) {
            throw new DeliveryServiceException(ExceptionMessageType.INCORRECT_PROB_RANGE);
        }
    }
    @Override
    public void checkStoreOrCustomerExist() throws DeliveryServiceException {
        if(storeRepository.count() + customerRepository.count() == 0){
            throw new DeliveryServiceException(ExceptionMessageType.NO_STORE_OR_CUSTOMER_EXIST);
        }
    }
    @Override
    public void checkCommand (String token) throws DeliveryServiceException {
        if(!ALLOWED_COMMANDS.contains(token)) {
            throw new DeliveryServiceException(ExceptionMessageType.COMMAND_NOT_FOUND);
        }
    }
    @Override
    public void checkOrderAlreadyCheckedOut (Order order) throws DeliveryServiceException {
        if(order.getOrderStatus() == OrderStatus.CHECKED_OUT) {
            throw new DeliveryServiceException(ExceptionMessageType.ORDER_ALREADY_CHECKED_OUT);
        }
    }
    @Override
    public void checkDroneNotInRepair(Drone drone) throws DeliveryServiceException {
        if(drone.getDroneStatus() == DroneStatus.IN_REPAIR){
            throw new DeliveryServiceException(ExceptionMessageType.DRONE_IN_REPAIR);
        }
    }
    @Override
    public void checkCustomerToDistributeCoupons() throws DeliveryServiceException {
        if(customerRepository.count() == 0){
            throw new DeliveryServiceException(ExceptionMessageType.NO_CUSTOMER_TO_DISTRIBUTE_COUPONS);
        }
    }
    @Override
    public Map<String, Order> getOrdersFromDrone(Store store, Drone drone) {
        List<String> orderIds = drone.getOrderIds();
        Map<String, Order> orders = new TreeMap<>();
        for(String orderId : orderIds) {
            orders.put(orderId, store.getOrders().get(orderId));
        }
        return orders;
    }
    @Override
    public void checkOrderStatusIsCreated(Order order) throws DeliveryServiceException {
        if(order.getOrderStatus() != OrderStatus.CREATED){
            throw new DeliveryServiceException(ExceptionMessageType.ORDER_STATUS_MUST_BE_STATUS_TO_ADD_ITEM);
        }
    }
    @Override
    public void orderHasItems(Order order) throws DeliveryServiceException {
        if(order.getLines().size() == 0) {
            throw new DeliveryServiceException(ExceptionMessageType.ORDER_MUST_HAS_ITEMS_ADDED_BEFORE_CHECKOUT);
        }
    }

}

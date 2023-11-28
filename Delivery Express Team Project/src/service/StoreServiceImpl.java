package com.group11.assignment5.service;

import com.group11.assignment5.config.ConstantConfig;
import com.group11.assignment5.exception.DeliveryServiceException;
import com.group11.assignment5.message.OtherMessageType;
import com.group11.assignment5.model.*;
import com.group11.assignment5.repository.CustomerRepository;
import com.group11.assignment5.repository.PilotRepository;
import com.group11.assignment5.repository.StoreRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Service
public class StoreServiceImpl implements StoreService {
    private StoreRepository storeRepository;
    private PilotRepository pilotRepository;
    private CustomerRepository customerRepository;
    private CheckService checkService;
    private BirdService birdService;
    private static Logger logger = LoggerFactory.getLogger(StoreServiceImpl.class);

    @Override
    public void makeStore(String storeName, double initialRevenue, double x, double y) {
        Store newStore = new Store(storeName, initialRevenue, new Coordinate(x, y));
        storeRepository.save(newStore);
        logger.info(OtherMessageType.CHANGE_COMPLETED.getMessage());

    }

    @Override
    public void displayStores() {
        storeRepository.findAll().forEach((Store store) -> {
            logger.info("name:{},revenue:{},coordinate:[{},{}],hasBird:{},willAttack:{}", store.getStoreName(), store.getRevenue(), store.getCoordinate().getX(), store.getCoordinate().getY(), store.getHasBird(), store.getWillAttack());
        });
        logger.info(OtherMessageType.DISPLAY_COMPLETED.getMessage());
    }

    @Override
    public void sellItem(Store store, String itemName, double weight) {
        store.setItems(itemName, weight);
        storeRepository.save(store);
        logger.info(OtherMessageType.CHANGE_COMPLETED.getMessage());
    }

    @Override
    public void displayItems(Store store) {
        Map<String, Item> sortedItems = new TreeMap<>(store.getItems()); //sad... it seems like mongodb does not support treemap, I have to cast it to treemap again
        for (Item item : sortedItems.values()) {
            logger.info("{},{}", item.getItemName(), item.getWeight());
        }
        logger.info(OtherMessageType.DISPLAY_COMPLETED.getMessage());
    }

    @Override
    public void makeDrone(Store store, String droneId, double weightCapacity, int tripsLeft, double droneSpeed) {
        store.setDrones(droneId, weightCapacity, tripsLeft, droneSpeed);
        storeRepository.save(store);
        logger.info(OtherMessageType.CHANGE_COMPLETED.getMessage());
    }

    @Override
    public void displayDrones(Store store) {
        Map<String, Drone> sortedDrones = new TreeMap<>(store.getDrones());
        for (Drone drone : sortedDrones.values()) {
            Pilot pilot = pilotRepository.findOneByPilotId(drone.getCurrentPilotAccountId());
            String pilotFullName = pilot == null ? "" : pilot.getFullName();
            String flownByStr = drone.getHasAssignedPilot() ? ",flown_by:" + pilotFullName : "";
            logger.info("droneID:{},total_cap:{},num_orders:{},order_ids:{},remaining_cap:{},trips_left:{}{},drone_speed:{},drone_status:{}", drone.getDroneId(), drone.getWeightCapacity(),
                    drone.getNumberOfOrders(), drone.getOrderIds(), drone.getRemainingCapacity(), drone.getTripsLeft(), flownByStr, drone.getDroneSpeed(), drone.getDroneStatus());
        }
        logger.info(OtherMessageType.DISPLAY_COMPLETED.getMessage());
    }

    @Override
    public void makeOrder(Store store, String orderID, Drone drone, String customerId) {
        Order newOrder = store.setOrders(orderID, drone.getDroneId(), customerId);
        drone.setNumberOfOrders(drone.getNumberOfOrders() + 1);
        //sma added for checkout and deliver order
        drone.setDroneStatus(DroneStatus.ASSIGNED_ORDER);
        drone.getOrderIds().add(orderID);
        storeRepository.save(store);
        logger.info(OtherMessageType.CHANGE_COMPLETED.getMessage());
    }

    @Override
    public void displayOrders(Store store) {
        Map<String, Order> sortedOrders = new TreeMap<>(store.getOrders());
        for (Order order : sortedOrders.values()) {
            logger.info("orderID:{},droneId:{},customerId:{},order_status:{},checkout_time:{},delivery_time:{}", order.getOrderId(), order.getDroneId(),order.getCustomerId(),order.getOrderStatus(), order.getCheckoutTime(), order.getDeliveryTime());
            Map<String, Line> sortedLines = new TreeMap<>(order.getLines());
            for (Line line : sortedLines.values()) {
                logger.info("item_name:{},total_quantity:{},total_cost:{},total_weight:{},", line.getItem().getItemName(), line.getQuantity(), line.getTotalCost(), line.getTotalWeight());
            }
        }
        logger.info(OtherMessageType.DISPLAY_COMPLETED.getMessage());
    }

    @Override
    public void requestItem(Store store, Order order, Item item, int quantity, double unitPrice) {
        Customer customer = customerRepository.findOneByCustomerId(order.getCustomerId());
        Drone drone = store.getDrones().get(order.getDroneId());
        double totalPendingCostForCustomer = customer.getPendingTotalCost() + quantity * unitPrice;
        double newWeight = quantity * item.getWeight();
        customer.setPendingTotalCost(totalPendingCostForCustomer);//update here
        order.setLines(item, quantity, unitPrice);
        drone.setRemainingCapacity(drone.getRemainingCapacity() - newWeight);
        storeRepository.save(store);
        customerRepository.save(customer);
        logger.info(OtherMessageType.CHANGE_COMPLETED.getMessage());
    }
    @Override
    public void checkOutOrder(Store store, Order order){

        Customer customer = customerRepository.findOneByCustomerId(order.getCustomerId());

        //check num of coupon, if customer has unexpired coupons, apply the first unexpired coupon automatically
        double totalCost = order.getTotalCost();
        boolean findOneValidCoupon = false;
        Date currentTime = new Date();
        while (!customer.getCoupons().isEmpty()){
            if(findOneValidCoupon){
                break;
            }
            Coupon coupon = customer.getCoupons().remove(0); //pop out coupon and check if it is valid
            if(!coupon.getExpirationDate().before(currentTime)) {
                continue;
            }
            totalCost = totalCost * (1 - coupon.getDiscountRate());
            order.setCoupon(coupon);
            logger.info("customer {} has succesfully applied coupon {} to order {} at checkout.", customer.getCustomerId(), coupon.getCouponId(), order.getOrderId());
            findOneValidCoupon = true;

        }
        customer.setCredit(customer.getCredit() - totalCost); //deduct order total cost from the customer's credit;
        store.setRevenue(store.getRevenue() + totalCost); //add order total cost to the store's revenue

        order.setOrderStatus(OrderStatus.CHECKED_OUT);
        Date dataNow = new Date();
        order.setCheckoutTime(dataNow);
        customer.setLastCheckoutTime(dataNow);
        customerRepository.save(customer);
        storeRepository.save(store);
        logger.info(OtherMessageType.CHANGE_COMPLETED.getMessage());
    }

    @Override
    public void deliverOrders(Store store, Drone drone) throws DeliveryServiceException {
        Date startDate = new Date(System.currentTimeMillis());
        Pilot pilot = pilotRepository.findOneByPilotId(drone.getCurrentPilotAccountId());
        drone.setDroneStatus(DroneStatus.IN_FLY);
        storeRepository.save(store);
        //sort order list for delivery based on customer'last order checkout time
        Queue<Order> orderQueue = new PriorityQueue<>((order1, order2) -> {
            return order1.getCheckoutTime().compareTo(order2.getCheckoutTime());
        });
        checkService.getOrdersFromDrone(store, drone).values().forEach(order -> {
            orderQueue.add(order);
        });

        //set the start locationA as store location
        Coordinate locationA = store.getCoordinate();
        boolean getAttakedDuringTheDeliver = false;

        // for every order in the queue, send the drone to this order's location, if attack, we first try to transfer it to a different drone
        while (!orderQueue.isEmpty()){
            Order order = orderQueue.poll();
            //check if the drone has been attacked at store
            if(store.getWillAttack()){
                logger.info("Drone is attacked at store {}", store.getStoreName());
                getAttakedDuringTheDeliver = true;
                //try transfer the remaining orders to other drones
                transferOrdersToAnotherDrone(store, drone);
                //reassign the bird after attack
                birdService.reassignBirdCoordinate(store.getBird(), store, null);

                //break the loop, Delivery Failed
                break;
            }

            // if the drone has not been attacked at store
            //set the destination to this order's customer location
            Customer customer = customerRepository.findOneByCustomerId(order.getCustomerId());
            Coordinate locationB = customer.getCoordinate();

            //calculate the duration base on distance and speed.
            double xOfLocationA = locationA.getX();
            double yOfLocationA = locationA.getY();
            double xOfLocationB = locationB.getX();
            double yOfLocationB = locationB.getY();

            double distance = Math.sqrt(Math.pow((xOfLocationA - xOfLocationB), 2) + Math.pow((yOfLocationA - yOfLocationB), 2));
            double duration = distance / drone.getDroneSpeed();

            //if the drone has been attacked at customer's location
            if (customer.getWillAttack()){
                logger.info("Drone is attacked at customer {}", customer.getCustomerId());
                getAttakedDuringTheDeliver = true;
                // add the duration from this customer to store for all orders on the drone
                for (Order remainOrder : orderQueue) {
                    remainOrder.setDeliverDuration(remainOrder.getDeliverDuration() + (long) duration);
                }

                //try transfer the remaining orders to other drones
                transferOrdersToAnotherDrone(store, drone);
                //reassign the bird after attack
                birdService.reassignBirdCoordinate(customer.getBird(), null, customer);

                //break the loop, Delivery Failed
                break;
            }
            //if the drone has not been attacked at all
            //deliver this one order successfully

            deliverOneOrder(store, drone, pilot, customer, order, startDate, duration);

            //add duration to the remaining orders on the drone
            for (Order remainOrder : orderQueue) {
                remainOrder.setDeliverDuration(remainOrder.getDeliverDuration() + (long) duration);
            }

            //set the start location to this order's location
            locationA = locationB;
        }
        if(getAttakedDuringTheDeliver){
            //now we have checked all orders, we should sendDroneToRepair
            sendDroneToRepair(drone,store);
        }
        storeRepository.save(store);



    }
    @Override
    //For each order do this function
    public void deliverOneOrder(Store store, Drone drone, Pilot pilot, Customer customer, Order order, Date startDate, double duration){

        drone.setTripsLeft(drone.getTripsLeft() - 1);
        pilot.setNumberOfDeliveries(pilot.getNumberOfDeliveries() + 1);
        drone.setRemainingCapacity(drone.getRemainingCapacity() + order.getTotalWeight()); //drone's remaining cap increase because the order is off the drone
        drone.setPurchasedOrderCount(drone.getPurchasedOrderCount() + 1);
        //the drone's overloads should be updated since there is a new purchased order
        //if first time purchase
        if(drone.getPurchasedOrderCount() == 1){
            drone.setOverloadOrderCount(drone.getNumberOfOrders() - 1);
        }
        //if totalNumberOfOrders is 1 (last order), no change to overloads
        else if (drone.getNumberOfOrders() != 1){
            drone.setOverloadOrderCount(drone.getOverloadOrderCount() + 1);
        }
        drone.setNumberOfOrders(drone.getNumberOfOrders() - 1);
        drone.setDroneStatus(DroneStatus.IDLE);
        removeOrderIdFromDrone(drone, order.getOrderId());

        //update the customer Rating, increase customer rating by 1
        customer.setCustomerRating(customer.getCustomerRating() + 1);

        //add duration to the delivered order and assign DeliveryTime
        order.setDeliverDuration((long) duration);
        Date endTime = new Date(startDate.getTime() + order.getDeliverDuration());
        order.setDeliveryTime(endTime);
        order.setOrderStatus(OrderStatus.DELIVERED);
        logger.info("order {} is delivered successfully", order.getOrderId());

        //if Coupon has expired, give the customer another coupon as penalty for the store
        if(order.getCoupon() != null && order.getCoupon().getExpirationDate().before(endTime)){
            customer.getCoupons().add(new Coupon(new Date()));
            logger.info("customer {} gets an additonal coupon because coupon expires when delivery", customer.getCustomerId());
        }

        pilotRepository.save(pilot);
        customerRepository.save(customer);
        storeRepository.save(store);
        logger.info(OtherMessageType.CHANGE_COMPLETED.getMessage());
    }
    @Override
    public void cancelOrder(Store store, Order order) {
        Drone drone = store.getDrones().get(order.getDroneId());
        drone.setNumberOfOrders(drone.getNumberOfOrders() - 1); //drone's num of orders decrease by 1
        drone.setRemainingCapacity(drone.getRemainingCapacity() + order.getTotalWeight()); //drone's remaining cap increase by the delivered order weight
        store.removeOrder(order);
        storeRepository.save(store);//update the repository
        logger.info(OtherMessageType.CHANGE_COMPLETED.getMessage());
    }
    @Override
    public void flyDrone(Store store, Drone drone, Pilot pilot) {
        if (drone.getHasAssignedPilot()) {//the drone is currently controlled by another pilot
            //release drone's current pilot
            Pilot currentPilot = pilotRepository.findOneByPilotId(drone.getCurrentPilotAccountId());
            currentPilot.setHasAssignedDrone(Boolean.FALSE);
            pilotRepository.save(currentPilot);

        }
        if (pilot.getHasAssignedDrone()) {//the pilot is currently controlling another drone
            //release pilot's current drone
            String[] currentStoreNameAndDroneId = pilot.getCurrentStoreNameAndDroneId();
            Store storeWithCurrentDrone = storeRepository.findOneByStoreName(currentStoreNameAndDroneId[0]);
            Drone currentDrone = storeWithCurrentDrone.getDrones().get(currentStoreNameAndDroneId[1]);
            currentDrone.setHasAssignedPilot(false);
            currentDrone.setCurrentPilotAccountId("");
            storeRepository.save(storeWithCurrentDrone);
        }
        //assign drone to pilot
        drone.setCurrentPilotAccountId(pilot.getPilotId());
        pilot.setCurrentStoreNameAndDroneId(new String[]{store.getStoreName(), drone.getDroneId()});
        drone.setHasAssignedPilot(true);
        pilot.setHasAssignedDrone(true);
        //need to get storeFromDb because the store might be changed
        Store storeFromDB = storeRepository.findOneByStoreName(store.getStoreName());
        storeFromDB.getDrones().replace(drone.getDroneId(), drone);
        storeRepository.save(storeFromDB);
        pilotRepository.save(pilot);
        logger.info(OtherMessageType.CHANGE_COMPLETED.getMessage());
    }

    @Override
    public void transferOrder(Store store, Order order, Drone drone) {
        if (order.getDroneId().equals(drone.getDroneId())) {
            logger.info(OtherMessageType.NEW_DRONE_IS_CURRENT_NO_CHANGE.getMessage());
        }
        else {//for old drone, the number of orders should be decreased by 1
            //for old drone, the remainingCapacity should be increased
            Drone oldDrone = store.getDrones().get(order.getDroneId());
            oldDrone.setNumberOfOrders(oldDrone.getNumberOfOrders() - 1);
            oldDrone.setRemainingCapacity(oldDrone.getRemainingCapacity() + order.getTotalWeight());
            removeOrderIdFromDrone(oldDrone, order.getOrderId());
            if(oldDrone.getOrderIds().size() == 0) {
                oldDrone.setDroneStatus(DroneStatus.IDLE);
            }

            //update order and associate it with new drone
            order.setDroneId(drone.getDroneId());
            drone.setNumberOfOrders(drone.getNumberOfOrders() + 1);
            drone.setRemainingCapacity(drone.getRemainingCapacity() - order.getTotalWeight());
            drone.setTransferredOrderCount(drone.getTransferredOrderCount() + 1);
            drone.getOrderIds().add(order.getOrderId());
            drone.setDroneStatus(DroneStatus.ASSIGNED_ORDER);
            storeRepository.save(store);
            logger.info("Order {} is transferred to Drone {}", order.getOrderId(), drone.getDroneId());
        }
    }

    @Override
    public void displayEfficiency() {
        for (Store store : storeRepository.findAll()) {
            int purchases = 0;
            int overloads = 0;
            int transfers = 0;
            //get all the drones from this store
            Map<String, Drone> sortedDrone = new TreeMap<>(store.getDrones());
            for (Drone drone : sortedDrone.values()) {
                purchases += drone.getPurchasedOrderCount();
                overloads += drone.getOverloadOrderCount();
                transfers += drone.getTransferredOrderCount();
            }
            //each store calculate the efficiency then output
            //loop through all stores...
            logger.info("name:{},purchases:{},overloads:{},transfers:{}", store.getStoreName(), purchases, overloads,transfers);
        }
    }
    @Override
    public void transferOrdersToAnotherDrone(Store store, Drone oldDrone) throws DeliveryServiceException {
        logger.info("System is looking for other drones to transfer remaining order...");
        List<String> orderIds = new ArrayList<>(oldDrone.getOrderIds());
        for(String orderId : orderIds) {
            Order order = store.getOrders().get(orderId);
            for(Drone drone : store.getDrones().values()) {
                if(drone.getDroneStatus() == DroneStatus.IN_FLY || drone.getDroneStatus() == DroneStatus.IN_REPAIR || drone.getDroneId().equals(oldDrone.getDroneId())){
                    continue;
                }
                checkService.checkDroneCapacityWhenTransferOrder(drone, order.getTotalWeight());

                transferOrder(store, order, drone);
                break;
            }
        }
    }
    @Override
    public void sendDroneToRepair(Drone drone, Store store) {
        logger.info("drone {} is sent to repair. Please wait...", drone.getDroneId());
        //add the repair time to delivery duration

        drone.setDroneStatus(DroneStatus.IN_REPAIR);
        List<String> orderIds = drone.getOrderIds();
        for(String orderId : orderIds) {
            Order order = store.getOrders().get(orderId);
            long originDeliverDuration = order.getDeliverDuration();
            order.setDeliverDuration(originDeliverDuration + ConstantConfig.REPAIR_DURATION);

        }
        if(orderIds.size() == 0){
            drone.setDroneStatus(DroneStatus.IDLE);
        }
        else{
            drone.setDroneStatus(DroneStatus.ASSIGNED_ORDER);
        }
        storeRepository.save(store);
        logger.info("Repair is done for drone {}", drone.getDroneId());
    }
    public void removeOrderIdFromDrone(Drone drone, String orderId) {
        List<String> orderIds = drone.getOrderIds();
        int removeIndex = -1;
        for(int i = 0; i < orderIds.size(); i++){
            if(orderIds.get(i).equals(orderId)){
                removeIndex = i;
                break;
            }
        }
        if(removeIndex != -1){
            orderIds.remove(removeIndex);
        }
    }

}


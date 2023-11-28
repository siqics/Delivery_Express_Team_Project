package com.group11.assignment5.service;

import com.group11.assignment5.exception.DeliveryServiceException;
import com.group11.assignment5.message.OtherMessageType;
import com.group11.assignment5.model.*;
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
public class CommandServiceImpl implements CommandService{
    private CheckService checkService;
    private StoreService storeService;
    private PilotService pilotService;
    private CustomerService customerService;
    private BirdService birdService;
    private SchedulerService schedulerService;

    private static Logger logger = LoggerFactory.getLogger(CommandServiceImpl.class);
  
    @Override
    public void commandLoop() {
        logger.info("~*~*~*~*~*~*~*~*~*~Welcome to the Grocery Express Delivery Service!~*~*~*~*~*~*~*~*~*~");
        logger.info("~*~*~*~*~*~*~If you want to read the instructions, please enter \"help\"~*~*~*~*~*~*~");
        Scanner commandLineInput = new Scanner(System.in);
        String wholeInputLine;
        String[] tokens;
        final String DELIMITER = ",";

        label:
        while (commandLineInput.hasNextLine()) {
            try {
                wholeInputLine = commandLineInput.nextLine();
                tokens = wholeInputLine.split(DELIMITER);
                trimTokens(tokens); //to improve robustness, we add this to remove leading and trailing spaces for any token
                logger.info("> " + wholeInputLine);
                if (tokens[0].startsWith("//")) {
                    continue;
                }
                checkService.checkCommand(tokens[0]); //only allow certain commands
                switch (tokens[0]) {
                    case "make_store": {
                        //make_store, storeName, initialRevenue, locationX, locationY
                        checkService.checkArgsCount(tokens, 5); //to improve robustness, we add this to check the args count
                        checkService.checkStoreExists(tokens[1]);
                        double initialRevenue = checkService.checkNumber(tokens[2]);//to improve robustness, we add this to check some inputs should be parsed to numbers
                        double x = checkService.checkNumber(tokens[3]);
                        double y = checkService.checkNumber(tokens[4]);
                        checkService.checkAddressExists(x, y); //to improve robustness, we add this to check if some stores have the same address
                        storeService.makeStore(tokens[1], initialRevenue, x, y);

                        break;
                    }
                    case "display_stores": {
                        //display_stores
                        checkService.checkArgsCount(tokens, 1);
                        storeService.displayStores();

                        break;
                    }
                    case "sell_item": {
                        //sell_item, storeName, itemName, weight
                        checkService.checkArgsCount(tokens, 4);
                        Store store = checkService.checkStoreNotExist(tokens[1]);
                        double weight = checkService.checkNumber(tokens[3]);//to improve robustness, we add this to check some inputs should be parsed to numbers
                        String itemName = tokens[2];
                        checkService.checkItemIdExistInStore(itemName, store.getItems());
                        storeService.sellItem(store, itemName, weight);

                        break;
                    }

                    case "display_items": {
                        //display_items, storeName
                        checkService.checkArgsCount(tokens, 2);
                        Store store = checkService.checkStoreNotExist(tokens[1]);
                        storeService.displayItems(store);

                        break;
                    }
                    case "make_pilot": {
                        //make_pilot, pilotId, firstName, lastName, phoneNumber, taxId, licenseId, numberOfDeliveries
                        checkService.checkArgsCount(tokens, 8);
                        checkService.checkPilotIdExists(tokens[1]);
                        checkService.checkPilotLicenseExists(tokens[6]);
                        int numberOfDeliveries = (int) checkService.checkNumber(tokens[7]);
                        pilotService.makePilot(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], numberOfDeliveries);

                        break;
                    }
                    case "display_pilots": {
                        //display_pilots
                        checkService.checkArgsCount(tokens, 1);
                        pilotService.displayPilots();

                        break;
                    }
                    case "make_drone": {
                        //make_drone, storeName, droneId, weightCapacity, tripsLeft(fuel), droneSpeed
                        checkService.checkArgsCount(tokens, 6);
                        Store store = checkService.checkStoreNotExist(tokens[1]);
                        checkService.checkDroneIdExists(tokens[2], store.getDrones());
                        double weightCapacity = checkService.checkNumber(tokens[3]);
                        int tripsLeft = (int) checkService.checkNumber(tokens[4]);
                        double droneSpeed = checkService.checkNumber(tokens[5]);
                        storeService.makeDrone(store, tokens[2], weightCapacity, tripsLeft, droneSpeed);

                        break;
                    }
                    case "display_drones": {
                        //display_drones, storeName
                        checkService.checkArgsCount(tokens, 2);
                        Store store = checkService.checkStoreNotExist(tokens[1]);
                        storeService.displayDrones(store);

                        break;
                    }
                    case "fly_drone": {
                        //fly_drone, storeName, droneId, pilotId
                        checkService.checkArgsCount(tokens, 4);
                        Store store = checkService.checkStoreNotExist(tokens[1]);
                        Drone drone = checkService.checkDroneIdNotExist(tokens[2], store.getDrones());
                        Pilot pilot = checkService.checkPilotIdNotExist(tokens[3]);
                        checkService.checkDroneNotInRepair(drone);
                        storeService.flyDrone(store, drone, pilot);

                        break;
                    }
                    case "make_customer": {
                        //make_customer,customerID,Alana,Apple,222-222-2222,customerRating,credit, locationX, locationY
                        checkService.checkArgsCount(tokens, 9);
                        checkService.checkCustomerIdExists(tokens[1]);
                        int rating = (int) checkService.checkNumber(tokens[5]);
                        double credit = checkService.checkNumber(tokens[6]);
                        double location_x = checkService.checkNumber(tokens[7]);
                        double location_y = checkService.checkNumber(tokens[8]);
                        checkService.checkAddressExists(location_x, location_x); //to improve robustness, we add this to check if some stores have the same address
                        customerService.makeCustomer(tokens[1], tokens[2], tokens[3], tokens[4], rating, credit, location_x, location_y);

                        break;
                    }
                    case "display_customers": {
                        //display_customers
                        checkService.checkArgsCount(tokens, 1);
                        customerService.displayCustomers();

                        break;
                    }

                    case "create_bird": {
                        //create_bird, birdId, probAttack(integer from 0 - 100)
                        checkService.checkArgsCount(tokens, 3);
                        checkService.checkStoreOrCustomerExist();
                        checkService.checkBirdIdExists(tokens[1]);
                        int probAttack = (int) checkService.checkNumber(tokens[2]);
                        checkService.checkProbAttackRange(probAttack);
                        birdService.makeBird(tokens[1], probAttack);

                        break;
                    }
                    case "display_birds": {
                        //display_birds
                        checkService.checkArgsCount(tokens, 1);
                        birdService.displayBirds();

                        break;
                    }
                    case "start_order": {
                        //start_order, storeName, orderId, droneId, customerId
                        checkService.checkArgsCount(tokens, 5);
                        Store store = checkService.checkStoreNotExist(tokens[1]);
                        checkService.checkOrderExists(tokens[2], store.getOrders());
                        Drone drone = checkService.checkDroneIdNotExist(tokens[3], store.getDrones());
                        checkService.checkDroneNotInRepair(drone);
                        checkService.checkCustomerNotExists(tokens[4]);
                        storeService.makeOrder(store, tokens[2], drone, tokens[4]);
                        break;
                    }

                    case "display_orders": {
                        //display_orders, storeName
                        checkService.checkArgsCount(tokens, 2);
                        Store store = checkService.checkStoreNotExist(tokens[1]);
                        storeService.displayOrders(store);

                        break;
                    }
                    case "request_item": {
                        //request_item, storeName, orderId, itemName, quantity, unitPrice
                        checkService.checkArgsCount(tokens, 6);
                        Store store = checkService.checkStoreNotExist(tokens[1]);
                        Order order = checkService.checkOrderIdNotExist(tokens[2], store.getOrders());
                        checkService.checkOrderStatusIsCreated(order); //if order is checked out or delivered, we cannot add items to this order
                        String itemName = tokens[3];
                        Item item = checkService.checkItemIdNotExistInStore(itemName, store.getItems());
                        checkService.checkItemAlreadyOrdered(itemName, order.getLines());
                        int quantity = (int) checkService.checkNumber(tokens[4]);
                        double unitPrice = checkService.checkNumber(tokens[5]);
                        checkService.checkCustomerCredit(order.getCustomerId(), quantity, unitPrice);
                        double weight = quantity * item.getWeight();
                        checkService.checkDroneCapacity(store.getDrones().get(order.getDroneId()), weight);
                        storeService.requestItem(store, order, item, quantity, unitPrice);

                        break;
                    }
                    case "cancel_order": {
                        //cancel_order, storeName, orderId
                        checkService.checkArgsCount(tokens, 3);
                        Store store = checkService.checkStoreNotExist(tokens[1]);
                        Order order =  checkService.checkOrderIdNotExist(tokens[2], store.getOrders());
                        storeService.cancelOrder(store, order);
                      
                        break;
                    }
                    case "transfer_order": {
                        //transfer_order, storeName,orderId,droneId
                        checkService.checkArgsCount(tokens, 4);
                        Store store = checkService.checkStoreNotExist(tokens[1]);
                        Order order = checkService.checkOrderIdNotExist(tokens[2], store.getOrders());
                        Drone drone = checkService.checkDroneIdNotExist(tokens[3], store.getDrones());
                        //check if the newDrone can take the order
                        checkService.checkDroneNotInRepair(drone);
                        checkService.checkDroneCapacity(drone, order.getTotalWeight());
                        checkService.checkDroneCapacityWhenTransferOrder(drone, order.getTotalWeight());
                        storeService.transferOrder(store, order, drone);

                        break;
                    }
                    case "display_efficiency": {
                        //display_efficiency
                        checkService.checkArgsCount(tokens, 1);
                        storeService.displayEfficiency();

                        break;
                    }
                    case "checkout_order":{
                        //checkout_order, storeName, orderId
                        checkService.checkArgsCount(tokens,3);
                        Store store = checkService.checkStoreNotExist(tokens[1]);
                        Order order = checkService.checkOrderIdNotExist(tokens[2], store.getOrders());
                        checkService.checkOrderStatusIsCreated(order);
                        checkService.checkOrderAlreadyCheckedOut(order);
                        checkService.orderHasItems(order);
                        storeService.checkOutOrder(store, order);
                      
                        break;
                    }
                    case "deliver_orders":{
                        //deliver_order, storeName, droneId
                        checkService.checkArgsCount(tokens,3);
                        Store store = checkService.checkStoreNotExist(tokens[1]);
                        Drone drone = checkService.checkDroneIdNotExist(tokens[2],store.getDrones());
                        //check if the drone can deliver all orders
                        //check if there is order in drone
                        int numOfOrder = checkService.checkDroneHasOrder(drone);
                        checkService.checkDroneTripLeft(drone,numOfOrder);
                        //check if all orders assigned to this drone has been checked out
                        checkService.checkAllOrderCheckedOut(store, drone);
                        //check if the drone has a pilot
                        checkService. checkDroneHasAssignedPilot(drone);
                        checkService.checkDroneNotInRepair(drone);
                        storeService.deliverOrders(store, drone);
                      
                        break;
                    }
                    case "distribute_coupon":{
                        //distribute_coupon, frequency_for_high_rating_customer(days), frequency_for_low_rating_customer(days)
                        checkService.checkArgsCount(tokens,3);
                        int highFrequency = (int) checkService.checkNumber(tokens[1]);
                        int lowFrequency= (int) checkService.checkNumber(tokens[2]);
                        //check the input frequency is greater than zero and high freq > low freq
                        checkService.checkCustomerToDistributeCoupons();
                        checkService.checkFrequencyValid(highFrequency, lowFrequency);
                        customerService.distributeCoupon(highFrequency, lowFrequency);
                        logger.info(OtherMessageType.CHANGE_COMPLETED.getMessage());
                      
                        break;
                    }
                    case "stop_distribute_coupon":{
                        //stop_distribute_coupon
                        checkService.checkArgsCount(tokens,1);

                        schedulerService.cancelFutureScheduler();
                        logger.info(OtherMessageType.CHANGE_COMPLETED.getMessage());

                        break;
                    }
                    case "display_coupon" : {
                        //display_coupon, customerId
                        checkService.checkArgsCount(tokens, 2);
                        Customer customer = checkService.checkCustomerNotExists(tokens[1]);
                        customerService.displayCoupon(customer);

                        break;

                    }
                    case "help": {
                        displayHelp();

                        break;
                    }
                    case "stop": {
                        logger.info("stop acknowledged");
                        break label;
                    }
                    default: {
                        break;
                    }
                }
            } catch(DeliveryServiceException e){
                logger.info(e.getMessage());
            } catch(Exception e){
                logger.error("", e);
                logger.info("\n");
            }
        }

        logger.info("simulation terminated");
        commandLineInput.close();
    }

    public void trimTokens(String[] tokens) {
        for(int i = 0; i < tokens.length; i++){
            tokens[i] = tokens[i].trim();
        }
    }
          
    public void displayHelp () {
        logger.info("This is the instruction of how to test this program.\n\n" +
                    "To make_store, type:\nmake_store, storeName, initialRevenue, locationX, locationY\n(e.g. make_store,kroger,33000, 15, 30)\n\n" +
                    "To display_stores, type:\ndisplay_stores\n(e.g. display_stores)\n\n" +
                    "To sell_item, type:\nsell_item, storeName, itemName, weight\n(e.g. sell_item,kroger,pot_roast,5)\n\n" +
                    "To display_items, type:\ndisplay_items, storeName\n(e.g. display_items, kroger)\n\n" +
                    "To make_pilot, type:\nmake_pilot, pilotId, firstName, lastName, phoneNumber, taxId, licenseId, numberOfDeliveries\n" +
                    "(e.g. make_pilot,ffig8,Finneas,Fig,888-888-8888,890-12-3456,panam_10,33)\n\n" +
                    "To display_pilots, type:\ndisplay_pilots\n(e.g. display_pilots)\n\n" +
                    "To make_drone, type:\nmake_drone, storeName, droneId, weightCapacity, tripsLeft(fuel), droneSpeed\n(e.g. make_drone,kroger,1,40,1,6)\n\n" +
                    "To display_drones, type:\ndisplay_drones, storeName\n(e.g. display_drones,kroger)\n\n" +
                    "To fly_drone, type:\nfly_drone,storeName,droneID,pilotID\n(e.g. fly_drone,kroger,1,ffig8)\n\n" +
                    "To make_customer, type:\nmake_customer,customerID,firstName,lastName,phoneNumber,customerRating,credit, locationX, locationY\n" +
                    "(e.g. make_customer,aapple2,Alana,Apple,222-222-2222,4,100, 15, 39)\n\n" +
                    "To display_customers, type:\ndisplay_customers\n(e.g. display_customers)\n\n" +
                    "To create_bird, type:\ncreate_bird, birdId, attackPossibility(choose an int from 0 to 100)\n(e.g. create_bird, bird123, 75)\n\n" +
                    "To display_birds, type:\ndisplay_birds\n(e.g. display_birds)\n\n" +
                    "To start_order, type:\nstart_order, storeName, orderID, droneID, customerID\n" +
                    "(e.g. start_order,kroger,purchaseA,1,aapple2)\n\n" +
                    "To display_orders, type:\ndisplay_orders, storeName\n" +
                    "(e.g. display_orders, qfc)\n\n" +
                    "To request_item, type:\nrequest_item, storeName, orderID, itemName, quantity, unit_price\n" +
                    "(e.g. request_item,kroger,purchaseA,pot_roast,3,10)\n\n"+
                    "To checkout_order, type:\ncheckout_order, storeName, orderID\n"+
                    "(e.g. checkout_order,kroger,purchaseA)\n\n"+
                    "To deliver_orders, type:\ndeliver_orders, storeName, droneID"+
                    "(e.g. deliver_orders,kroger,ffig8)\n\n"+
                    "To distribute_coupon, type:\ndistribute_coupon, frequency_for_high_rating_customer(days), frequency_for_low_rating_customer(days)"+
                    "(e.g. distribute_coupon,5, 7)\n\n" +
                    "To stop_distribute_coupon, type:\nstop_distribute_coupon"+
                    "(e.g. stop_distribute_coupon)\n\n" +
                    "To display_coupon, type:\ndisplay_coupon, customerId"+
                    "(e.g. display_coupon, customer-a)\n\n" +
                    "To cancel_order, type:\ncancel_order, storeName, orderId\n" +
                    "(e.g. cancel_order,qfc,purchaseB)\n\n" +
                    "To transfer_order, type:\ntransfer_order, storeName,orderId,droneId\n" +
                    "(e.g. transfer_order,qfc,purchaseB,3)\n\n" +
                    "To display_efficiency, type:\ndisplay_efficiency\n\n");
    }
}



package com.group11.assignment5.service;

import com.group11.assignment5.exception.DeliveryServiceException;
import com.group11.assignment5.exception.ExceptionMessageType;
import com.group11.assignment5.message.OtherMessageType;
import com.group11.assignment5.model.*;
import com.group11.assignment5.repository.BirdRepository;
import com.group11.assignment5.repository.CustomerRepository;
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
public class BirdServiceImpl implements BirdService{
    private BirdRepository birdRepository;
    private StoreRepository storeRepository;
    private CustomerRepository customerRepository;


    private static Logger logger = LoggerFactory.getLogger(BirdServiceImpl.class);

    @Override
    public int getRandomIndex(int size) {
        //generate an integer from [0, size-1]
        return new Random().nextInt(size);
    }


    @Override
    public void reassignBirdCoordinate(Bird bird, Store currentStore, Customer currentCustomer) throws DeliveryServiceException {
        //first remove bird from old store or customer if there is any
        if(currentStore != null){
            currentStore.deleteBirdAtStore();
            storeRepository.save(currentStore);
        }
        if(currentCustomer != null){
            currentCustomer.deleteBirdAtCustomer();
            customerRepository.save(currentCustomer);
        }

        //get all stores and customers form db without any birds
        List<Store> noBirdsStores = new ArrayList<>();
        storeRepository.findAll().forEach(store ->{
            if(!store.getHasBird()){
                noBirdsStores.add(store);
            }
        });

        List<Customer> noBirdsCustomers = new ArrayList<>();
        customerRepository.findAll().forEach(customer -> {
            if(!customer.getHasBird()){
                noBirdsCustomers.add(customer);
            }
        });

        //get a random integer from [0, size-1]
        if(noBirdsStores.size() + noBirdsCustomers.size() <= 0) {
            throw new DeliveryServiceException(ExceptionMessageType.NO_STORE_OR_CUSTOMER_EXIST);
        }
        int selectedIndex = getRandomIndex(noBirdsStores.size() + noBirdsCustomers.size());

        //select a store or customer based on the selectedIndex
        Store selectedStore = null;
        Customer selectedCustomer = null;
        if (selectedIndex < noBirdsStores.size()) {
            selectedStore = noBirdsStores.get(selectedIndex);
        } else {
            selectedCustomer = noBirdsCustomers.get(selectedIndex - noBirdsStores.size());
        }


        if (selectedStore != null) {
            //we choose a store without any bird
            selectedStore.markBirdAtStore(bird);
            selectedStore.setWillAttack(bird.doesBirdAttack());
            bird.setCoordinate(selectedStore.getCoordinate());
            storeRepository.save(selectedStore);
            logger.info("bird {} is assigned to store {}", bird.getBirdId(), selectedStore.getStoreName());
        } else if (selectedCustomer != null) {
            //we choose a customer without any bird
            selectedCustomer.markBirdAtCustomer(bird);
            selectedCustomer.setWillAttack(bird.doesBirdAttack());
            bird.setCoordinate(selectedCustomer.getCoordinate());
            customerRepository.save(selectedCustomer);
            logger.info("bird {} is assigned to customer {}", bird.getBirdId(), selectedCustomer.getCustomerId());
        }

        birdRepository.save(bird);
        logger.info(OtherMessageType.CHANGE_COMPLETED.getMessage());


    }
    @Override
    public void makeBird(String birdId, int probAttack) throws DeliveryServiceException {
        Bird newBird = new Bird(birdId, probAttack);
        reassignBirdCoordinate(newBird, null, null);

    }
    @Override
    public void displayBirds() {
        List<Bird> birds = birdRepository.findAll();
        for(Bird bird : birds) {
            logger.info("birdId:{},attack_possibility:{},coordinate:[{},{}]", bird.getBirdId(), bird.getProbAttack(), bird.getCoordinate().getX(), bird.getCoordinate().getY());
        }
        logger.info(OtherMessageType.DISPLAY_COMPLETED.getMessage());
    }
}

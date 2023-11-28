package com.group11.assignment5.service;

import com.group11.assignment5.message.OtherMessageType;
import com.group11.assignment5.model.Pilot;
import com.group11.assignment5.repository.PilotRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Service
public class PilotServiceImpl implements PilotService{
    private PilotRepository pilotRepository;
    private static Logger logger = LoggerFactory.getLogger(PilotServiceImpl.class);
    @Override
    public void makePilot(String pilotId, String firstName, String lastName, String phoneNumber, String taxId, String licenseId, int numberOfDeliveries){
        pilotRepository.save(new Pilot(pilotId, firstName, lastName, phoneNumber, taxId, licenseId, numberOfDeliveries));
        logger.info(OtherMessageType.CHANGE_COMPLETED.getMessage());
    }
    @Override
    public void displayPilots() {
        pilotRepository.findAll().forEach((Pilot pilot) -> {
            logger.info("name:{},phone:{},taxID:{},licenseID:{},experience:{}", pilot.getFullName(), pilot.getPhoneNumber(), pilot.getTaxId(),
                    pilot.getLicenseId(), pilot.getNumberOfDeliveries());
        });
        logger.info(OtherMessageType.DISPLAY_COMPLETED.getMessage());
    }
}

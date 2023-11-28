package com.group11.assignment5.service;


public interface PilotService {
    void makePilot(String pilotId, String firstName, String lastName, String phoneNumber, String taxId, String licenseId, int numberOfDeliveries);
    void displayPilots();
}

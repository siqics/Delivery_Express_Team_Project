package com.group11.assignment5.exception;

public class DeliveryServiceException extends Exception{
    public DeliveryServiceException(ExceptionMessageType messageType){
        super(messageType.getMessage());
    }
}

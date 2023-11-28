package com.group11.assignment5.exception;


public enum ExceptionMessageType {
    //Exception Message:
    STORE_ID_EXISTS("ERROR:store_identifier_already_exists"),
    ITEM_ID_EXISTS("ERROR:item_identifier_already_exists"),
    PILOT_ID_EXISTS("ERROR:pilot_identifier_already_exists"),
    PILOT_LICENSE_EXISTS("ERROR:pilot_license_already_exists"),
    DRONE_ID_EXISTS("ERROR:drone_identifier_already_exists"),
    CUSTOMER_ID_EXISTS("ERROR:customer_identifier_already_exists"),
    ORDER_ID_EXISTS("ERROR:order_identifier_already_exists"),
    BIRD_ID_EXISTS("ERROR:bird_identifier_already_exists"),
    STORE_ID_NOT_EXIST("ERROR:store_identifier_does_not_exist"),
    PILOT_ID_NOT_EXIST("ERROR:pilot_identifier_does_not_exist"),
    DRONE_ID_NOT_EXIST("ERROR:drone_identifier_does_not_exist"),
    CUSTOMER_ID_NOT_EXIST("ERROR:customer_identifier_does_not_exist"),
    ORDER_ID_NOT_EXIST("ERROR:order_identifier_does_not_exist"),
    ITEM_ID_NOT_EXIST("ERROR:item_identifier_does_not_exist"),
    ITEM_ALREADY_ORDERED("ERROR:item_already_ordered"),
    CUSTOMER_CANT_AFFORD("ERROR:customer_cant_afford_new_item"),
    DRONE_CANT_CARRY("ERROR:drone_cant_carry_new_item"),
    DRONE_NEEDS_FUEL("ERROR:drone_needs_fuel"),
    DRONE_NEEDS_PILOT("ERROR:drone_needs_pilot"),
    DRONE_NOT_ENOUGH_CAPACITY("ERROR:new_drone_does_not_have_enough_capacity"),
    DRONE_ORDER_NOT_EXIST("ERROR:drone_does_not_have_order_to_delivery"),
    //more exception messages to improve robustness
    INPUTS_SHOULD_BE_NUMBERS("ERROR: some_inputs_in_the_command_should_be_numbers"),
    STORE_ADDRESS_ALREADY_EXISTS("ERROR: store_address_already_exists"),
    ORDER_NEEDS_CHECKOUT("ERROR: all_orders_on_this_drone_should_be_checked_out"),
    INVALID_FREQUENCY("ERROR: deliver_frequency_must_be_greater_than_zero_and_high_rating_customer_frequency_interval_must_be_less_than_the_low_rating_customer_frequency_interval"),
    INCORRECT_ARGS_COUNT("ERROR: incorrect_args_count"),
    INCORRECT_PROB_RANGE("ERROR: probability_should_between_0_to_100"),
    NO_STORE_OR_CUSTOMER_EXIST("ERROR: no_available_store_or_customer_to_assign_bird"),
    COMMAND_NOT_FOUND("ERROR: command_not_found_please_enter_help_to_find_all_allowed_commands"),
    ORDER_ALREADY_CHECKED_OUT("ERROR: order_already_checked out"),
    NO_CUSTOMER_TO_DISTRIBUTE_COUPONS("ERROR: no_customer_to_distribute_coupons"),
    DRONE_IN_REPAIR("ERROR: drone_is_not_available_since_it_is_in_repair"),

    ORDER_STATUS_MUST_BE_STATUS_TO_ADD_ITEM("ERROR: order_status_must_be_created_to_allow_adding_items"),

    ORDER_MUST_HAS_ITEMS_ADDED_BEFORE_CHECKOUT("ERROR: order_must_has_added_items_before_checkout");
  
    private String message;
    ExceptionMessageType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}


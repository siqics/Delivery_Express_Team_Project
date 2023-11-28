package com.group11.assignment5.message;

public enum OtherMessageType {
    CHANGE_COMPLETED("OK:change_completed"),
    DISPLAY_COMPLETED("OK:display_completed"),
    NEW_DRONE_IS_CURRENT_NO_CHANGE("OK:new_drone_is_current_drone_no_change");

    private String message;
    OtherMessageType(String message) {
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }
}

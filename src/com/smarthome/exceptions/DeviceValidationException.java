package com.smarthome.exceptions;

/**
 * Custom exception thrown when appliance parameters violate operational boundaries
 * (e.g. Temperature outside 16-30°C, Brightness outside 0-100%, Speed outside 0-5).
 */
public class DeviceValidationException extends RuntimeException {
    public DeviceValidationException(String message) {
        super(message);
    }
}

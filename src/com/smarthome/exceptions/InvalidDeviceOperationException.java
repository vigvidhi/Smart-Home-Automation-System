package com.smarthome.exceptions;

/**
 * Custom exception thrown when attempting an invalid physical or logical state transition
 * (e.g. Opening a deadbolted door, adjusting temperature while AC is OFF, operating camera in shutdown).
 */
public class InvalidDeviceOperationException extends RuntimeException {
    public InvalidDeviceOperationException(String message) {
        super(message);
    }
}

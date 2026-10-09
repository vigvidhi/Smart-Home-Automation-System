package com.smarthome.model;

import com.smarthome.exceptions.InvalidDeviceOperationException;

/**
 * Concrete class representing a Smart Electronic Door / Deadbolt.
 * Enhanced in Phase 5:
 * - Security lockout tracking (after 3 consecutive failed PIN attempts)
 * - Defensive error handling with custom exceptions
 * - Physical barrier validation
 */
public class Door extends Device {
    private boolean isLocked;
    private boolean isOpen;
    private String securityPin;
    private int failedAttempts;
    private boolean isSecurityLockoutActive;

    public Door(String id, String name, String roomName) {
        this(id, name, roomName, "1234");
    }

    public Door(String id, String name, String roomName, String initialPin) {
        super(id, name, roomName);
        this.isLocked = true;
        this.isOpen = false;
        this.securityPin = (initialPin != null && !initialPin.trim().isEmpty()) ? initialPin : "1234";
        this.isPoweredOn = true;
        this.failedAttempts = 0;
        this.isSecurityLockoutActive = false;
    }

    /**
     * Unlocks the door if the provided PIN matches and lockout is not active.
     */
    public boolean unlock(String enteredPin) {
        if (isSecurityLockoutActive) {
            System.err.printf("[SECURITY LOCKOUT] Door '%s': Lockout active due to repeated unauthorized attempts!%n", getName());
            notifyListeners("SECURITY LOCKOUT: Door keypad locked due to repeated invalid attempts");
            return false;
        }

        if (!this.isLocked) {
            System.out.printf("[INFO] Door '%s' is already unlocked.%n", getName());
            return true;
        }

        if (this.securityPin.equals(enteredPin)) {
            this.isLocked = false;
            this.failedAttempts = 0;
            notifyListeners("Unlocked with valid PIN");
            return true;
        } else {
            failedAttempts++;
            System.err.printf("[SECURITY ALERT] Door '%s': Access Denied! Invalid PIN attempt (%d/3)%n", getName(), failedAttempts);
            notifyListeners(String.format("SECURITY ALERT: Invalid PIN Attempt (%d/3)", failedAttempts));

            if (failedAttempts >= 3) {
                isSecurityLockoutActive = true;
                notifyListeners("SECURITY CRITICAL: Keypad LOCKOUT triggered (3 failed attempts)");
            }
            return false;
        }
    }

    /**
     * Locks the door. Automatically closes the physical door if currently ajar.
     */
    public void lock() {
        if (this.isOpen) {
            System.out.printf("[ACTION] Closing door '%s' before engaging deadbolt.%n", getName());
            this.isOpen = false;
        }
        this.isLocked = true;
        notifyListeners("Deadbolt engaged (LOCKED)");
    }

    /**
     * Opens the door. Requires the door to be unlocked first.
     */
    public boolean openDoor() {
        if (this.isLocked) {
            System.err.printf("[OPERATION FAILED] Door '%s' cannot be opened because it is LOCKED.%n", getName());
            return false;
        }
        this.isOpen = true;
        notifyListeners("Door OPENED");
        return true;
    }

    /**
     * Closes the door without necessarily locking it.
     */
    public void closeDoor() {
        this.isOpen = false;
        notifyListeners("Door CLOSED");
    }

    public boolean changePin(String oldPin, String newPin) {
        if (this.securityPin.equals(oldPin)) {
            if (newPin != null && newPin.trim().length() >= 4) {
                this.securityPin = newPin.trim();
                this.failedAttempts = 0;
                this.isSecurityLockoutActive = false;
                notifyListeners("Security PIN changed successfully");
                return true;
            } else {
                System.err.println("[VALIDATION ERROR] New PIN must be at least 4 digits.");
                return false;
            }
        }
        System.err.println("[SECURITY ERROR] Incorrect old PIN. PIN change rejected.");
        return false;
    }

    public void clearLockout() {
        this.failedAttempts = 0;
        this.isSecurityLockoutActive = false;
        notifyListeners("Security lockout cleared by resident");
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isSecurityLockoutActive() {
        return isSecurityLockoutActive;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    @Override
    public String getStatusSummary() {
        if (isSecurityLockoutActive) {
            return "[LOCKED - SECURITY LOCKOUT]";
        }
        String lockState = isLocked ? "LOCKED" : "UNLOCKED";
        String physicalState = isOpen ? "OPEN" : "CLOSED";
        return String.format("[%s, %s]", lockState, physicalState);
    }

    @Override
    public void reset() {
        this.isLocked = true;
        this.isOpen = false;
        this.securityPin = "1234";
        this.failedAttempts = 0;
        this.isSecurityLockoutActive = false;
        this.isPoweredOn = true;
        notifyListeners("Door reset to default (LOCKED, CLOSED, PIN 1234)");
    }
}

package com.smarthome.model;

import com.smarthome.interfaces.Adjustable;

/**
 * Concrete class representing a smart fan.
 * Demonstrates:
 * - Inheritance (extends Device)
 * - Interface Implementation (implements Adjustable)
 * - Encapsulation (speed validation 0-5)
 * - Polymorphism (overrides getStatusSummary and reset)
 */
public class Fan extends Device implements Adjustable {
    private int speed; // Range: 0 (Off) to 5 (Max)
    private boolean isOscillating;

    public Fan(String id, String name, String roomName) {
        super(id, name, roomName);
        this.speed = 0;
        this.isOscillating = false;
    }

    @Override
    public boolean setSpeed(int speed) {
        if (speed < 0 || speed > 5) {
            System.err.printf("[VALIDATION ERROR] Fan '%s': Speed must be between 0 and 5. Received: %d%n",
                    getName(), speed);
            return false;
        }
        this.speed = speed;
        this.isPoweredOn = (speed > 0);
        notifyListeners("Speed set to " + speed + "/5");
        return true;
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    public boolean isOscillating() {
        return isOscillating;
    }

    public void setOscillating(boolean oscillating) {
        this.isOscillating = oscillating;
        notifyListeners("Oscillation set to " + (oscillating ? "ON" : "OFF"));
    }

    public void toggleOscillation() {
        setOscillating(!this.isOscillating);
    }

    @Override
    public void turnOn() {
        super.turnOn();
        if (this.speed == 0) {
            this.speed = 3; // Default medium speed
        }
        notifyListeners("Fan powered ON (Default Speed: " + speed + ")");
    }

    @Override
    public void turnOff() {
        super.turnOff();
        this.speed = 0;
        this.isOscillating = false;
        notifyListeners("Fan powered OFF");
    }

    @Override
    public String getStatusSummary() {
        if (!isPoweredOn || speed == 0) {
            return "OFF";
        }
        return String.format("ON (Speed: %d/5, Oscillating: %s)", speed, isOscillating ? "YES" : "NO");
    }

    @Override
    public void reset() {
        this.isPoweredOn = false;
        this.speed = 0;
        this.isOscillating = false;
        notifyListeners("Reset to factory defaults");
    }
}

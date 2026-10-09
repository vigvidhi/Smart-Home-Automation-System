package com.smarthome.model;

import java.util.Arrays;
import java.util.List;

/**
 * Concrete class representing an Air Conditioner (AC) / Smart Thermostat.
 * Demonstrates:
 * - Inheritance (extends Device)
 * - Encapsulation (validated temperature range 16-30°C and modes)
 * - Polymorphism (custom status reporting)
 */
public class AirConditioner extends Device {
    public static final int MIN_TEMP = 16;
    public static final int MAX_TEMP = 30;
    public static final List<String> VALID_MODES = Arrays.asList("COOL", "HEAT", "FAN", "ECO");

    private int targetTemperature; // Range: 16°C to 30°C
    private int currentRoomTemperature; // Simulated ambient reading
    private String mode; // COOL, HEAT, FAN, ECO

    public AirConditioner(String id, String name, String roomName) {
        super(id, name, roomName);
        this.targetTemperature = 24; // Standard comfortable default
        this.currentRoomTemperature = 27; // Typical initial ambient temperature
        this.mode = "COOL";
    }

    public boolean setTargetTemperature(int temperature) {
        if (!isPoweredOn) {
            System.err.printf("[OPERATION WARNING] AC '%s' is OFF. Turn ON before adjusting temperature.%n", getName());
            return false;
        }
        if (temperature < MIN_TEMP || temperature > MAX_TEMP) {
            System.err.printf("[VALIDATION ERROR] AC '%s': Temperature must be between %d°C and %d°C. Received: %d°C%n",
                    getName(), MIN_TEMP, MAX_TEMP, temperature);
            return false;
        }
        this.targetTemperature = temperature;
        notifyListeners("Target temperature set to " + temperature + "°C");
        return true;
    }

    public int getTargetTemperature() {
        return targetTemperature;
    }

    public int getCurrentRoomTemperature() {
        return currentRoomTemperature;
    }

    public void setCurrentRoomTemperature(int temp) {
        this.currentRoomTemperature = temp;
        notifyListeners("Simulated ambient room temperature updated to " + temp + "°C");
    }

    public boolean setMode(String mode) {
        if (mode == null || !VALID_MODES.contains(mode.toUpperCase())) {
            System.err.printf("[VALIDATION ERROR] AC '%s': Invalid mode '%s'. Allowed: %s%n",
                    getName(), mode, VALID_MODES);
            return false;
        }
        this.mode = mode.toUpperCase();
        notifyListeners("Mode changed to " + this.mode);
        return true;
    }

    public String getMode() {
        return mode;
    }

    public boolean increaseTemperature() {
        return setTargetTemperature(this.targetTemperature + 1);
    }

    public boolean decreaseTemperature() {
        return setTargetTemperature(this.targetTemperature - 1);
    }

    @Override
    public void turnOn() {
        super.turnOn();
        notifyListeners("AC powered ON (Mode: " + mode + ", Setpoint: " + targetTemperature + "°C)");
    }

    @Override
    public void turnOff() {
        super.turnOff();
        notifyListeners("AC powered OFF");
    }

    @Override
    public String getStatusSummary() {
        if (!isPoweredOn) {
            return "OFF";
        }
        return String.format("ON (Target: %d°C, Room: %d°C, Mode: %s)",
                targetTemperature, currentRoomTemperature, mode);
    }

    @Override
    public void reset() {
        this.isPoweredOn = false;
        this.targetTemperature = 24;
        this.currentRoomTemperature = 27;
        this.mode = "COOL";
        notifyListeners("Reset to factory defaults");
    }
}

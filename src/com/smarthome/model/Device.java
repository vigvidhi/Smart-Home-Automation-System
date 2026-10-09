package com.smarthome.model;

import com.smarthome.interfaces.DeviceStateListener;
import com.smarthome.interfaces.Switchable;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all smart appliances in the simulator.
 * Demonstrates:
 * - Abstraction (cannot be instantiated directly, defines abstract methods)
 * - Encapsulation (private fields with controlled getters/setters)
 * - Interfaces (implements Switchable)
 */
public abstract class Device implements Switchable {
    private final String id;
    private final String name;
    private final String roomName;
    protected boolean isPoweredOn;
    
    // Observer pattern listeners to connect with GUI in Phase 3
    private final List<DeviceStateListener> listeners = new ArrayList<>();

    /**
     * Parameterized Constructor
     * @param id Unique identifier (e.g., "LGT-101")
     * @param name Human-readable device name (e.g., "Living Room Chandelier")
     * @param roomName Room where device is placed
     */
    public Device(String id, String name, String roomName) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Device ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Device name cannot be null or empty.");
        }
        this.id = id.trim();
        this.name = name.trim();
        this.roomName = (roomName != null && !roomName.trim().isEmpty()) ? roomName.trim() : "Unassigned";
        this.isPoweredOn = false;
    }

    // --- Switchable Interface Implementations ---

    @Override
    public void turnOn() {
        if (!this.isPoweredOn) {
            this.isPoweredOn = true;
            notifyListeners("Powered ON");
        }
    }

    @Override
    public void turnOff() {
        if (this.isPoweredOn) {
            this.isPoweredOn = false;
            notifyListeners("Powered OFF");
        }
    }

    @Override
    public boolean isOn() {
        return this.isPoweredOn;
    }

    // --- Abstract Methods (Must be implemented by concrete subclasses) ---

    /**
     * Returns a detailed textual summary of the device's current state.
     */
    public abstract String getStatusSummary();

    /**
     * Resets the device to its default factory state.
     */
    public abstract void reset();

    // --- Observer Listener Registration ---

    public void addStateListener(DeviceStateListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeStateListener(DeviceStateListener listener) {
        listeners.remove(listener);
    }

    protected void notifyListeners(String eventDescription) {
        for (DeviceStateListener listener : listeners) {
            listener.onDeviceStateChanged(this, eventDescription);
        }
    }

    // --- Encapsulated Getters ---

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRoomName() {
        return roomName;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) -> %s", id, name, roomName, getStatusSummary());
    }
}

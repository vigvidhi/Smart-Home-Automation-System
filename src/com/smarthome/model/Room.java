package com.smarthome.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an individual room containing multiple smart appliances.
 * Demonstrates:
 * - Aggregation / Composition (Room contains a collection of Device objects)
 * - Polymorphism (iterating over heterogenous Device list and invoking common methods)
 * - Encapsulation (protects internal device list)
 */
public class Room {
    private final String roomName;
    private final List<Device> devices;

    public Room(String roomName) {
        if (roomName == null || roomName.trim().isEmpty()) {
            throw new IllegalArgumentException("Room name cannot be empty.");
        }
        this.roomName = roomName.trim();
        this.devices = new ArrayList<>();
    }

    public void addDevice(Device device) {
        if (device == null) {
            System.err.println("[ERROR] Cannot add null device to room " + roomName);
            return;
        }
        // Avoid duplicate ID
        for (Device existing : devices) {
            if (existing.getId().equalsIgnoreCase(device.getId())) {
                System.err.printf("[ERROR] Device with ID '%s' already exists in %s.%n", device.getId(), roomName);
                return;
            }
        }
        devices.add(device);
        System.out.printf("[ROOM CONFIG] Added '%s' [%s] to %s.%n", device.getName(), device.getId(), roomName);
    }

    public boolean removeDevice(String deviceId) {
        return devices.removeIf(d -> d.getId().equalsIgnoreCase(deviceId));
    }

    public Device getDeviceById(String deviceId) {
        for (Device d : devices) {
            if (d.getId().equalsIgnoreCase(deviceId)) {
                return d;
            }
        }
        return null;
    }

    /**
     * Demonstrates Polymorphism: Turns off all switchable devices in this room.
     */
    public void turnOffAllDevices() {
        System.out.printf("[ACTION] Turning OFF all devices in %s...%n", roomName);
        for (Device d : devices) {
            d.turnOff();
        }
    }

    /**
     * Demonstrates Polymorphism: Turns on all switchable devices in this room.
     */
    public void turnOnAllDevices() {
        System.out.printf("[ACTION] Turning ON all devices in %s...%n", roomName);
        for (Device d : devices) {
            d.turnOn();
        }
    }

    public int getActiveDeviceCount() {
        int count = 0;
        for (Device d : devices) {
            if (d.isOn()) {
                count++;
            }
        }
        return count;
    }

    public int getTotalDeviceCount() {
        return devices.size();
    }

    public String getRoomName() {
        return roomName;
    }

    /**
     * Returns an unmodifiable list of devices to protect internal state.
     */
    public List<Device> getDevices() {
        return Collections.unmodifiableList(devices);
    }

    public void printRoomStatus() {
        System.out.printf("--- Room: %s (Active: %d/%d) ---%n", roomName, getActiveDeviceCount(), getTotalDeviceCount());
        for (Device d : devices) {
            System.out.printf("  * %s%n", d.toString());
        }
    }
}

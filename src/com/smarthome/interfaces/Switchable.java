package com.smarthome.interfaces;

/**
 * Interface representing devices that can be turned ON and OFF.
 * Demonstrates Abstraction in OOP.
 */
public interface Switchable {
    void turnOn();
    void turnOff();
    boolean isOn();
}

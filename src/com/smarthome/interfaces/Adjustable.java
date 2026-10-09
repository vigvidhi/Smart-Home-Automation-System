package com.smarthome.interfaces;

/**
 * Interface representing devices with adjustable operational speeds (e.g., Fans).
 */
public interface Adjustable {
    boolean setSpeed(int speed);
    int getSpeed();
}

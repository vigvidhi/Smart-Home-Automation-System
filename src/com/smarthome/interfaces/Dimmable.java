package com.smarthome.interfaces;

/**
 * Interface representing devices with adjustable illumination (0% to 100%).
 */
public interface Dimmable {
    boolean setBrightness(int level);
    int getBrightness();
}

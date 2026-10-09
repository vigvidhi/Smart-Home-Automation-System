package com.smarthome.model;

import com.smarthome.interfaces.Dimmable;

/**
 * Concrete class representing a smart light appliance.
 * Demonstrates:
 * - Inheritance (extends Device)
 * - Interface Implementation (implements Dimmable)
 * - Encapsulation (validated brightness)
 * - Polymorphism & Method Overriding
 * - Method Overloading (turnOn())
 */
public class Light extends Device implements Dimmable {
    private int brightness; // Range: 0 to 100%
    private String colorTone; // e.g. "Warm White", "Cool White", "Daylight"

    public Light(String id, String name, String roomName) {
        super(id, name, roomName);
        this.brightness = 0;
        this.colorTone = "Warm White";
    }

    public Light(String id, String name, String roomName, String colorTone) {
        super(id, name, roomName);
        this.brightness = 0;
        this.colorTone = (colorTone != null) ? colorTone : "Warm White";
    }

    @Override
    public boolean setBrightness(int level) {
        if (level < 0 || level > 100) {
            System.err.printf("[VALIDATION ERROR] Light '%s': Brightness must be between 0 and 100. Received: %d%n",
                    getName(), level);
            return false;
        }
        this.brightness = level;
        this.isPoweredOn = (level > 0);
        notifyListeners("Brightness set to " + level + "%");
        return true;
    }

    @Override
    public int getBrightness() {
        return brightness;
    }

    public String getColorTone() {
        return colorTone;
    }

    public void setColorTone(String colorTone) {
        if (colorTone != null && !colorTone.trim().isEmpty()) {
            this.colorTone = colorTone;
            notifyListeners("Color tone set to " + colorTone);
        }
    }

    // Overridden turnOn(): Restores 100% brightness if previously 0
    @Override
    public void turnOn() {
        super.turnOn();
        if (this.brightness == 0) {
            this.brightness = 100;
        }
        notifyListeners("Light turned ON at " + brightness + "% brightness");
    }

    // Overloaded turnOn(int brightness) demonstrates compile-time polymorphism
    public void turnOn(int initialBrightness) {
        if (setBrightness(initialBrightness)) {
            this.isPoweredOn = (initialBrightness > 0);
            notifyListeners("Light turned ON with " + initialBrightness + "% brightness");
        }
    }

    @Override
    public void turnOff() {
        super.turnOff();
        this.brightness = 0;
        notifyListeners("Light turned OFF");
    }

    @Override
    public String getStatusSummary() {
        return isPoweredOn ? String.format("ON (Brightness: %d%%, Tone: %s)", brightness, colorTone) : "OFF";
    }

    @Override
    public void reset() {
        this.isPoweredOn = false;
        this.brightness = 0;
        this.colorTone = "Warm White";
        notifyListeners("Reset to factory defaults");
    }
}

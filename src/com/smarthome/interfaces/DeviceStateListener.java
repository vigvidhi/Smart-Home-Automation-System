package com.smarthome.interfaces;

import com.smarthome.model.Device;

/**
 * Listener interface for device state change events.
 * Provides a clean bridge to Phase 3 GUI (Java Swing / JavaFX)
 * using the Observer design pattern.
 */
public interface DeviceStateListener {
    void onDeviceStateChanged(Device device, String eventDescription);
}

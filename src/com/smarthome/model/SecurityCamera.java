package com.smarthome.model;

/**
 * Concrete class representing a Smart Security Camera / Motion Sensor.
 * Demonstrates:
 * - Inheritance (extends Device)
 * - State management (Recording, Motion simulation)
 * - Defensive checks (cannot record while powered off)
 */
public class SecurityCamera extends Device {
    private boolean isRecording;
    private boolean motionDetected;
    private String resolution;

    public SecurityCamera(String id, String name, String roomName) {
        super(id, name, roomName);
        this.isRecording = false;
        this.motionDetected = false;
        this.resolution = "1080p Full HD";
        this.isPoweredOn = true; // Cameras typically stay powered on in standby
    }

    public boolean startRecording() {
        if (!isPoweredOn) {
            System.err.printf("[OPERATION WARNING] Camera '%s' is OFF. Turn ON before starting recording.%n", getName());
            return false;
        }
        this.isRecording = true;
        notifyListeners("Recording STARTED (" + resolution + ")");
        return true;
    }

    public void stopRecording() {
        this.isRecording = false;
        notifyListeners("Recording STOPPED");
    }

    public void triggerMotion() {
        if (!isPoweredOn) {
            System.out.printf("[SIMULATION IGNORED] Camera '%s' is powered off; motion undetected.%n", getName());
            return;
        }
        this.motionDetected = true;
        this.isRecording = true; // Auto-trigger recording on motion
        System.out.printf("[SECURITY EVENT] Motion detected by '%s'! Auto-recording engaged.%n", getName());
        notifyListeners("SECURITY ALERT: Motion Detected! Auto-recording active");
    }

    public void clearMotion() {
        this.motionDetected = false;
        notifyListeners("Motion alert cleared");
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean isMotionDetected() {
        return motionDetected;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        if (resolution != null && !resolution.trim().isEmpty()) {
            this.resolution = resolution;
        }
    }

    @Override
    public void turnOff() {
        super.turnOff();
        this.isRecording = false;
        this.motionDetected = false;
        notifyListeners("Camera powered down to Standby/OFF");
    }

    @Override
    public String getStatusSummary() {
        if (!isPoweredOn) {
            return "OFF";
        }
        return String.format("ACTIVE (Recording: %s, Motion: %s, Res: %s)",
                isRecording ? "YES" : "NO",
                motionDetected ? "ALERT!" : "None",
                resolution);
    }

    @Override
    public void reset() {
        this.isPoweredOn = true;
        this.isRecording = false;
        this.motionDetected = false;
        this.resolution = "1080p Full HD";
        notifyListeners("Camera reset to defaults");
    }
}

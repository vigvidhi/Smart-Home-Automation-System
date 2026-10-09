package com.smarthome.automation;

import com.smarthome.model.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central automation engine orchestrating rules, simulated environmental conditions,
 * and scene presets.
 * Demonstrates:
 * - Aggregation & Composition of Rules and Scenes
 * - Event Dispatching & Observer Pattern
 * - Simulated Environmental State Management (Sensors)
 * - Conflict Resolution between Automated and Manual Controls
 */
public class AutomationManager {
    private boolean isMasterAutomationEnabled;

    // Simulated Environmental & Sensor State Variables
    private double ambientTemperature; // Default: 26°C
    private boolean isNightTime;       // Virtual Time of Day
    private boolean isMotionDetected;  // Simulated PIR sensor
    private boolean isAwayMode;        // Away state flag
    private boolean isSecurityMode;    // Armed security state

    private final List<AutomationRule> rules;
    private final SceneManager sceneManager;
    private final List<AutomationEventListener> eventListeners;

    public AutomationManager(SmartHome smartHome) {
        this.isMasterAutomationEnabled = true;
        this.ambientTemperature = 26.0;
        this.isNightTime = false;
        this.isMotionDetected = false;
        this.isAwayMode = false;
        this.isSecurityMode = false;

        this.rules = new ArrayList<>();
        this.sceneManager = new SceneManager();
        this.eventListeners = new ArrayList<>();

        initializeDefaultScenes(smartHome);
        initializeDefaultRules(smartHome);
    }

    private void initializeDefaultScenes(SmartHome smartHome) {
        // 1. Good Morning Scene
        Scene morning = new Scene("Good Morning", "Illuminates active zones, sets gentle climate, unlocks doors.");
        morning.addAction(() -> {
            for (Device d : smartHome.getAllDevices()) {
                if (d instanceof Light) {
                    ((Light) d).turnOn();
                    ((Light) d).setBrightness(65);
                } else if (d instanceof Fan) {
                    ((Fan) d).turnOn();
                    ((Fan) d).setSpeed(2);
                } else if (d instanceof AirConditioner) {
                    ((AirConditioner) d).turnOn();
                    ((AirConditioner) d).setTargetTemperature(25);
                    ((AirConditioner) d).setMode("ECO");
                } else if (d instanceof Door) {
                    ((Door) d).unlock("1234");
                }
            }
            notifyListeners("Scene Activated", "'Good Morning' applied: Lights 65%, Fans speed 2, AC 25°C Eco, Doors unlocked.");
        });
        sceneManager.registerScene(morning);

        // 2. Movie Mode Scene
        Scene movie = new Scene("Movie Mode", "Dims living room lights to 15%, turns others off, sets cool ambient climate.");
        movie.addAction(() -> {
            for (Device d : smartHome.getAllDevices()) {
                if (d.getRoomName().equalsIgnoreCase("Living Room")) {
                    if (d instanceof Light) {
                        ((Light) d).turnOn();
                        ((Light) d).setBrightness(15);
                    } else if (d instanceof AirConditioner) {
                        ((AirConditioner) d).turnOn();
                        ((AirConditioner) d).setTargetTemperature(21);
                        ((AirConditioner) d).setMode("COOL");
                    } else if (d instanceof Fan) {
                        ((Fan) d).setSpeed(1);
                    }
                } else {
                    if (d instanceof Light) {
                        d.turnOff();
                    }
                }
            }
            notifyListeners("Scene Activated", "'Movie Mode' applied: Living Room dimmed to 15%, AC 21°C Cool, other lights OFF.");
        });
        sceneManager.registerScene(movie);

        // 3. Sleep Mode Scene
        Scene sleep = new Scene("Sleep Mode", "Shuts off all general lighting, locks all doors, arms cameras.");
        sleep.addAction(() -> {
            for (Device d : smartHome.getAllDevices()) {
                if (d instanceof Light) {
                    if (d.getName().toLowerCase().contains("bedside") || d.getName().toLowerCase().contains("night")) {
                        ((Light) d).turnOn();
                        ((Light) d).setBrightness(10);
                    } else {
                        d.turnOff();
                    }
                } else if (d instanceof Fan && d.getRoomName().equalsIgnoreCase("Bedroom")) {
                    ((Fan) d).turnOn();
                    ((Fan) d).setSpeed(3);
                } else if (d instanceof AirConditioner && d.getRoomName().equalsIgnoreCase("Bedroom")) {
                    ((AirConditioner) d).turnOn();
                    ((AirConditioner) d).setTargetTemperature(22);
                } else if (d instanceof Door) {
                    ((Door) d).lock();
                } else if (d instanceof SecurityCamera) {
                    ((SecurityCamera) d).turnOn();
                    ((SecurityCamera) d).startRecording();
                }
            }
            notifyListeners("Scene Activated", "'Sleep Mode' applied: General lights OFF, Bedside 10%, Doors locked, Cameras active.");
        });
        sceneManager.registerScene(sleep);

        // 4. Away Mode Scene
        Scene away = new Scene("Away Mode", "Powers off all non-essential appliances, deadbolts doors, full surveillance.");
        away.addAction(() -> {
            for (Device d : smartHome.getAllDevices()) {
                if (d instanceof Door) {
                    ((Door) d).lock();
                } else if (d instanceof SecurityCamera) {
                    ((SecurityCamera) d).turnOn();
                    ((SecurityCamera) d).startRecording();
                } else {
                    d.turnOff();
                }
            }
            isAwayMode = true;
            notifyListeners("Scene Activated", "'Away Mode' applied: All appliances OFF, Doors locked, Surveillance armed.");
        });
        sceneManager.registerScene(away);
    }

    private void initializeDefaultRules(SmartHome smartHome) {
        // RULE 1: High Temperature AC Auto-Cool
        rules.add(new AutomationRule(
                "RUL-01",
                "High Temperature Cooling",
                "If simulated temperature > 28°C, automatically turn ON AC at 22°C.",
                () -> ambientTemperature > 28.0,
                () -> {
                    for (Device d : smartHome.getAllDevices()) {
                        if (d instanceof AirConditioner && !d.isOn()) {
                            d.turnOn();
                            ((AirConditioner) d).setTargetTemperature(22);
                            ((AirConditioner) d).setMode("COOL");
                        }
                    }
                    notifyListeners("Auto Rule Triggered", String.format("Ambient temperature (%.1f°C) > 28°C. Air Conditioners turned ON (22°C Cool).", ambientTemperature));
                }
        ));

        // RULE 2: Motion-Triggered Porch Lighting
        rules.add(new AutomationRule(
                "RUL-02",
                "Motion-Activated Lighting",
                "If motion is detected at the entrance, turn ON Porch Light (100%).",
                () -> isMotionDetected,
                () -> {
                    Device porchLight = smartHome.findDevice("LGT-EN-01");
                    if (porchLight instanceof Light) {
                        porchLight.turnOn();
                        ((Light) porchLight).setBrightness(100);
                        notifyListeners("Auto Rule Triggered", "Motion detected! Porch Light turned ON at 100% illumination.");
                    }
                }
        ));

        // RULE 3: Night Fall Comfort Lighting
        rules.add(new AutomationRule(
                "RUL-03",
                "Night-Time Soft Lighting",
                "If night falls, turn Bedside Reading Lamp ON at soft 20% brightness.",
                () -> isNightTime,
                () -> {
                    Device bedLight = smartHome.findDevice("LGT-BD-01");
                    if (bedLight instanceof Light && !bedLight.isOn()) {
                        bedLight.turnOn();
                        ((Light) bedLight).setBrightness(20);
                        notifyListeners("Auto Rule Triggered", "Night detected. Bedside Lamp turned ON at 20% soft glow.");
                    }
                }
        ));

        // RULE 4: Away Mode Energy Saver & Lock
        rules.add(new AutomationRule(
                "RUL-04",
                "Away Mode Lockdown",
                "If Away Mode enabled, ensure all entrance doors are deadbolted.",
                () -> isAwayMode,
                () -> {
                    for (Device d : smartHome.getAllDevices()) {
                        if (d instanceof Door && !((Door) d).isLocked()) {
                            ((Door) d).lock();
                        }
                    }
                    notifyListeners("Auto Rule Triggered", "Away Mode verified: All smart locks deadbolted.");
                }
        ));

        // RULE 5: Security Surveillance Mode
        rules.add(new AutomationRule(
                "RUL-05",
                "Security Perimeter Guard",
                "If security mode armed, engage continuous CCTV recording.",
                () -> isSecurityMode,
                () -> {
                    for (Device d : smartHome.getAllDevices()) {
                        if (d instanceof SecurityCamera && !((SecurityCamera) d).isRecording()) {
                            ((SecurityCamera) d).startRecording();
                        }
                    }
                    notifyListeners("Auto Rule Triggered", "Security Mode Armed: All cameras recording.");
                }
        ));
    }

    /**
     * Evaluates all enabled rules.
     */
    public void evaluateAllRules() {
        if (!isMasterAutomationEnabled) {
            return;
        }

        for (AutomationRule rule : rules) {
            rule.evaluateAndExecute();
        }
    }

    // --- Simulated Environmental Sensor Mutators (Triggers evaluation) ---

    public void setAmbientTemperature(double temp) {
        this.ambientTemperature = temp;
        evaluateAllRules();
    }

    public double getAmbientTemperature() {
        return ambientTemperature;
    }

    public void setNightTime(boolean night) {
        this.isNightTime = night;
        evaluateAllRules();
    }

    public boolean isNightTime() {
        return isNightTime;
    }

    public void setMotionDetected(boolean motion) {
        this.isMotionDetected = motion;
        evaluateAllRules();
    }

    public boolean isMotionDetected() {
        return isMotionDetected;
    }

    public void setAwayMode(boolean away) {
        this.isAwayMode = away;
        evaluateAllRules();
    }

    public boolean isAwayMode() {
        return isAwayMode;
    }

    public void setSecurityMode(boolean security) {
        this.isSecurityMode = security;
        evaluateAllRules();
    }

    public boolean isSecurityMode() {
        return isSecurityMode;
    }

    // --- Master Controls & Management ---

    public boolean isMasterAutomationEnabled() {
        return isMasterAutomationEnabled;
    }

    public void setMasterAutomationEnabled(boolean enabled) {
        this.isMasterAutomationEnabled = enabled;
        notifyListeners("Automation Engine", enabled ? "Master Automation Engine ENABLED." : "Master Automation Engine DISABLED.");
    }

    public void setRuleEnabled(String ruleId, boolean enabled) {
        for (AutomationRule r : rules) {
            if (r.getId().equalsIgnoreCase(ruleId)) {
                r.setEnabled(enabled);
                notifyListeners("Rule Updated", String.format("Rule '%s' [%s] set to %s.", r.getName(), r.getId(), enabled ? "ACTIVE" : "DISABLED"));
                break;
            }
        }
    }

    public List<AutomationRule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public void resetAutomationDefaults() {
        this.ambientTemperature = 26.0;
        this.isNightTime = false;
        this.isMotionDetected = false;
        this.isAwayMode = false;
        this.isSecurityMode = false;
        this.isMasterAutomationEnabled = true;

        for (AutomationRule r : rules) {
            r.setEnabled(true);
        }
        notifyListeners("Automation Reset", "Automation engine reset to factory baseline.");
    }

    // --- Observer Notification ---

    public void addEventListener(AutomationEventListener listener) {
        if (listener != null && !eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
    }

    private void notifyListeners(String title, String description) {
        for (AutomationEventListener l : eventListeners) {
            l.onAutomationTriggered(title, description);
        }
    }
}

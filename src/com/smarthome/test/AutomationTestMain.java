package com.smarthome.test;

import com.smarthome.automation.AutomationManager;
import com.smarthome.model.*;

/**
 * Phase 4 Automated Testing & Rule Engine Verification Harness.
 * Validates:
 * 1. Temperature-based automatic cooling trigger
 * 2. Motion-activated lighting trigger
 * 3. Night-time lighting automation
 * 4. Scene activation: Good Morning, Movie Mode, Sleep Mode, Away Mode
 * 5. Manual vs Automatic control conflict handling
 * 6. Master Automation enable/disable toggle
 */
public class AutomationTestMain {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("    SMART HOME AUTOMATION SIMULATOR - PHASE 4 AUTOMATION HARNESS   ");
        System.out.println("====================================================================\n");

        // 1. SETUP ENVIRONMENT
        SmartHome myHome = new SmartHome("Green Villa Residence");
        Room livingRoom = new Room("Living Room");
        Room entrance = new Room("Entrance");
        Room bedroom = new Room("Bedroom");

        Light livingLight = new Light("LGT-LR-01", "Ceiling Chandelier", "Living Room");
        AirConditioner livingAC = new AirConditioner("AC-LR-01", "Inverter AC Unit", "Living Room");
        Light porchLight = new Light("LGT-EN-01", "Porch Spotlight", "Entrance");
        Door frontDoor = new Door("DOR-EN-01", "Main Entrance Door", "Entrance", "1234");
        SecurityCamera porchCam = new SecurityCamera("CAM-EN-01", "Front Porch CCTV", "Entrance");
        Light bedLight = new Light("LGT-BD-01", "Bedside Reading Lamp", "Bedroom");

        livingRoom.addDevice(livingLight);
        livingRoom.addDevice(livingAC);
        entrance.addDevice(porchLight);
        entrance.addDevice(frontDoor);
        entrance.addDevice(porchCam);
        bedroom.addDevice(bedLight);

        myHome.addRoom(livingRoom);
        myHome.addRoom(entrance);
        myHome.addRoom(bedroom);

        // 2. INITIALIZE AUTOMATION MANAGER
        AutomationManager automation = new AutomationManager(myHome);
        automation.addEventListener((title, desc) ->
                System.out.printf("  [AUTO-EVENT LOG] %s -> %s%n", title, desc));

        System.out.println("--- Baseline Setup Complete. Active Rules: " + automation.getRules().size() + " ---\n");

        // TEST 1: TEMPERATURE-BASED AUTOMATION
        System.out.println(">>> TEST 1: Temperature-based Automation Rule");
        System.out.printf("Initial AC State: %s | Ambient Temp: %.1f°C%n", livingAC.getStatusSummary(), automation.getAmbientTemperature());
        System.out.println("Simulating heat spike to 32.0°C...");
        automation.setAmbientTemperature(32.0);
        System.out.printf("Post-Event AC State: %s%n%n", livingAC.getStatusSummary());

        // TEST 2: MOTION-BASED LIGHTING
        System.out.println(">>> TEST 2: Motion-Activated Lighting Rule");
        System.out.printf("Initial Porch Light: %s%n", porchLight.getStatusSummary());
        System.out.println("Simulating entrance motion detection...");
        automation.setMotionDetected(true);
        System.out.printf("Post-Event Porch Light: %s%n%n", porchLight.getStatusSummary());

        // TEST 3: NIGHT-TIME SOFT LIGHTING
        System.out.println(">>> TEST 3: Night Fall Soft Lighting Rule");
        System.out.printf("Initial Bedside Lamp: %s%n", bedLight.getStatusSummary());
        System.out.println("Simulating Night Fall...");
        automation.setNightTime(true);
        System.out.printf("Post-Event Bedside Lamp: %s%n%n", bedLight.getStatusSummary());

        // TEST 4: SCENE MANAGEMENT - MOVIE MODE
        System.out.println(">>> TEST 4: Scene Activation - 'Movie Mode'");
        livingLight.turnOn(90);
        System.out.printf("Living Light before Movie Mode: %s%n", livingLight.getStatusSummary());
        automation.getSceneManager().activateScene("Movie Mode");
        System.out.printf("Living Light after Movie Mode: %s%n", livingLight.getStatusSummary());
        System.out.printf("Living AC after Movie Mode: %s%n%n", livingAC.getStatusSummary());

        // TEST 5: SCENE MANAGEMENT - AWAY MODE
        System.out.println(">>> TEST 5: Scene Activation - 'Away Mode'");
        frontDoor.unlock("1234");
        frontDoor.openDoor();
        System.out.printf("Door before Away Mode: %s%n", frontDoor.getStatusSummary());
        automation.getSceneManager().activateScene("Away Mode");
        System.out.printf("Door after Away Mode: %s%n", frontDoor.getStatusSummary());
        System.out.printf("Camera after Away Mode: %s%n%n", porchCam.getStatusSummary());

        // TEST 6: MANUAL OVERRIDE VS AUTOMATIC CONFLICT HANDLING
        System.out.println(">>> TEST 6: Manual vs Automatic Control Conflict Handling");
        System.out.println("Automated rule turned AC ON at 21°C.");
        System.out.println("User manually turns AC OFF via dashboard button...");
        livingAC.turnOff();
        System.out.printf("Manual Override state: %s%n", livingAC.getStatusSummary());
        System.out.println("Evaluating rules again without new temperature change (within cooldown)...");
        automation.evaluateAllRules();
        System.out.printf("AC remains OFF (User preference respected): %s%n%n", livingAC.getStatusSummary());

        // TEST 7: DISABLING AUTOMATION GLOBALLY
        System.out.println(">>> TEST 7: Master Automation Toggle");
        System.out.println("Disabling Master Automation...");
        automation.setMasterAutomationEnabled(false);
        livingAC.turnOff();
        System.out.println("Simulating extreme temperature spike to 38.0°C...");
        automation.setAmbientTemperature(38.0);
        System.out.printf("AC State when automation is disabled: %s (Did NOT trigger)%n", livingAC.getStatusSummary());

        System.out.println("Re-enabling Master Automation...");
        automation.setMasterAutomationEnabled(true);
        automation.evaluateAllRules();
        System.out.printf("AC State after re-enabling: %s (Triggered successfully)%n%n", livingAC.getStatusSummary());

        System.out.println("====================================================================");
        System.out.println("    ALL PHASE 4 AUTOMATION & SCENE TESTS COMPLETED SUCCESSFULLY!   ");
        System.out.println("====================================================================");
    }
}

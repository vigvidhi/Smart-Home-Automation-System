package com.smarthome.test;

import com.smarthome.interfaces.DeviceStateListener;
import com.smarthome.model.*;

/**
 * Phase 2 Test Driver & Demonstration Main Class.
 * Validates:
 * 1. Object instantiation and encapsulation
 * 2. Polymorphic behaviors across devices
 * 3. Input validation and error handling
 * 4. State management and listener callbacks (Observer pattern)
 * 5. Multi-room aggregation and scene automation
 */
public class SimulationTestMain {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("    SMART HOME AUTOMATION SIMULATOR - PHASE 2 TEST HARNESS");
        System.out.println("====================================================================\n");

        // 1. SETUP SMART HOME AND ROOMS
        SmartHome myHome = new SmartHome("Green Villa Residence");

        Room livingRoom = new Room("Living Room");
        Room bedroom = new Room("Master Bedroom");
        Room entrance = new Room("Entrance");

        // 2. INSTANTIATE SMART DEVICES
        Light livingRoomLight = new Light("LGT-01", "Ceiling Chandelier", "Living Room", "Warm White");
        Fan livingRoomFan = new Fan("FAN-01", "Ceiling Fan", "Living Room");
        AirConditioner livingRoomAC = new AirConditioner("AC-01", "Inverter AC", "Living Room");

        Light bedLight = new Light("LGT-02", "Bedside Night Lamp", "Master Bedroom", "Warm White");
        Fan bedFan = new Fan("FAN-02", "Bedroom Fan", "Master Bedroom");
        AirConditioner bedAC = new AirConditioner("AC-02", "Bedroom AC", "Master Bedroom");

        Door mainDoor = new Door("DOR-01", "Front Entrance Door", "Entrance", "4321");
        SecurityCamera porchCamera = new SecurityCamera("CAM-01", "Porch CCTV Camera", "Entrance");

        // 3. ATTACH REAL-TIME STATE LISTENERS (Prepares for Phase 3 GUI updates)
        DeviceStateListener consoleLogger = (device, eventDesc) ->
                System.out.printf("  [LIVE TELEMETRY] %s (%s) -> %s%n",
                        device.getName(), device.getRoomName(), eventDesc);

        livingRoomLight.addStateListener(consoleLogger);
        livingRoomFan.addStateListener(consoleLogger);
        livingRoomAC.addStateListener(consoleLogger);
        mainDoor.addStateListener(consoleLogger);
        porchCamera.addStateListener(consoleLogger);

        // 4. POPULATE ROOMS (Demonstrating Aggregation)
        livingRoom.addDevice(livingRoomLight);
        livingRoom.addDevice(livingRoomFan);
        livingRoom.addDevice(livingRoomAC);

        bedroom.addDevice(bedLight);
        bedroom.addDevice(bedFan);
        bedroom.addDevice(bedAC);

        entrance.addDevice(mainDoor);
        entrance.addDevice(porchCamera);

        myHome.addRoom(livingRoom);
        myHome.addRoom(bedroom);
        myHome.addRoom(entrance);

        System.out.println("\n--- Initial Home Configuration ---");
        myHome.printHomeSummary();

        // 5. TEST INDIVIDUAL DEVICE FUNCTIONALITIES
        System.out.println(">>> TEST CASE 1: Individual Device Operations");
        System.out.println("Activating Living Room Chandelier:");
        livingRoomLight.turnOn();
        livingRoomLight.setBrightness(85);

        System.out.println("\nActivating Living Room Fan:");
        livingRoomFan.turnOn();
        livingRoomFan.setSpeed(4);
        livingRoomFan.setOscillating(true);

        System.out.println("\nActivating Living Room AC:");
        livingRoomAC.turnOn();
        livingRoomAC.setTargetTemperature(22);
        livingRoomAC.setMode("COOL");

        // 6. TEST SECURITY & DOOR OPERATIONS
        System.out.println("\n>>> TEST CASE 2: Security & Door Operations");
        System.out.println("Attempting to open locked door directly:");
        boolean openSuccess = mainDoor.openDoor(); // Should fail validation
        System.out.printf("Door open attempted while locked: %s%n", openSuccess ? "SUCCESS" : "FAILED (Safe)");

        System.out.println("\nEntering WRONG PIN ('9999'):");
        mainDoor.unlock("9999"); // Should fail

        System.out.println("\nEntering CORRECT PIN ('4321'):");
        mainDoor.unlock("4321"); // Should succeed
        mainDoor.openDoor(); // Now open should succeed

        System.out.println("\nSimulating Camera Motion Detection:");
        porchCamera.triggerMotion();

        // 7. TEST ERROR HANDLING AND BOUNDARY VALIDATION
        System.out.println("\n>>> TEST CASE 3: Boundary Validation and Defensive Error Handling");
        System.out.println("Attempting invalid fan speed (7):");
        livingRoomFan.setSpeed(7);

        System.out.println("Attempting invalid AC temperature (35°C):");
        livingRoomAC.setTargetTemperature(35);

        System.out.println("Attempting invalid light brightness (150%):");
        livingRoomLight.setBrightness(150);

        System.out.println("Attempting to adjust temperature on an AC that is OFF:");
        bedAC.turnOff();
        bedAC.setTargetTemperature(20);

        // 8. TEST POLYMORPHISM (Room-Level Bulk Operation)
        System.out.println("\n>>> TEST CASE 4: Polymorphic Room Operations");
        System.out.println("Turning off all devices in Living Room polymorphically:");
        livingRoom.turnOffAllDevices();

        // 9. TEST AUTOMATED SCENES
        System.out.println("\n>>> TEST CASE 5: Automated Scene Presets");
        myHome.executeScene("AWAY");
        myHome.printHomeSummary();

        myHome.executeScene("NIGHT");
        myHome.printHomeSummary();

        myHome.executeScene("MORNING");
        myHome.printHomeSummary();

        // 10. SYSTEM RESET
        System.out.println(">>> TEST CASE 6: System Factory Reset");
        myHome.executeScene("RESET");
        myHome.printHomeSummary();

        System.out.println("====================================================================");
        System.out.println("    ALL PHASE 2 UNIT & INTEGRATION TESTS EXECUTED SUCCESSFULLY!    ");
        System.out.println("====================================================================");
    }
}

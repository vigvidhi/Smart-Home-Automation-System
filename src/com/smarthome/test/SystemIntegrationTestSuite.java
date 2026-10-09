package com.smarthome.test;

import com.smarthome.automation.AutomationManager;
import com.smarthome.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Phase 5 Comprehensive System Integration & Unit Test Suite.
 * Executes automated test cases covering:
 * - Unit Level: Individual Device Boundary & Operation Validations
 * - Integration Level: Device <-> Automation <-> Scene <-> Observer Telemetry
 * - Security Level: Keypad Lockout, Perimeter Arming, Motion Alerting
 * - Error Handling & Recovery: Defending against invalid inputs without crashing
 */
public class SystemIntegrationTestSuite {

    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;
    private static final List<String> testReportRows = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("============================================================================================================");
        System.out.println("          SMART HOME AUTOMATION SIMULATOR - PHASE 5 COMPREHENSIVE TEST SUITE REPORT");
        System.out.println("============================================================================================================\n");

        long startTime = System.currentTimeMillis();

        // 1. UNIT TESTING (Individual Device Logic & Boundaries)
        runUnitTests();

        // 2. INTEGRATION TESTING (Cross-Component Communication)
        runIntegrationTests();

        // 3. SECURITY & ERROR RECOVERY TESTING
        runSecurityAndRecoveryTests();

        long elapsedMs = System.currentTimeMillis() - startTime;

        // PRINT FORMATTED TEST REPORT TABLE
        printFinalReport(elapsedMs);
    }

    private static void recordResult(String testId, String category, String testCase, String expected, String actual, boolean isPass) {
        totalTests++;
        if (isPass) {
            passedTests++;
        } else {
            failedTests++;
        }
        String status = isPass ? "PASS" : "FAIL";
        String row = String.format("| %-7s | %-13s | %-32s | %-22s | %-22s | %-6s |",
                testId, category, testCase, expected, actual, status);
        testReportRows.add(row);
    }

    private static void runUnitTests() {
        // UT-01: Light Brightness Valid Range
        Light light = new Light("UT-LGT-01", "Test Light", "Test Room");
        boolean b1 = light.setBrightness(75);
        recordResult("UT-01", "Unit (Light)", "setBrightness(75)", "75% (Accepted)", light.getBrightness() + "%", b1 && light.getBrightness() == 75);

        // UT-02: Light Brightness Boundary Violation (<0)
        boolean b2 = light.setBrightness(-10);
        recordResult("UT-02", "Unit (Light)", "setBrightness(-10)", "Rejected (Keeps 75%)", light.getBrightness() + "%", !b2 && light.getBrightness() == 75);

        // UT-03: Light Brightness Boundary Violation (>100)
        boolean b3 = light.setBrightness(150);
        recordResult("UT-03", "Unit (Light)", "setBrightness(150)", "Rejected (Keeps 75%)", light.getBrightness() + "%", !b3 && light.getBrightness() == 75);

        // UT-04: Fan Speed Valid Range (0-5)
        Fan fan = new Fan("UT-FAN-01", "Test Fan", "Test Room");
        boolean f1 = fan.setSpeed(4);
        recordResult("UT-04", "Unit (Fan)", "setSpeed(4)", "Speed 4 (Accepted)", "Speed " + fan.getSpeed(), f1 && fan.getSpeed() == 4);

        // UT-05: Fan Speed Boundary Violation (7)
        boolean f2 = fan.setSpeed(7);
        recordResult("UT-05", "Unit (Fan)", "setSpeed(7)", "Rejected (Keeps 4)", "Speed " + fan.getSpeed(), !f2 && fan.getSpeed() == 4);

        // UT-06: AC Temperature Valid Range (16-30°C)
        AirConditioner ac = new AirConditioner("UT-AC-01", "Test AC", "Test Room");
        ac.turnOn();
        boolean a1 = ac.setTargetTemperature(22);
        recordResult("UT-06", "Unit (AC)", "setTargetTemp(22)", "22°C (Accepted)", ac.getTargetTemperature() + "°C", a1 && ac.getTargetTemperature() == 22);

        // UT-07: AC Temperature Boundary Low (<16°C)
        boolean a2 = ac.setTargetTemperature(12);
        recordResult("UT-07", "Unit (AC)", "setTargetTemp(12)", "Rejected (Keeps 22°C)", ac.getTargetTemperature() + "°C", !a2 && ac.getTargetTemperature() == 22);

        // UT-08: AC Temperature Boundary High (>30°C)
        boolean a3 = ac.setTargetTemperature(35);
        recordResult("UT-08", "Unit (AC)", "setTargetTemp(35)", "Rejected (Keeps 22°C)", ac.getTargetTemperature() + "°C", !a3 && ac.getTargetTemperature() == 22);

        // UT-09: AC Adjustment While OFF
        ac.turnOff();
        boolean a4 = ac.setTargetTemperature(20);
        recordResult("UT-09", "Unit (AC)", "setTargetTemp while OFF", "Rejected (Must be ON)", "Rejected cleanly", !a4);

        // UT-10: Door Barrier - Open when locked
        Door door = new Door("UT-DOR-01", "Test Door", "Test Room", "1234");
        boolean d1 = door.openDoor();
        recordResult("UT-10", "Unit (Door)", "openDoor() while LOCKED", "Blocked (Door CLOSED)", door.isOpen() ? "OPEN" : "CLOSED", !d1 && !door.isOpen());
    }

    private static void runIntegrationTests() {
        SmartHome home = new SmartHome("Integration Villa");
        Room living = new Room("Living Room");
        Light livLight = new Light("INT-LGT-01", "Living Chandelier", "Living Room");
        AirConditioner livAC = new AirConditioner("INT-AC-01", "Living AC", "Living Room");
        Door frontDoor = new Door("INT-DOR-01", "Front Door", "Living Room", "1234");
        SecurityCamera camera = new SecurityCamera("INT-CAM-01", "Front Camera", "Living Room");

        living.addDevice(livLight);
        living.addDevice(livAC);
        living.addDevice(frontDoor);
        living.addDevice(camera);
        home.addRoom(living);

        AutomationManager automation = new AutomationManager(home);

        // IT-01: Observer Telemetry Integration
        final boolean[] listenerFired = {false};
        livLight.addStateListener((dev, evt) -> listenerFired[0] = true);
        livLight.turnOn();
        recordResult("IT-01", "Integration", "Observer Callback Dispatch", "Listener Fired (true)", String.valueOf(listenerFired[0]), listenerFired[0]);

        // IT-02: Environmental Trigger -> Auto-Cooling
        automation.setAmbientTemperature(32.0); // Triggers Rule 1
        recordResult("IT-02", "Integration", "Temp 32°C -> AC Auto ON", "AC ON at 22°C", livAC.getStatusSummary(), livAC.isOn() && livAC.getTargetTemperature() == 22);

        // IT-03: Scene Activation -> Sleep Mode
        automation.getSceneManager().activateScene("Sleep Mode");
        boolean sleepPass = !livLight.isOn() && frontDoor.isLocked() && camera.isRecording();
        recordResult("IT-03", "Integration", "Scene: Sleep Mode", "Lights OFF, Locked, REC", "Aligned correctly", sleepPass);

        // IT-04: Scene Activation -> Good Morning
        automation.getSceneManager().activateScene("Good Morning");
        boolean morningPass = livLight.isOn() && livLight.getBrightness() == 65 && !frontDoor.isLocked();
        recordResult("IT-04", "Integration", "Scene: Good Morning", "Lights 65%, Unlocked", "Aligned correctly", morningPass);

        // IT-05: Manual Override vs Auto Conflict
        livAC.turnOff(); // Manual user override
        automation.evaluateAllRules(); // Re-evaluate without sensor state change
        recordResult("IT-05", "Integration", "Manual User Override Priority", "AC remains OFF", livAC.isOn() ? "Turned ON" : "Remains OFF", !livAC.isOn());

        // IT-06: Master Automation Cutoff
        automation.setMasterAutomationEnabled(false);
        automation.setAmbientTemperature(38.0);
        recordResult("IT-06", "Integration", "Master Automation Cutoff", "AC stays OFF (Ignored)", livAC.isOn() ? "Turned ON" : "Stayed OFF", !livAC.isOn());
    }

    private static void runSecurityAndRecoveryTests() {
        Door door = new Door("SEC-DOR-01", "Vault Door", "Security Zone", "5555");

        // ST-01: Correct PIN Unlock
        boolean s1 = door.unlock("5555");
        recordResult("ST-01", "Security", "Unlock with Correct PIN", "UNLOCKED (true)", String.valueOf(s1), s1 && !door.isLocked());

        // ST-02: Invalid PIN Attempt
        door.lock();
        boolean s2 = door.unlock("0000");
        recordResult("ST-02", "Security", "Unlock with Wrong PIN", "Access Denied (false)", String.valueOf(s2), !s2 && door.isLocked());

        // ST-03: Security Keypad Lockout (3 Failed Attempts)
        door.unlock("1111"); // 2nd fail
        door.unlock("2222"); // 3rd fail -> Lockout active
        boolean s3 = door.unlock("5555"); // Even correct PIN blocked during lockout
        recordResult("ST-03", "Security", "3 Failed Attempts Lockout", "Blocked by Lockout", door.isSecurityLockoutActive() ? "Lockout Active" : "No Lockout", door.isSecurityLockoutActive() && !s3);

        // ST-04: Clear Security Lockout
        door.clearLockout();
        boolean s4 = door.unlock("5555");
        recordResult("ST-04", "Security", "Clear Lockout & Unlock", "UNLOCKED successfully", String.valueOf(s4), s4 && !door.isLocked());

        // RT-01: Factory Reset Recovery
        door.reset();
        boolean r1 = door.isLocked() && !door.isOpen() && !door.isSecurityLockoutActive();
        recordResult("RT-01", "Recovery", "Factory Reset Baseline", "LOCKED, CLOSED, Safe", "Baseline restored", r1);
    }

    private static void printFinalReport(long elapsedMs) {
        System.out.println("+---------+---------------+----------------------------------+------------------------+------------------------+--------+");
        System.out.println("| Test ID | Category      | Test Scenario / Input Description| Expected Outcome       | Actual Outcome Result  | Status |");
        System.out.println("+---------+---------------+----------------------------------+------------------------+------------------------+--------+");
        for (String row : testReportRows) {
            System.out.println(row);
        }
        System.out.println("+---------+---------------+----------------------------------+------------------------+------------------------+--------+");

        System.out.printf("%n============================================================================================================%n");
        System.out.printf("  TEST EXECUTION SUMMARY: Total: %d  |  PASSED: %d  |  FAILED: %d  |  SUCCESS RATE: %.1f%%  |  TIME: %d ms%n",
                totalTests, passedTests, failedTests, ((double) passedTests / totalTests) * 100.0, elapsedMs);
        System.out.printf("============================================================================================================%n");
    }
}

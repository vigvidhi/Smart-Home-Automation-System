package com.smarthome;

import com.smarthome.automation.AutomationManager;
import com.smarthome.gui.SmartHomeDashboard;
import com.smarthome.model.*;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main Application Launcher for the Smart Home Automation Simulator.
 * Integrates:
 * - 5 Household Rooms and 13 Smart Devices (Phase 2 & 3)
 * - AutomationManager & Rule Engine (Phase 4)
 * - Modern Interactive Swing Dashboard (Phase 3 & 4)
 */
public class SmartHomeApp {

    public static void main(String[] args) {
        // Set modern native Look and Feel if available
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Falls back to standard Java cross-platform look
        }

        // 1. Build Smart Home Architecture
        SmartHome myHome = new SmartHome("Harmony Smart Residence");

        // 2. Room 1: Living Room
        Room livingRoom = new Room("Living Room");
        livingRoom.addDevice(new Light("LGT-LR-01", "Ceiling Chandelier", "Living Room", "Warm White"));
        livingRoom.addDevice(new Fan("FAN-LR-01", "Living Room Fan", "Living Room"));
        livingRoom.addDevice(new AirConditioner("AC-LR-01", "Inverter AC Unit", "Living Room"));

        // 3. Room 2: Bedroom
        Room bedroom = new Room("Bedroom");
        bedroom.addDevice(new Light("LGT-BD-01", "Bedside Reading Lamp", "Bedroom", "Warm White"));
        bedroom.addDevice(new Fan("FAN-BD-01", "Bedroom Ceiling Fan", "Bedroom"));
        bedroom.addDevice(new AirConditioner("AC-BD-01", "Master Bedroom AC", "Bedroom"));

        // 4. Room 3: Kitchen
        Room kitchen = new Room("Kitchen");
        kitchen.addDevice(new Light("LGT-KT-01", "Countertop Task Lights", "Kitchen", "Daylight"));
        kitchen.addDevice(new Fan("FAN-KT-01", "Kitchen Exhaust Fan", "Kitchen"));

        // 5. Room 4: Bathroom
        Room bathroom = new Room("Bathroom");
        bathroom.addDevice(new Light("LGT-BT-01", "Vanity Mirror Light", "Bathroom", "Cool White"));
        bathroom.addDevice(new Fan("FAN-BT-01", "Bathroom Exhaust Vent", "Bathroom"));

        // 6. Room 5: Entrance
        Room entrance = new Room("Entrance");
        entrance.addDevice(new Light("LGT-EN-01", "Porch Spotlight", "Entrance", "Daylight"));
        entrance.addDevice(new Door("DOR-EN-01", "Main Entrance Door", "Entrance", "1234"));
        entrance.addDevice(new SecurityCamera("CAM-EN-01", "Front Porch CCTV", "Entrance"));

        // Register rooms into Smart Home
        myHome.addRoom(livingRoom);
        myHome.addRoom(bedroom);
        myHome.addRoom(kitchen);
        myHome.addRoom(bathroom);
        myHome.addRoom(entrance);

        // 7. Initialize Automation Manager (Phase 4 Engine)
        AutomationManager automationManager = new AutomationManager(myHome);

        // 8. Launch Graphical User Interface on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            SmartHomeDashboard dashboard = new SmartHomeDashboard(myHome, automationManager);
            dashboard.setVisible(true);
        });
    }
}

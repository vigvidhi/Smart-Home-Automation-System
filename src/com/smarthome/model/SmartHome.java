package com.smarthome.model;

import com.smarthome.interfaces.Adjustable;
import com.smarthome.interfaces.Dimmable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Master container representing the entire Smart Home.
 * Demonstrates:
 * - Aggregation (SmartHome manages multiple Room instances)
 * - Polymorphism (processes mixed device types in automated scenes)
 * - Automation Logic (Scene presets: Morning, Night, Away, Movie)
 */
public class SmartHome {
    private final String homeName;
    private final Map<String, Room> rooms;

    public SmartHome(String homeName) {
        this.homeName = (homeName != null && !homeName.trim().isEmpty()) ? homeName.trim() : "My Smart Residence";
        this.rooms = new LinkedHashMap<>();
    }

    public void addRoom(Room room) {
        if (room != null) {
            rooms.put(room.getRoomName().toLowerCase(), room);
        }
    }

    public Room getRoom(String roomName) {
        if (roomName == null) return null;
        return rooms.get(roomName.toLowerCase());
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public Device findDevice(String deviceId) {
        if (deviceId == null) return null;
        for (Room r : rooms.values()) {
            Device d = r.getDeviceById(deviceId);
            if (d != null) {
                return d;
            }
        }
        return null;
    }

    public List<Device> getAllDevices() {
        List<Device> all = new ArrayList<>();
        for (Room r : rooms.values()) {
            all.addAll(r.getDevices());
        }
        return all;
    }

    /**
     * Executes automated scenes across all rooms using Polymorphism.
     * @param sceneName Name of preset ("MORNING", "NIGHT", "AWAY", "MOVIE", "RESET")
     */
    public void executeScene(String sceneName) {
        if (sceneName == null) return;
        String normalized = sceneName.trim().toUpperCase();
        System.out.printf("%n======================================================%n");
        System.out.printf("  [AUTOMATION ENGINE] Executing Scene: %s%n", normalized);
        System.out.printf("======================================================%n");

        switch (normalized) {
            case "MORNING":
                applyMorningScene();
                break;
            case "NIGHT":
                applyNightScene();
                break;
            case "AWAY":
                applyAwayScene();
                break;
            case "MOVIE":
                applyMovieScene();
                break;
            case "RESET":
                resetAllDevices();
                break;
            default:
                System.err.println("[SCENE ERROR] Unknown scene preset: " + sceneName);
        }
    }

    private void applyMorningScene() {
        for (Device d : getAllDevices()) {
            if (d instanceof Light) {
                Light light = (Light) d;
                light.turnOn();
                light.setBrightness(60);
            } else if (d instanceof Fan) {
                Fan fan = (Fan) d;
                fan.turnOn();
                fan.setSpeed(2);
            } else if (d instanceof AirConditioner) {
                AirConditioner ac = (AirConditioner) d;
                ac.turnOn();
                ac.setTargetTemperature(25);
                ac.setMode("ECO");
            } else if (d instanceof Door) {
                // Interior doors can be unlocked
                Door door = (Door) d;
                if (!door.getRoomName().equalsIgnoreCase("Entrance")) {
                    door.unlock("1234");
                }
            }
        }
        System.out.println("[SCENE COMPLETE] 'Morning Scene' successfully engaged.");
    }

    private void applyNightScene() {
        for (Device d : getAllDevices()) {
            if (d instanceof Light) {
                // Bedside light dim, others off
                if (d.getName().toLowerCase().contains("bedside") || d.getName().toLowerCase().contains("night")) {
                    ((Light) d).turnOn();
                    ((Light) d).setBrightness(15);
                } else {
                    d.turnOff();
                }
            } else if (d instanceof Fan) {
                Fan fan = (Fan) d;
                fan.turnOn();
                fan.setSpeed(3);
            } else if (d instanceof AirConditioner) {
                AirConditioner ac = (AirConditioner) d;
                ac.turnOn();
                ac.setTargetTemperature(22);
                ac.setMode("COOL");
            } else if (d instanceof Door) {
                Door door = (Door) d;
                door.lock(); // All doors locked at night
            } else if (d instanceof SecurityCamera) {
                SecurityCamera cam = (SecurityCamera) d;
                cam.turnOn();
                cam.startRecording();
            }
        }
        System.out.println("[SCENE COMPLETE] 'Night Scene' successfully engaged.");
    }

    private void applyAwayScene() {
        for (Device d : getAllDevices()) {
            if (d instanceof Door) {
                ((Door) d).lock();
            } else if (d instanceof SecurityCamera) {
                SecurityCamera cam = (SecurityCamera) d;
                cam.turnOn();
                cam.startRecording();
            } else {
                // Polymorphic shutdown of all general appliances (lights, fans, ACs)
                d.turnOff();
            }
        }
        System.out.println("[SCENE COMPLETE] 'Away Scene' successfully engaged. Residence secured.");
    }

    private void applyMovieScene() {
        for (Device d : getAllDevices()) {
            if (d.getRoomName().equalsIgnoreCase("Living Room")) {
                if (d instanceof Light) {
                    ((Light) d).turnOn();
                    ((Light) d).setBrightness(20);
                } else if (d instanceof AirConditioner) {
                    ((AirConditioner) d).turnOn();
                    ((AirConditioner) d).setTargetTemperature(21);
                } else if (d instanceof Fan) {
                    ((Fan) d).setSpeed(1);
                }
            }
        }
        System.out.println("[SCENE COMPLETE] 'Movie Scene' configured for Living Room.");
    }

    public void resetAllDevices() {
        System.out.println("[ACTION] Resetting all home appliances to factory defaults...");
        for (Device d : getAllDevices()) {
            d.reset();
        }
        System.out.println("[ACTION COMPLETE] All devices reset.");
    }

    public void printHomeSummary() {
        System.out.println("\n######################################################");
        System.out.printf("  %s - MASTER STATUS REPORT%n", homeName.toUpperCase());
        System.out.println("######################################################");
        for (Room r : rooms.values()) {
            r.printRoomStatus();
            System.out.println();
        }
        System.out.printf("Total Rooms: %d | Total Devices: %d%n", rooms.size(), getAllDevices().size());
        System.out.println("######################################################\n");
    }

    public String getHomeName() {
        return homeName;
    }
}

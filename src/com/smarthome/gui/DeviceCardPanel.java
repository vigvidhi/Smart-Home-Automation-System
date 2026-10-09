package com.smarthome.gui;

import com.smarthome.model.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Reusable Card Panel for individual Smart Appliances.
 * Demonstrates:
 * - GUI and Model Separation (OOP Integration)
 * - Reactive UI updates via DeviceStateListener (Observer Pattern)
 * - Input validation & friendly user feedback
 */
public class DeviceCardPanel extends JPanel {
    private final Device device;
    private final JLabel statusBadge;
    private final JButton powerBtn;
    private final JPanel controlsPanel;

    // Device-specific UI controls
    private JSlider brightnessSlider;
    private JComboBox<String> lightToneCombo;

    private JSlider fanSpeedSlider;
    private JCheckBox fanOscillateCheck;

    private JLabel acTempLabel;
    private JComboBox<String> acModeCombo;

    private JLabel doorStateLabel;
    private JButton doorOpenCloseBtn;

    private JLabel camStatusLabel;

    public DeviceCardPanel(Device device) {
        this.device = device;
        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(250, 252, 255));
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 220, 230), 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        // 1. Header: Icon/Type, Name, and Status Badge
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(device.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(new Color(33, 43, 54));

        JLabel typeLabel = new JLabel(getDeviceTypeString());
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        typeLabel.setForeground(new Color(110, 120, 135));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1));
        titleBlock.setOpaque(false);
        titleBlock.add(nameLabel);
        titleBlock.add(typeLabel);

        statusBadge = new JLabel(" OFF ", SwingConstants.CENTER);
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(new EmptyBorder(3, 8, 3, 8));
        updateBadgeVisuals();

        headerPanel.add(titleBlock, BorderLayout.WEST);
        headerPanel.add(statusBadge, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Center: Specific Controls Panel
        controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setOpaque(false);
        buildDeviceSpecificControls();
        add(controlsPanel, BorderLayout.CENTER);

        // 3. Footer: Master Power Toggle Button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footerPanel.setOpaque(false);
        powerBtn = new JButton(device.isOn() ? "Turn OFF" : "Turn ON");
        powerBtn.setFocusPainted(false);
        powerBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        powerBtn.addActionListener(e -> togglePower());
        footerPanel.add(powerBtn);
        add(footerPanel, BorderLayout.SOUTH);

        // 4. Attach Observer Listener: Updates GUI when device state changes externally
        device.addStateListener((d, eventDesc) -> SwingUtilities.invokeLater(this::syncUIFromModel));

        // Initial UI Synchronization
        syncUIFromModel();
    }

    private String getDeviceTypeString() {
        if (device instanceof Light) return "💡 Lighting Appliance";
        if (device instanceof Fan) return "🌀 Ventilation Fan";
        if (device instanceof AirConditioner) return "❄️ Climate / AC Unit";
        if (device instanceof Door) return "🚪 Smart Security Lock";
        if (device instanceof SecurityCamera) return "📹 Surveillance Camera";
        return "📱 Smart Appliance";
    }

    private void buildDeviceSpecificControls() {
        if (device instanceof Light) {
            buildLightControls((Light) device);
        } else if (device instanceof Fan) {
            buildFanControls((Fan) device);
        } else if (device instanceof AirConditioner) {
            buildAcControls((AirConditioner) device);
        } else if (device instanceof Door) {
            buildDoorControls((Door) device);
        } else if (device instanceof SecurityCamera) {
            buildCameraControls((SecurityCamera) device);
        }
    }

    private void buildLightControls(Light light) {
        JPanel sliderPanel = new JPanel(new BorderLayout(5, 5));
        sliderPanel.setOpaque(false);
        sliderPanel.setBorder(new EmptyBorder(6, 0, 4, 0));

        JLabel sliderLabel = new JLabel("Brightness:");
        sliderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        brightnessSlider = new JSlider(0, 100, light.getBrightness());
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setMajorTickSpacing(25);
        brightnessSlider.setPaintLabels(false);
        brightnessSlider.addChangeListener(e -> {
            if (!brightnessSlider.getValueIsAdjusting()) {
                light.setBrightness(brightnessSlider.getValue());
            }
        });

        sliderPanel.add(sliderLabel, BorderLayout.WEST);
        sliderPanel.add(brightnessSlider, BorderLayout.CENTER);
        controlsPanel.add(sliderPanel);

        // Tone Dropdown
        JPanel tonePanel = new JPanel(new BorderLayout(5, 5));
        tonePanel.setOpaque(false);
        JLabel toneLabel = new JLabel("Tone:");
        toneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lightToneCombo = new JComboBox<>(new String[]{"Warm White", "Cool White", "Daylight"});
        lightToneCombo.setSelectedItem(light.getColorTone());
        lightToneCombo.addActionListener(e -> light.setColorTone((String) lightToneCombo.getSelectedItem()));
        tonePanel.add(toneLabel, BorderLayout.WEST);
        tonePanel.add(lightToneCombo, BorderLayout.CENTER);
        controlsPanel.add(tonePanel);
    }

    private void buildFanControls(Fan fan) {
        JPanel speedPanel = new JPanel(new BorderLayout(5, 5));
        speedPanel.setOpaque(false);
        speedPanel.setBorder(new EmptyBorder(6, 0, 4, 0));

        JLabel speedLabel = new JLabel("Speed (0-5):");
        speedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        fanSpeedSlider = new JSlider(0, 5, fan.getSpeed());
        fanSpeedSlider.setPaintTicks(true);
        fanSpeedSlider.setMajorTickSpacing(1);
        fanSpeedSlider.setSnapToTicks(true);
        fanSpeedSlider.setPaintLabels(true);
        fanSpeedSlider.addChangeListener(e -> {
            if (!fanSpeedSlider.getValueIsAdjusting()) {
                fan.setSpeed(fanSpeedSlider.getValue());
            }
        });

        speedPanel.add(speedLabel, BorderLayout.WEST);
        speedPanel.add(fanSpeedSlider, BorderLayout.CENTER);
        controlsPanel.add(speedPanel);

        fanOscillateCheck = new JCheckBox("Oscillation", fan.isOscillating());
        fanOscillateCheck.setOpaque(false);
        fanOscillateCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fanOscillateCheck.addActionListener(e -> fan.setOscillating(fanOscillateCheck.isSelected()));
        controlsPanel.add(fanOscillateCheck);
    }

    private void buildAcControls(AirConditioner ac) {
        JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        tempPanel.setOpaque(false);

        JButton minusBtn = new JButton("-");
        minusBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        minusBtn.addActionListener(e -> {
            if (!ac.decreaseTemperature()) {
                showWarning("AC must be ON and temperature must be >= 16°C.");
            }
        });

        acTempLabel = new JLabel(ac.getTargetTemperature() + "°C");
        acTempLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        acTempLabel.setForeground(new Color(24, 144, 255));

        JButton plusBtn = new JButton("+");
        plusBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        plusBtn.addActionListener(e -> {
            if (!ac.increaseTemperature()) {
                showWarning("AC must be ON and temperature must be <= 30°C.");
            }
        });

        tempPanel.add(new JLabel("Set Temp:"));
        tempPanel.add(minusBtn);
        tempPanel.add(acTempLabel);
        tempPanel.add(plusBtn);
        controlsPanel.add(tempPanel);

        // Mode Combo
        JPanel modePanel = new JPanel(new BorderLayout(5, 5));
        modePanel.setOpaque(false);
        JLabel modeLabel = new JLabel("Mode:");
        modeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        acModeCombo = new JComboBox<>(new String[]{"COOL", "HEAT", "FAN", "ECO"});
        acModeCombo.setSelectedItem(ac.getMode());
        acModeCombo.addActionListener(e -> ac.setMode((String) acModeCombo.getSelectedItem()));
        modePanel.add(modeLabel, BorderLayout.WEST);
        modePanel.add(acModeCombo, BorderLayout.CENTER);
        controlsPanel.add(modePanel);
    }

    private void buildDoorControls(Door door) {
        JPanel doorInfo = new JPanel(new BorderLayout(4, 4));
        doorInfo.setOpaque(false);
        doorInfo.setBorder(new EmptyBorder(4, 0, 4, 0));

        doorStateLabel = new JLabel("Status: " + door.getStatusSummary());
        doorStateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        doorInfo.add(doorStateLabel, BorderLayout.CENTER);
        controlsPanel.add(doorInfo);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        btnRow.setOpaque(false);

        JButton unlockBtn = new JButton("Unlock (PIN)");
        unlockBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        unlockBtn.addActionListener(e -> {
            String pin = JOptionPane.showInputDialog(this, "Enter 4-Digit Security PIN (Default: 1234):",
                    "Door Security Check", JOptionPane.QUESTION_MESSAGE);
            if (pin != null) {
                if (!door.unlock(pin)) {
                    JOptionPane.showMessageDialog(this, "Access Denied! Incorrect PIN.", "Security Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton lockBtn = new JButton("Lock");
        lockBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lockBtn.addActionListener(e -> door.lock());

        doorOpenCloseBtn = new JButton(door.isOpen() ? "Close Door" : "Open Door");
        doorOpenCloseBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        doorOpenCloseBtn.addActionListener(e -> {
            if (door.isOpen()) {
                door.closeDoor();
            } else {
                if (!door.openDoor()) {
                    JOptionPane.showMessageDialog(this, "Cannot open door: Deadbolt is LOCKED!", "Lock Alert", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnRow.add(unlockBtn);
        btnRow.add(lockBtn);
        btnRow.add(doorOpenCloseBtn);
        controlsPanel.add(btnRow);
    }

    private void buildCameraControls(SecurityCamera cam) {
        JPanel camInfo = new JPanel(new BorderLayout(4, 4));
        camInfo.setOpaque(false);
        camInfo.setBorder(new EmptyBorder(4, 0, 4, 0));

        camStatusLabel = new JLabel(cam.isRecording() ? "● Recording Active" : "Standby Mode");
        camStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        camInfo.add(camStatusLabel, BorderLayout.CENTER);
        controlsPanel.add(camInfo);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        btnRow.setOpaque(false);

        JButton motionBtn = new JButton("Simulate Motion");
        motionBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        motionBtn.addActionListener(e -> cam.triggerMotion());

        JButton clearBtn = new JButton("Clear Alert");
        clearBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        clearBtn.addActionListener(e -> cam.clearMotion());

        btnRow.add(motionBtn);
        btnRow.add(clearBtn);
        controlsPanel.add(btnRow);
    }

    private void togglePower() {
        if (device.isOn()) {
            device.turnOff();
        } else {
            device.turnOn();
        }
    }

    public void syncUIFromModel() {
        powerBtn.setText(device.isOn() ? "Turn OFF" : "Turn ON");
        updateBadgeVisuals();

        // Update specific component states
        if (device instanceof Light) {
            Light light = (Light) device;
            if (brightnessSlider != null && brightnessSlider.getValue() != light.getBrightness()) {
                brightnessSlider.setValue(light.getBrightness());
            }
            if (lightToneCombo != null && !lightToneCombo.getSelectedItem().equals(light.getColorTone())) {
                lightToneCombo.setSelectedItem(light.getColorTone());
            }
        } else if (device instanceof Fan) {
            Fan fan = (Fan) device;
            if (fanSpeedSlider != null && fanSpeedSlider.getValue() != fan.getSpeed()) {
                fanSpeedSlider.setValue(fan.getSpeed());
            }
            if (fanOscillateCheck != null) {
                fanOscillateCheck.setSelected(fan.isOscillating());
            }
        } else if (device instanceof AirConditioner) {
            AirConditioner ac = (AirConditioner) device;
            if (acTempLabel != null) {
                acTempLabel.setText(ac.getTargetTemperature() + "°C");
            }
            if (acModeCombo != null && !acModeCombo.getSelectedItem().equals(ac.getMode())) {
                acModeCombo.setSelectedItem(ac.getMode());
            }
        } else if (device instanceof Door) {
            Door door = (Door) device;
            if (doorStateLabel != null) {
                doorStateLabel.setText("Status: " + door.getStatusSummary());
            }
            if (doorOpenCloseBtn != null) {
                doorOpenCloseBtn.setText(door.isOpen() ? "Close Door" : "Open Door");
            }
        } else if (device instanceof SecurityCamera) {
            SecurityCamera cam = (SecurityCamera) device;
            if (camStatusLabel != null) {
                if (cam.isMotionDetected()) {
                    camStatusLabel.setText("⚠️ MOTION DETECTED!");
                    camStatusLabel.setForeground(new Color(220, 38, 38));
                } else if (cam.isRecording()) {
                    camStatusLabel.setText("● Recording (" + cam.getResolution() + ")");
                    camStatusLabel.setForeground(new Color(16, 185, 129));
                } else {
                    camStatusLabel.setText("Standby Mode");
                    camStatusLabel.setForeground(new Color(100, 116, 139));
                }
            }
        }
        repaint();
    }

    private void updateBadgeVisuals() {
        if (device instanceof Door) {
            Door door = (Door) device;
            if (door.isLocked()) {
                statusBadge.setText(" LOCKED ");
                statusBadge.setBackground(new Color(239, 68, 68)); // Red
                statusBadge.setForeground(Color.WHITE);
            } else {
                statusBadge.setText(" UNLOCKED ");
                statusBadge.setBackground(new Color(245, 158, 11)); // Amber
                statusBadge.setForeground(Color.WHITE);
            }
            return;
        }

        if (device instanceof SecurityCamera) {
            SecurityCamera cam = (SecurityCamera) device;
            if (cam.isMotionDetected()) {
                statusBadge.setText(" ALERT! ");
                statusBadge.setBackground(new Color(220, 38, 38));
                statusBadge.setForeground(Color.WHITE);
            } else if (cam.isRecording()) {
                statusBadge.setText(" REC ");
                statusBadge.setBackground(new Color(16, 185, 129));
                statusBadge.setForeground(Color.WHITE);
            } else {
                statusBadge.setText(" STANDBY ");
                statusBadge.setBackground(new Color(148, 163, 184));
                statusBadge.setForeground(Color.WHITE);
            }
            return;
        }

        if (device.isOn()) {
            statusBadge.setText(" ON ");
            statusBadge.setBackground(new Color(34, 197, 94)); // Emerald Green
            statusBadge.setForeground(Color.WHITE);
        } else {
            statusBadge.setText(" OFF ");
            statusBadge.setBackground(new Color(148, 163, 184)); // Slate Gray
            statusBadge.setForeground(Color.WHITE);
        }
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Warning", JOptionPane.WARNING_MESSAGE);
    }
}

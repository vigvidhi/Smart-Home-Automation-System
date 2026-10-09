package com.smarthome.gui;

import com.smarthome.automation.AutomationEventListener;
import com.smarthome.automation.AutomationManager;
import com.smarthome.interfaces.DeviceStateListener;
import com.smarthome.model.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Main Graphical User Interface Dashboard for the Smart Home Simulator.
 * Refined in Phase 5:
 * - Tooltips across all buttons and controls for enhanced usability
 * - Safety confirmation dialogs for destructive actions (Master Shutdown, Reset)
 * - Activity Log Export to physical file (ActivityLog.txt)
 * - Clear Log & Reset recovery options
 * - Robust error handling & zero-crash guarantees
 */
public class SmartHomeDashboard extends JFrame {
    private final SmartHome smartHome;
    private final AutomationManager automationManager;
    private final List<DeviceCardPanel> cardPanels = new ArrayList<>();
    private AutomationControlPanel automationControlPanel;

    // Header metrics labels
    private JLabel totalDevicesLabel;
    private JLabel activeDevicesLabel;
    private JLabel securityStatusLabel;
    private JLabel clockLabel;

    // Real-time console log
    private JTextArea logTextArea;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public SmartHomeDashboard(SmartHome smartHome) {
        this(smartHome, new AutomationManager(smartHome));
    }

    public SmartHomeDashboard(SmartHome smartHome, AutomationManager automationManager) {
        this.smartHome = smartHome;
        this.automationManager = automationManager;

        initWindowProperties();
        initUIComponents();
        attachGlobalTelemetryListeners();
        attachAutomationEventListener();
        startVirtualClock();
        refreshAllComponents();

        appendLog("SYSTEM", "Smart Home Simulation Dashboard & Automation Engine initialized successfully.");
    }

    private void initWindowProperties() {
        setTitle("Smart Home Automation Simulator - College Project Demo [v2.0]");
        setSize(1180, 850);
        setMinimumSize(new Dimension(1000, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
    }

    private void initUIComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout(0, 0));
        rootPanel.setBackground(new Color(243, 244, 246));

        // 1. TOP HEADER & METRICS BANNER
        rootPanel.add(buildHeaderBanner(), BorderLayout.NORTH);

        // 2. CENTER: SCENES TOOLBAR + ROOM TABBED PANE
        JPanel centerContainer = new JPanel(new BorderLayout(0, 8));
        centerContainer.setOpaque(false);
        centerContainer.setBorder(new EmptyBorder(8, 12, 8, 12));

        centerContainer.add(buildQuickScenesToolbar(), BorderLayout.NORTH);
        centerContainer.add(buildRoomTabbedPane(), BorderLayout.CENTER);
        rootPanel.add(centerContainer, BorderLayout.CENTER);

        // 3. BOTTOM: ACTIVITY EVENT LOG CONSOLE
        rootPanel.add(buildConsoleLogPanel(), BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private JPanel buildHeaderBanner() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 41, 59)); // Slate Navy
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Left: Title and subtitle
        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false);

        JLabel titleLabel = new JLabel("🏠 " + smartHome.getHomeName().toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Pure Software Simulation • Java OOP • Swing • Rule Engine • In-Memory Telemetry");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(148, 163, 184));

        titleBox.add(titleLabel);
        titleBox.add(subLabel);
        header.add(titleBox, BorderLayout.WEST);

        // Right: Real-time Stats Badges
        JPanel statsBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsBox.setOpaque(false);

        totalDevicesLabel = createStatBadge("Total Devices: 0", new Color(51, 65, 85));
        totalDevicesLabel.setToolTipText("Total appliances configured across all rooms");

        activeDevicesLabel = createStatBadge("Active: 0", new Color(16, 185, 129));
        activeDevicesLabel.setToolTipText("Count of currently powered-on appliances");

        securityStatusLabel = createStatBadge("Security: SECURE", new Color(14, 116, 144));
        securityStatusLabel.setToolTipText("Overall perimeter security & alarm status");

        clockLabel = createStatBadge("12:00:00", new Color(71, 85, 105));
        clockLabel.setToolTipText("Virtual simulated system clock");

        statsBox.add(totalDevicesLabel);
        statsBox.add(activeDevicesLabel);
        statsBox.add(securityStatusLabel);
        statsBox.add(clockLabel);

        header.add(statsBox, BorderLayout.EAST);
        return header;
    }

    private JLabel createStatBadge(String text, Color bg) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(Color.WHITE);
        badge.setBackground(bg);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(6, 12, 6, 12));
        return badge;
    }

    private JPanel buildQuickScenesToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel scenesTitle = new JLabel("Quick Actions & Scenes:");
        scenesTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        scenesTitle.setForeground(new Color(71, 85, 105));
        bar.add(scenesTitle);

        // Scene Action Buttons with Tooltips
        bar.add(createActionButton("☀️ Morning", "Activate Good Morning Scene (Lights 65%, Fan Speed 2, Unlocked)",
                new Color(245, 158, 11), e -> triggerScene("Good Morning")));
        bar.add(createActionButton("🌙 Night / Sleep", "Activate Sleep Mode (General lights OFF, Bedside 10%, Doors locked, Cameras active)",
                new Color(99, 102, 241), e -> triggerScene("Sleep Mode")));
        bar.add(createActionButton("🚗 Away", "Activate Away Mode (All appliances OFF, Deadbolts engaged, Full surveillance)",
                new Color(239, 68, 68), e -> triggerScene("Away Mode")));
        bar.add(createActionButton("🎬 Movie", "Activate Movie Mode (Living Room dimmed to 15%, Quiet Fan, 21°C Cool)",
                new Color(139, 92, 246), e -> triggerScene("Movie Mode")));

        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 24));
        bar.add(sep);

        // Bulk Controls with Tooltips & Safety Dialogs
        bar.add(createActionButton("💡 All Lights ON", "Turn ON all lights in every room",
                new Color(34, 197, 94), e -> bulkLightsControl(true)));
        bar.add(createActionButton("💡 All Lights OFF", "Turn OFF all lights in every room",
                new Color(100, 116, 139), e -> bulkLightsControl(false)));
        bar.add(createActionButton("🛑 Turn ALL Devices OFF", "Master Shutdown: Turn OFF all appliances in residence",
                new Color(225, 29, 72), e -> confirmAndTurnAllOff()));
        bar.add(createActionButton("🔄 Factory Reset", "Restore factory baseline for devices and automation",
                new Color(71, 85, 105), e -> confirmAndResetAll()));

        return bar;
    }

    private JButton createActionButton(String label, String tooltip, Color baseColor, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(248, 250, 252));
        btn.setForeground(baseColor);
        btn.setToolTipText(tooltip);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(baseColor, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(listener);
        return btn;
    }

    private JTabbedPane buildRoomTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // 1. Overview Tab (All Devices)
        tabbedPane.addTab("🏡 Whole Residence", createRoomScrollPane(smartHome.getAllDevices()));

        // 2. Individual Room Tabs
        for (Room room : smartHome.getAllRooms()) {
            tabbedPane.addTab(room.getRoomName(), createRoomScrollPane(room.getDevices()));
        }

        // 3. Automation & Rules Control Panel Tab
        automationControlPanel = new AutomationControlPanel(automationManager, this::refreshAllComponents);
        tabbedPane.addTab("🤖 Smart Automation & Rules", automationControlPanel);

        return tabbedPane;
    }

    private JScrollPane createRoomScrollPane(List<Device> devices) {
        JPanel grid = new JPanel(new GridLayout(0, 3, 14, 14));
        grid.setBackground(new Color(243, 244, 246));
        grid.setBorder(new EmptyBorder(12, 12, 12, 12));

        for (Device d : devices) {
            DeviceCardPanel card = new DeviceCardPanel(d);
            cardPanels.add(card);
            grid.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel buildConsoleLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                new EmptyBorder(8, 14, 8, 14)
        ));

        JPanel logHeader = new JPanel(new BorderLayout());
        logHeader.setOpaque(false);

        JLabel logTitle = new JLabel("📋 Real-Time Activity & Automation Event Log:");
        logTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logTitle.setForeground(new Color(71, 85, 105));

        JPanel logActionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        logActionButtons.setOpaque(false);

        JButton exportBtn = new JButton("💾 Export Log");
        exportBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        exportBtn.setFocusPainted(false);
        exportBtn.setToolTipText("Export telemetry log to ActivityLog.txt file");
        exportBtn.addActionListener(e -> exportLogToFile());

        JButton clearBtn = new JButton("Clear Log");
        clearBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        clearBtn.setFocusPainted(false);
        clearBtn.setToolTipText("Clear the on-screen console output");
        clearBtn.addActionListener(e -> logTextArea.setText(""));

        logActionButtons.add(exportBtn);
        logActionButtons.add(clearBtn);

        logHeader.add(logTitle, BorderLayout.WEST);
        logHeader.add(logActionButtons, BorderLayout.EAST);
        panel.add(logHeader, BorderLayout.NORTH);

        logTextArea = new JTextArea(6, 80);
        logTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logTextArea.setEditable(false);
        logTextArea.setBackground(new Color(15, 23, 42)); // Dark Slate Terminal
        logTextArea.setForeground(new Color(226, 232, 240));
        logTextArea.setCaretColor(Color.WHITE);

        JScrollPane logScroll = new JScrollPane(logTextArea);
        logScroll.setBorder(new LineBorder(new Color(51, 65, 85), 1));
        panel.add(logScroll, BorderLayout.CENTER);

        return panel;
    }

    private void attachGlobalTelemetryListeners() {
        DeviceStateListener globalLogger = (device, eventDescription) -> {
            SwingUtilities.invokeLater(() -> {
                appendLog(device.getRoomName() + " • " + device.getName(), eventDescription);
                refreshAllComponents();
            });
        };

        for (Device d : smartHome.getAllDevices()) {
            d.addStateListener(globalLogger);
        }
    }

    private void attachAutomationEventListener() {
        AutomationEventListener autoLogger = (title, description) -> {
            SwingUtilities.invokeLater(() -> {
                appendLog("AUTOMATION ENGINE", title + ": " + description);
                refreshAllComponents();
            });
        };
        automationManager.addEventListener(autoLogger);
    }

    public void appendLog(String source, String message) {
        String timestamp = LocalTime.now().format(timeFormatter);
        String formattedLine = String.format("[%s] [%s] %s%n", timestamp, source, message);
        logTextArea.append(formattedLine);
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    }

    private void startVirtualClock() {
        Timer timer = new Timer(1000, e -> {
            clockLabel.setText("🕒 " + LocalTime.now().format(timeFormatter));
        });
        timer.start();
    }

    public void refreshAllComponents() {
        refreshHomeStats();
        for (DeviceCardPanel card : cardPanels) {
            card.syncUIFromModel();
        }
        if (automationControlPanel != null) {
            automationControlPanel.syncUIFromManager();
        }
    }

    private void refreshHomeStats() {
        int total = smartHome.getAllDevices().size();
        int active = 0;
        boolean anyAlarm = false;

        for (Device d : smartHome.getAllDevices()) {
            if (d.isOn()) active++;
            if (d instanceof SecurityCamera && ((SecurityCamera) d).isMotionDetected()) {
                anyAlarm = true;
            }
            if (d instanceof Door && ((Door) d).isSecurityLockoutActive()) {
                anyAlarm = true;
            }
        }

        totalDevicesLabel.setText("Total Devices: " + total);
        activeDevicesLabel.setText("Active: " + active);

        if (anyAlarm) {
            securityStatusLabel.setText("🚨 ALARM: SECURITY ALERT");
            securityStatusLabel.setBackground(new Color(220, 38, 38));
        } else {
            securityStatusLabel.setText("🛡️ Security: SECURE");
            securityStatusLabel.setBackground(new Color(14, 116, 144));
        }
    }

    // --- Action Handlers with Confirmation Dialogs ---

    private void triggerScene(String sceneName) {
        appendLog("SCENE CONTROLLER", "Executing Scene Preset: " + sceneName);
        boolean success = automationManager.getSceneManager().activateScene(sceneName);
        if (!success) {
            smartHome.executeScene(sceneName);
        }
        refreshAllComponents();
    }

    private void bulkLightsControl(boolean turnOn) {
        for (Device d : smartHome.getAllDevices()) {
            if (d instanceof Light) {
                if (turnOn) d.turnOn();
                else d.turnOff();
            }
        }
        appendLog("BULK ACTION", turnOn ? "Turned ON all lights in home." : "Turned OFF all lights in home.");
        refreshAllComponents();
    }

    private void confirmAndTurnAllOff() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to shut down ALL appliances in the residence?",
                "Confirm Master Power Shutdown",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            for (Device d : smartHome.getAllDevices()) {
                d.turnOff();
            }
            appendLog("BULK ACTION", "Master Power Shutdown: All appliances powered down.");
            refreshAllComponents();
        }
    }

    private void confirmAndResetAll() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Restore entire smart residence and automation engine to factory baseline?",
                "Confirm Factory Reset",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            smartHome.resetAllDevices();
            automationManager.resetAutomationDefaults();
            appendLog("SYSTEM", "Factory Reset: All devices and automation baseline restored.");
            refreshAllComponents();
        }
    }

    private void exportLogToFile() {
        String logContent = logTextArea.getText();
        if (logContent.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Event log is currently empty. Nothing to export.", "Export Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File targetFile = new File("ActivityLog.txt");
        try (FileWriter writer = new FileWriter(targetFile)) {
            writer.write("====================================================================\n");
            writer.write("        SMART HOME AUTOMATION SIMULATOR - TELEMETRY EVENT LOG        \n");
            writer.write("====================================================================\n\n");
            writer.write(logContent);
            JOptionPane.showMessageDialog(this,
                    "Event log exported successfully to:\n" + targetFile.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to export log: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

package com.smarthome.gui;

import com.smarthome.automation.AutomationManager;
import com.smarthome.automation.AutomationRule;
import com.smarthome.automation.Scene;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 * Dedicated GUI Panel for Managing Smart Automation Rules, Scene Presets,
 * and Simulated Environmental Sensor Readings.
 * Demonstrates:
 * - GUI Integration for Phase 4
 * - Sensor Value Simulation (Sliders & Toggles)
 * - Conflict Handling & Master Automation Controls
 */
public class AutomationControlPanel extends JPanel {
    private final AutomationManager automationManager;
    private final Runnable onStateUpdatedCallback;

    // UI Widgets
    private JToggleButton masterAutomationToggle;
    private JLabel tempDisplayLabel;
    private JSlider tempSlider;
    private JToggleButton nightToggle;
    private JToggleButton securityToggle;

    private JComboBox<String> sceneComboBox;
    private JLabel sceneDescLabel;

    private JPanel rulesListPanel;

    public AutomationControlPanel(AutomationManager automationManager, Runnable onStateUpdatedCallback) {
        this.automationManager = automationManager;
        this.onStateUpdatedCallback = onStateUpdatedCallback;

        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(243, 244, 246));
        setBorder(new EmptyBorder(14, 16, 14, 16));

        // Top: Master Status & Quick Controls
        add(buildMasterHeader(), BorderLayout.NORTH);

        // Center: Split between Sensor Simulation (Left) and Rules Checklist (Right)
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 14, 0));
        centerGrid.setOpaque(false);
        centerGrid.add(buildSensorsSimulationPanel());
        centerGrid.add(buildRulesListPanel());
        add(centerGrid, BorderLayout.CENTER);

        // Bottom: Scene Management Section
        add(buildSceneManagementPanel(), BorderLayout.SOUTH);

        syncUIFromManager();
    }

    private JPanel buildMasterHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 16, 10, 16)
        ));

        JLabel title = new JLabel("⚡ Smart Automation & Rule Engine Control Center");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(new Color(30, 41, 59));

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightControls.setOpaque(false);

        masterAutomationToggle = new JToggleButton("Automation: ENABLED");
        masterAutomationToggle.setSelected(true);
        masterAutomationToggle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        masterAutomationToggle.setFocusPainted(false);
        masterAutomationToggle.setBackground(new Color(34, 197, 94));
        masterAutomationToggle.setForeground(Color.WHITE);
        masterAutomationToggle.addActionListener(e -> {
            boolean enabled = masterAutomationToggle.isSelected();
            automationManager.setMasterAutomationEnabled(enabled);
            masterAutomationToggle.setText(enabled ? "Automation: ENABLED" : "Automation: DISABLED");
            masterAutomationToggle.setBackground(enabled ? new Color(34, 197, 94) : new Color(239, 68, 68));
            if (onStateUpdatedCallback != null) onStateUpdatedCallback.run();
        });

        JButton resetBtn = new JButton("Reset Baseline");
        resetBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resetBtn.setFocusPainted(false);
        resetBtn.addActionListener(e -> {
            automationManager.resetAutomationDefaults();
            syncUIFromManager();
            if (onStateUpdatedCallback != null) onStateUpdatedCallback.run();
        });

        rightControls.add(masterAutomationToggle);
        rightControls.add(resetBtn);

        header.add(title, BorderLayout.WEST);
        header.add(rightControls, BorderLayout.EAST);
        return header;
    }

    private JPanel buildSensorsSimulationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createTitledBorder(new EmptyBorder(6, 10, 10, 10), "🌡️ Simulated Sensor Controls",
                        TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13), new Color(51, 65, 85))
        ));

        // 1. Ambient Temperature Slider
        JPanel tempBox = new JPanel(new BorderLayout(5, 5));
        tempBox.setOpaque(false);
        tempBox.setBorder(new EmptyBorder(6, 6, 12, 6));

        tempDisplayLabel = new JLabel("Room Ambient: 26.0°C (Trigger > 28°C)", JLabel.LEFT);
        tempDisplayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        tempSlider = new JSlider(15, 40, 26);
        tempSlider.setMajorTickSpacing(5);
        tempSlider.setMinorTickSpacing(1);
        tempSlider.setPaintTicks(true);
        tempSlider.setPaintLabels(true);
        tempSlider.addChangeListener(e -> {
            int val = tempSlider.getValue();
            tempDisplayLabel.setText(String.format("Room Ambient: %d.0°C %s", val, val > 28 ? "(Auto-Cool Active!)" : ""));
            if (val > 28) tempDisplayLabel.setForeground(new Color(220, 38, 38));
            else tempDisplayLabel.setForeground(new Color(51, 65, 85));

            automationManager.setAmbientTemperature(val);
            if (onStateUpdatedCallback != null) onStateUpdatedCallback.run();
        });

        tempBox.add(tempDisplayLabel, BorderLayout.NORTH);
        tempBox.add(tempSlider, BorderLayout.CENTER);
        panel.add(tempBox);

        // 2. Motion Detection Simulation Button
        JPanel motionBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        motionBox.setOpaque(false);
        JButton triggerMotionBtn = new JButton("🚶 Simulate Motion at Entrance");
        triggerMotionBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        triggerMotionBtn.setFocusPainted(false);
        triggerMotionBtn.setBackground(new Color(241, 245, 249));
        triggerMotionBtn.addActionListener(e -> {
            automationManager.setMotionDetected(true);
            if (onStateUpdatedCallback != null) onStateUpdatedCallback.run();
            // Automatically clear motion after 2.5 seconds simulation pulse
            Timer resetTimer = new Timer(2500, evt -> {
                automationManager.setMotionDetected(false);
                if (onStateUpdatedCallback != null) onStateUpdatedCallback.run();
            });
            resetTimer.setRepeats(false);
            resetTimer.start();
        });
        motionBox.add(triggerMotionBtn);
        panel.add(motionBox);

        // 3. Day / Night Cycle Toggle
        JPanel nightBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        nightBox.setOpaque(false);
        nightToggle = new JToggleButton("☀️ Day Mode Active");
        nightToggle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nightToggle.setFocusPainted(false);
        nightToggle.addActionListener(e -> {
            boolean isNight = nightToggle.isSelected();
            nightToggle.setText(isNight ? "🌙 Night Mode Active" : "☀️ Day Mode Active");
            automationManager.setNightTime(isNight);
            if (onStateUpdatedCallback != null) onStateUpdatedCallback.run();
        });
        nightBox.add(nightToggle);
        panel.add(nightBox);

        // 4. Armed Security Perimeter
        JPanel secBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        secBox.setOpaque(false);
        securityToggle = new JToggleButton("🛡️ Security Mode: DISARMED");
        securityToggle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        securityToggle.setFocusPainted(false);
        securityToggle.addActionListener(e -> {
            boolean armed = securityToggle.isSelected();
            securityToggle.setText(armed ? "🚨 Security Mode: ARMED" : "🛡️ Security Mode: DISARMED");
            securityToggle.setForeground(armed ? new Color(220, 38, 38) : Color.BLACK);
            automationManager.setSecurityMode(armed);
            if (onStateUpdatedCallback != null) onStateUpdatedCallback.run();
        });
        secBox.add(securityToggle);
        panel.add(secBox);

        return panel;
    }

    private JPanel buildRulesListPanel() {
        JPanel container = new JPanel(new BorderLayout(5, 5));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createTitledBorder(new EmptyBorder(6, 10, 10, 10), "📋 Active Automation Rules",
                        TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13), new Color(51, 65, 85))
        ));

        rulesListPanel = new JPanel();
        rulesListPanel.setLayout(new BoxLayout(rulesListPanel, BoxLayout.Y_AXIS));
        rulesListPanel.setOpaque(false);

        populateRulesList();

        JScrollPane scrollPane = new JScrollPane(rulesListPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private void populateRulesList() {
        rulesListPanel.removeAll();
        for (AutomationRule rule : automationManager.getRules()) {
            JPanel itemPanel = new JPanel(new BorderLayout(8, 2));
            itemPanel.setOpaque(false);
            itemPanel.setBorder(new EmptyBorder(6, 4, 6, 4));

            JCheckBox check = new JCheckBox(rule.getName(), rule.isEnabled());
            check.setFont(new Font("Segoe UI", Font.BOLD, 12));
            check.setOpaque(false);
            check.addActionListener(e -> {
                automationManager.setRuleEnabled(rule.getId(), check.isSelected());
                if (onStateUpdatedCallback != null) onStateUpdatedCallback.run();
            });

            JLabel desc = new JLabel("<html><font color='#64748B'>" + rule.getDescription() + "</font></html>");
            desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            JPanel textBlock = new JPanel(new GridLayout(2, 1));
            textBlock.setOpaque(false);
            textBlock.add(check);
            textBlock.add(desc);

            itemPanel.add(textBlock, BorderLayout.CENTER);
            rulesListPanel.add(itemPanel);
            rulesListPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        }
        rulesListPanel.revalidate();
        rulesListPanel.repaint();
    }

    private JPanel buildSceneManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createTitledBorder(new EmptyBorder(6, 10, 10, 10), "🎬 Preset Scene Management",
                        TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13), new Color(51, 65, 85))
        ));

        JPanel selectorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        selectorRow.setOpaque(false);

        List<Scene> scenes = automationManager.getSceneManager().getAllScenes();
        String[] sceneNames = scenes.stream().map(Scene::getName).toArray(String[]::new);

        sceneComboBox = new JComboBox<>(sceneNames);
        sceneComboBox.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sceneComboBox.setPreferredSize(new Dimension(160, 28));

        sceneDescLabel = new JLabel();
        sceneDescLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        sceneDescLabel.setForeground(new Color(71, 85, 105));

        JButton activateBtn = new JButton("🚀 Activate Selected Scene");
        activateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        activateBtn.setBackground(new Color(99, 102, 241));
        activateBtn.setForeground(Color.WHITE);
        activateBtn.setFocusPainted(false);
        activateBtn.addActionListener(e -> {
            String selected = (String) sceneComboBox.getSelectedItem();
            if (selected != null) {
                automationManager.getSceneManager().activateScene(selected);
                if (onStateUpdatedCallback != null) onStateUpdatedCallback.run();
            }
        });

        sceneComboBox.addActionListener(e -> updateSceneDescription());
        updateSceneDescription();

        selectorRow.add(new JLabel("Select Scene:"));
        selectorRow.add(sceneComboBox);
        selectorRow.add(activateBtn);

        panel.add(selectorRow, BorderLayout.WEST);
        panel.add(sceneDescLabel, BorderLayout.SOUTH);
        return panel;
    }

    private void updateSceneDescription() {
        String selected = (String) sceneComboBox.getSelectedItem();
        Scene sc = automationManager.getSceneManager().getScene(selected);
        if (sc != null) {
            sceneDescLabel.setText("Configuration: " + sc.getDescription());
        }
    }

    public void syncUIFromManager() {
        masterAutomationToggle.setSelected(automationManager.isMasterAutomationEnabled());
        masterAutomationToggle.setText(automationManager.isMasterAutomationEnabled() ? "Automation: ENABLED" : "Automation: DISABLED");
        masterAutomationToggle.setBackground(automationManager.isMasterAutomationEnabled() ? new Color(34, 197, 94) : new Color(239, 68, 68));

        tempSlider.setValue((int) automationManager.getAmbientTemperature());
        nightToggle.setSelected(automationManager.isNightTime());
        nightToggle.setText(automationManager.isNightTime() ? "🌙 Night Mode Active" : "☀️ Day Mode Active");

        securityToggle.setSelected(automationManager.isSecurityMode());
        securityToggle.setText(automationManager.isSecurityMode() ? "🚨 Security Mode: ARMED" : "🛡️ Security Mode: DISARMED");
        securityToggle.setForeground(automationManager.isSecurityMode() ? new Color(220, 38, 38) : Color.BLACK);

        populateRulesList();
        repaint();
    }
}

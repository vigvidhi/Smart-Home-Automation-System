package com.smarthome.automation;

/**
 * Listener interface for automation triggers and events.
 * Connects the AutomationManager to the GUI activity log.
 */
public interface AutomationEventListener {
    void onAutomationTriggered(String title, String description);
}

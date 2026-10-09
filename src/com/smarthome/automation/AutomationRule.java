package com.smarthome.automation;

/**
 * Encapsulates an individual smart automation rule binding a Condition to an Action.
 * Demonstrates:
 * - Composition: Holds instances of Condition and Action
 * - Encapsulation: Protects rule enablement and cooldown state
 * - Conflict Resolution: Debounce/cooldown mechanism prevents rapid oscillation
 */
public class AutomationRule {
    private final String id;
    private final String name;
    private final String description;
    private final Condition condition;
    private final Action action;

    private boolean isEnabled;
    private long lastTriggeredEpochMs;
    private long cooldownMs; // Minimum wait time before re-triggering

    public AutomationRule(String id, String name, String description, Condition condition, Action action) {
        this(id, name, description, condition, action, 3000); // 3 seconds default cooldown
    }

    public AutomationRule(String id, String name, String description, Condition condition, Action action, long cooldownMs) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule Name cannot be empty.");
        }
        if (condition == null || action == null) {
            throw new IllegalArgumentException("Condition and Action cannot be null.");
        }
        this.id = id.trim();
        this.name = name.trim();
        this.description = (description != null) ? description.trim() : "";
        this.condition = condition;
        this.action = action;
        this.isEnabled = true;
        this.lastTriggeredEpochMs = 0;
        this.cooldownMs = cooldownMs;
    }

    /**
     * Evaluates condition and executes action if enabled and cooldown has passed.
     * @return true if the rule was triggered and executed; false otherwise.
     */
    public boolean evaluateAndExecute() {
        if (!isEnabled) {
            return false;
        }

        long now = System.currentTimeMillis();
        if ((now - lastTriggeredEpochMs) < cooldownMs) {
            return false; // Still in cooldown period
        }

        if (condition.evaluate()) {
            action.execute();
            lastTriggeredEpochMs = now;
            return true;
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public long getCooldownMs() {
        return cooldownMs;
    }

    public void setCooldownMs(long cooldownMs) {
        this.cooldownMs = cooldownMs;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) -> %s", id, name, isEnabled ? "ACTIVE" : "DISABLED", description);
    }
}

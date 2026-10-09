package com.smarthome.automation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a multi-device preset macro configuration (e.g. Good Morning, Sleep Mode).
 * Demonstrates:
 * - Composition (Scene contains multiple Action commands)
 * - Polymorphism (Executes varied actions uniformly)
 */
public class Scene {
    private final String name;
    private final String description;
    private final List<Action> actions;

    public Scene(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Scene name cannot be empty.");
        }
        this.name = name.trim();
        this.description = (description != null) ? description.trim() : "";
        this.actions = new ArrayList<>();
    }

    public void addAction(Action action) {
        if (action != null) {
            this.actions.add(action);
        }
    }

    /**
     * Executes all configured scene actions sequentially.
     */
    public void activate() {
        for (Action action : actions) {
            action.execute();
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getActionCount() {
        return actions.size();
    }
}

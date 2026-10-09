package com.smarthome.automation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages registration, storage, and activation of all home scenes.
 * Demonstrates Aggregation (manages Scene objects).
 */
public class SceneManager {
    private final Map<String, Scene> scenes;

    public SceneManager() {
        this.scenes = new LinkedHashMap<>();
    }

    public void registerScene(Scene scene) {
        if (scene != null) {
            scenes.put(scene.getName().toLowerCase(), scene);
        }
    }

    public Scene getScene(String name) {
        if (name == null) return null;
        return scenes.get(name.trim().toLowerCase());
    }

    public List<Scene> getAllScenes() {
        return new ArrayList<>(scenes.values());
    }

    public boolean activateScene(String name) {
        Scene scene = getScene(name);
        if (scene != null) {
            scene.activate();
            return true;
        }
        System.err.println("[SCENE ERROR] Scene not found: " + name);
        return false;
    }
}

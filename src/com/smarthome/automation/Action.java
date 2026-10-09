package com.smarthome.automation;

/**
 * Functional Interface representing an executable automation action.
 * Demonstrates Abstraction and the Command Pattern.
 */
@FunctionalInterface
public interface Action {
    /**
     * Executes the assigned device mutation or macro operation.
     */
    void execute();
}

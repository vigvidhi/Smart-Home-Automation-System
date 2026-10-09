package com.smarthome.automation;

/**
 * Functional Interface representing an automation evaluation condition.
 * Demonstrates Abstraction in OOP.
 */
@FunctionalInterface
public interface Condition {
    /**
     * Evaluates whether the condition is currently satisfied.
     * @return true if condition triggers, false otherwise.
     */
    boolean evaluate();
}

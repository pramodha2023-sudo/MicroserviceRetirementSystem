package com.cloudnative.retirement.model;

import java.time.LocalDateTime;

/**
 * Represents a retirement decision event for metrics and validation.
 */
public class RetirementEvent {
    private final String serviceId;
    private final LocalDateTime timestamp;
    private final double utilityScore;
    private final int dependencyCount;
    private final String decision; // RETIRE or RETAIN
    private final double cpuFreed;
    private final String reason;

    public RetirementEvent(String serviceId, double utilityScore, int dependencyCount, 
                          String decision, double cpuFreed, String reason) {
        this.serviceId = serviceId;
        this.timestamp = LocalDateTime.now();
        this.utilityScore = utilityScore;
        this.dependencyCount = dependencyCount;
        this.decision = decision;
        this.cpuFreed = cpuFreed;
        this.reason = reason;
    }

    public String getServiceId() {
        return serviceId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getUtilityScore() {
        return utilityScore;
    }

    public int getDependencyCount() {
        return dependencyCount;
    }

    public String getDecision() {
        return decision;
    }

    public double getCpuFreed() {
        return cpuFreed;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "RetirementEvent{" +
                "serviceId='" + serviceId + '\'' +
                ", timestamp=" + timestamp +
                ", utilityScore=" + utilityScore +
                ", dependencyCount=" + dependencyCount +
                ", decision='" + decision + '\'' +
                ", cpuFreed=" + cpuFreed +
                ", reason='" + reason + '\'' +
                '}';
    }
}

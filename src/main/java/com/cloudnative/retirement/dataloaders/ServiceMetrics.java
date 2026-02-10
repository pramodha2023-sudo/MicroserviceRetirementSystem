package com.cloudnative.retirement.dataloaders;

/**
 * Represents metrics for a microservice from real-world data.
 * Used for loading data from CSV or external sources.
 */
public class ServiceMetrics {
    private final String serviceId;
    private final int requestCount;
    private final double slaScore;
    private final int dependentCount;
    private final long timestamp;

    public ServiceMetrics(String serviceId, int requestCount, double slaScore, int dependentCount) {
        this(serviceId, requestCount, slaScore, dependentCount, System.currentTimeMillis());
    }

    public ServiceMetrics(String serviceId, int requestCount, double slaScore, int dependentCount, long timestamp) {
        this.serviceId = serviceId;
        this.requestCount = requestCount;
        this.slaScore = Math.max(0.0, Math.min(1.0, slaScore)); // Clamp to [0, 1]
        this.dependentCount = dependentCount;
        this.timestamp = timestamp;
    }

    public String getServiceId() {
        return serviceId;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public double getSlaScore() {
        return slaScore;
    }

    public int getDependentCount() {
        return dependentCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("ServiceMetrics{id='%s', requests=%d, sla=%.2f, dependents=%d}", 
                           serviceId, requestCount, slaScore, dependentCount);
    }
}

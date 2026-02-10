package com.cloudnative.retirement.model;

import java.time.LocalDateTime;

/**
 * Represents a microservice in the cloud-native ecosystem.
 * Each service has autonomy to manage its own lifecycle.
 */
public class Microservice {
    private final String serviceId;
    private final String serviceName;
    private final LocalDateTime createdAt;
    private volatile double utilizationRate; // 0-1
    private volatile int requestCount; // requests in current window
    private volatile int dependentServiceCount;
    private volatile boolean isRetired;
    private LocalDateTime retiredAt;
    private double slaContribution; // 0-1

    public Microservice(String serviceId, String serviceName) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.createdAt = LocalDateTime.now();
        this.utilizationRate = 0.5;
        this.requestCount = 0;
        this.dependentServiceCount = 0;
        this.isRetired = false;
        this.slaContribution = 0.5;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getUtilizationRate() {
        return utilizationRate;
    }

    public void setUtilizationRate(double utilizationRate) {
        this.utilizationRate = Math.max(0, Math.min(1, utilizationRate));
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public int getDependentServiceCount() {
        return dependentServiceCount;
    }

    public void setDependentServiceCount(int count) {
        this.dependentServiceCount = count;
    }

    public boolean isRetired() {
        return isRetired;
    }

    public void retire() {
        this.isRetired = true;
        this.retiredAt = LocalDateTime.now();
    }

    public LocalDateTime getRetiredAt() {
        return retiredAt;
    }

    public double getSlaContribution() {
        return slaContribution;
    }

    public void setSlaContribution(double slaContribution) {
        this.slaContribution = Math.max(0, Math.min(1, slaContribution));
    }

    @Override
    public String toString() {
        return "Microservice{" +
                "serviceId='" + serviceId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", utilizationRate=" + utilizationRate +
                ", requestCount=" + requestCount +
                ", dependentServiceCount=" + dependentServiceCount +
                ", isRetired=" + isRetired +
                '}';
    }
}

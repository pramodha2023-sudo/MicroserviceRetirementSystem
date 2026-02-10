package com.cloudnative.retirement.modules;

import com.cloudnative.retirement.model.Microservice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lifecycle Learning Engine
 * Uses lightweight ML models (trend analysis, decay functions) to learn service relevance over time.
 * Predicts future utility based on historical patterns.
 */
public class LifecycleLearningEngine {
    private static final Logger logger = LoggerFactory.getLogger(LifecycleLearningEngine.class);

    private static final double DECAY_FACTOR = 0.95; // Exponential decay per cycle
    private static final double TREND_WEIGHT = 0.4;
    private static final double DECAY_WEIGHT = 0.6;

    private final int maxHistoryWindow;
    private final java.util.Map<String, java.util.Deque<Double>> utilityHistory;

    public LifecycleLearningEngine(int maxHistoryWindow) {
        this.maxHistoryWindow = maxHistoryWindow;
        this.utilityHistory = new java.util.concurrent.ConcurrentHashMap<>();
    }

    /**
     * Records a utility measurement for a service and learns from the pattern.
     */
    public void recordUtility(String serviceId, double utilityScore) {
        utilityHistory.computeIfAbsent(serviceId, k -> new java.util.LinkedList<>())
                     .addLast(utilityScore);

        // Maintain window size
        java.util.Deque<Double> history = utilityHistory.get(serviceId);
        if (history.size() > maxHistoryWindow) {
            history.removeFirst();
        }

        logger.debug("Recorded utility for {}: {}", serviceId, utilityScore);
    }

    /**
     * Predicts future utility based on learned patterns.
     * Uses trend analysis and exponential decay.
     */
    public double predictFutureUtility(Microservice service, double currentUtility) {
        String serviceId = service.getServiceId();
        java.util.Deque<Double> history = utilityHistory.get(serviceId);

        if (history == null || history.isEmpty()) {
            // No history - assume stable
            return currentUtility;
        }

        double trendComponent = calculateTrend(history);
        double decayComponent = calculateDecay(currentUtility, history.size());

        double predictedUtility = TREND_WEIGHT * trendComponent + DECAY_WEIGHT * decayComponent;

        logger.debug("Predicted future utility for {}: {} (trend: {}, decay: {})", 
                   serviceId, predictedUtility, trendComponent, decayComponent);

        return Math.max(0.0, Math.min(1.0, predictedUtility));
    }

    /**
     * Calculates trend direction from recent history.
     * Positive trend = increasing utility, Negative = decreasing
     */
    private double calculateTrend(java.util.Deque<Double> history) {
        if (history.size() < 2) {
            return history.getLast();
        }

        double[] values = history.stream().mapToDouble(Double::doubleValue).toArray();
        int n = values.length;

        // Simple linear trend calculation
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += values[i];
            sumXY += i * values[i];
            sumX2 += i * i;
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        
        // Current value adjusted by trend direction
        double currentValue = values[n - 1];
        double trendAdjustedValue = currentValue + (slope * 0.5); // Moderate trend influence

        return Math.max(0.0, Math.min(1.0, trendAdjustedValue));
    }

    /**
     * Calculates exponential decay of utility over time.
     * Services that were useful long ago are less likely to be useful now.
     */
    private double calculateDecay(double currentUtility, int cyclesSinceCreation) {
        // Exponential decay: U(t) = U₀ × (DECAY_FACTOR^t)
        double decayedUtility = currentUtility * Math.pow(DECAY_FACTOR, Math.log(cyclesSinceCreation + 1));
        return Math.max(0.0, Math.min(1.0, decayedUtility));
    }

    /**
     * Gets learning statistics for a service.
     */
    public String getLearningStats(String serviceId) {
        java.util.Deque<Double> history = utilityHistory.get(serviceId);
        if (history == null || history.isEmpty()) {
            return "No learning data for " + serviceId;
        }

        double average = history.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double min = history.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = history.stream().mapToDouble(Double::doubleValue).max().orElse(0);

        return String.format("Service %s - History: %d cycles, Avg: %.3f, Min: %.3f, Max: %.3f",
                           serviceId, history.size(), average, min, max);
    }
}

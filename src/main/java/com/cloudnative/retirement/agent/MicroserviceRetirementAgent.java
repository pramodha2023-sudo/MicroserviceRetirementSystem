package com.cloudnative.retirement.agent;

import com.cloudnative.retirement.model.Microservice;
import com.cloudnative.retirement.model.RetirementEvent;
import com.cloudnative.retirement.modules.DependencyAwarenessModule;
import com.cloudnative.retirement.modules.LifecycleLearningEngine;
import com.cloudnative.retirement.modules.UtilityAssessmentModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Microservice Retirement Agent (MRA)
 * Embedded within each microservice; controls self-evaluation and retirement decisions.
 * Autonomously decides when a service should retire based on learned utility and lifecycle intelligence.
 */
public class MicroserviceRetirementAgent {
    private static final Logger logger = LoggerFactory.getLogger(MicroserviceRetirementAgent.class);

    private final Microservice microservice;
    private final UtilityAssessmentModule utilityModule;
    private final LifecycleLearningEngine learningEngine;
    private final DependencyAwarenessModule dependencyModule;
    
    private final double utilityThreshold; // Below this threshold, consider retirement
    private final int retentionWindowSize; // Number of cycles to check threshold
    private int lowUtilityCount;
    
    private RetirementEvent lastRetirementEvent;

    public MicroserviceRetirementAgent(Microservice microservice, 
                                       UtilityAssessmentModule utilityModule,
                                       LifecycleLearningEngine learningEngine,
                                       DependencyAwarenessModule dependencyModule,
                                       double utilityThreshold,
                                       int retentionWindowSize) {
        this.microservice = microservice;
        this.utilityModule = utilityModule;
        this.learningEngine = learningEngine;
        this.dependencyModule = dependencyModule;
        this.utilityThreshold = utilityThreshold;
        this.retentionWindowSize = retentionWindowSize;
        this.lowUtilityCount = 0;
    }

    /**
     * Main agent decision loop - evaluates retirement necessity each cycle.
     * @return RetirementEvent if decision was made, null otherwise
     */
    public RetirementEvent evaluateRetirement() {
        if (microservice.isRetired()) {
            return null;
        }

        // Step 1: Compute utility score
        double utilityScore = utilityModule.computeUtility(microservice);
        logger.debug("Service {} utility score: {}", microservice.getServiceId(), utilityScore);

        // Step 2: Predict future relevance using lifecycle learning
        double predictedFutureUtility = learningEngine.predictFutureUtility(microservice, utilityScore);
        logger.debug("Service {} predicted future utility: {}", microservice.getServiceId(), predictedFutureUtility);

        // Step 3: Track low utility periods
        if (utilityScore < utilityThreshold && predictedFutureUtility < utilityThreshold) {
            lowUtilityCount++;
        } else {
            lowUtilityCount = 0;
        }

        // Step 4: Check if retirement threshold is met
        if (lowUtilityCount >= retentionWindowSize) {
            return evaluateRetirementSafety(utilityScore);
        }

        // Service continues operation
        RetirementEvent retainEvent = new RetirementEvent(
            microservice.getServiceId(),
            utilityScore,
            microservice.getDependentServiceCount(),
            "RETAIN",
            0,
            "Utility above threshold or insufficient low-utility window"
        );
        this.lastRetirementEvent = retainEvent;
        return retainEvent;
    }

    /**
     * Evaluates if retirement can be safely executed.
     */
    private RetirementEvent evaluateRetirementSafety(double utilityScore) {
        logger.info("Service {} approaching retirement threshold", microservice.getServiceId());

        // Check dependencies
        if (!dependencyModule.canSafelyRetire(microservice)) {
            logger.warn("Service {} has critical dependencies, cannot retire safely", 
                       microservice.getServiceId());
            
            RetirementEvent retainEvent = new RetirementEvent(
                microservice.getServiceId(),
                utilityScore,
                microservice.getDependentServiceCount(),
                "RETAIN",
                0,
                "Critical dependencies prevent retirement"
            );
            this.lastRetirementEvent = retainEvent;
            return retainEvent;
        }

        // Safe to retire
        logger.info("Initiating safe retirement for service {}", microservice.getServiceId());
        executeRetirement();

        double cpuFreed = calculateCpuFreed();
        RetirementEvent retirementEvent = new RetirementEvent(
            microservice.getServiceId(),
            utilityScore,
            0,
            "RETIRE",
            cpuFreed,
            "Low utility sustained for " + lowUtilityCount + " cycles with no critical dependencies"
        );
        
        this.lastRetirementEvent = retirementEvent;
        return retirementEvent;
    }

    /**
     * Executes graceful shutdown and retirement of the microservice.
     */
    private void executeRetirement() {
        try {
            logger.info("Executing graceful shutdown for service {}", microservice.getServiceId());
            
            // Simulate graceful shutdown: drain requests, notify dependents
            Thread.sleep(50); // Simulate graceful period
            
            // Mark service as retired
            microservice.retire();
            
            logger.info("Service {} successfully retired", microservice.getServiceId());
        } catch (InterruptedException e) {
            logger.error("Retirement execution interrupted for service {}", 
                        microservice.getServiceId(), e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Estimates CPU resources freed by retiring this service.
     */
    private double calculateCpuFreed() {
        // Estimated CPU freed (in arbitrary units)
        return (1.0 - microservice.getUtilizationRate()) * 20.0;
    }

    public Microservice getMicroservice() {
        return microservice;
    }

    public double getUtilityThreshold() {
        return utilityThreshold;
    }

    public int getLowUtilityCount() {
        return lowUtilityCount;
    }

    public RetirementEvent getLastRetirementEvent() {
        return lastRetirementEvent;
    }

    @Override
    public String toString() {
        return "MicroserviceRetirementAgent{" +
                "serviceId='" + microservice.getServiceId() + '\'' +
                ", lowUtilityCount=" + lowUtilityCount +
                ", retired=" + microservice.isRetired() +
                '}';
    }
}

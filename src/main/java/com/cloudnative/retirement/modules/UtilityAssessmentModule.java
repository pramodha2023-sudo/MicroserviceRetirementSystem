package com.cloudnative.retirement.modules;

import com.cloudnative.retirement.model.Microservice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility Assessment Module
 * Computes a utility score based on request volume, SLA contribution, collaboration frequency, and trust.
 * Utility = (RequestWeight × NormalizedRequests + SLAWeight × SLAContribution + CollaborationWeight × DependentsNormalized) / TotalWeight
 */
public class UtilityAssessmentModule {
    private static final Logger logger = LoggerFactory.getLogger(UtilityAssessmentModule.class);

    private static final double REQUEST_WEIGHT = 0.40;
    private static final double SLA_WEIGHT = 0.35;
    private static final double COLLABORATION_WEIGHT = 0.25;
    private static final double MAX_REQUESTS_PER_CYCLE = 1000.0;
    private static final double MAX_DEPENDENTS = 20.0;

    /**
     * Computes the utility score for a given microservice.
     * Range: [0.0, 1.0] where 1.0 = maximum utility, 0.0 = no utility
     */
    public double computeUtility(Microservice service) {
        double requestComponent = computeRequestUtility(service.getRequestCount());
        double slaComponent = service.getSlaContribution();
        double collaborationComponent = computeCollaborationUtility(service.getDependentServiceCount());

        double totalWeight = REQUEST_WEIGHT + SLA_WEIGHT + COLLABORATION_WEIGHT;
        double utilityScore = (REQUEST_WEIGHT * requestComponent + 
                             SLA_WEIGHT * slaComponent + 
                             COLLABORATION_WEIGHT * collaborationComponent) / totalWeight;

        logger.debug("Utility breakdown - Request: {}, SLA: {}, Collaboration: {}, Total: {}", 
                   requestComponent, slaComponent, collaborationComponent, utilityScore);

        return Math.max(0.0, Math.min(1.0, utilityScore));
    }

    /**
     * Request utility component - normalized request count
     */
    private double computeRequestUtility(int requestCount) {
        return Math.min(1.0, requestCount / MAX_REQUESTS_PER_CYCLE);
    }

    /**
     * Collaboration utility component - based on dependent services
     */
    private double computeCollaborationUtility(int dependentCount) {
        // Services with many dependents are more important
        return Math.min(1.0, dependentCount / MAX_DEPENDENTS);
    }

    /**
     * Returns a human-readable assessment of the utility score.
     */
    public String assessUtility(double utilityScore) {
        if (utilityScore >= 0.8) {
            return "CRITICAL";
        } else if (utilityScore >= 0.6) {
            return "HIGH";
        } else if (utilityScore >= 0.4) {
            return "MEDIUM";
        } else if (utilityScore >= 0.2) {
            return "LOW";
        } else {
            return "NEGLIGIBLE";
        }
    }
}

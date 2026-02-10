package com.cloudnative.retirement.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

/**
 * Manages microservice retirement configuration using AWS Systems Manager Parameter Store.
 */
public class ParameterStoreConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ParameterStoreConfigManager.class);

    private final AWSServiceConfig config;
    private final SsmClient ssmClient;

    public ParameterStoreConfigManager(AWSServiceConfig config) {
        this.config = config;
        this.ssmClient = SsmClient.builder()
                .region(config.getRegion())
                .build();
    }

    /**
     * Get a configuration parameter from Parameter Store.
     */
    public String getParameter(String parameterName) {
        if (!config.isAwsEnabled()) {
            logger.debug("AWS disabled, skipping Parameter Store lookup");
            return null;
        }

        try {
            String fullParameterName = config.getParameterStorePrefix() + parameterName;
            
            GetParameterRequest request = GetParameterRequest.builder()
                    .name(fullParameterName)
                    .withDecryption(true)
                    .build();

            GetParameterResponse response = ssmClient.getParameter(request);
            String value = response.parameter().value();
            logger.debug("Retrieved parameter from Parameter Store: {}", parameterName);
            return value;
        } catch (Exception e) {
            logger.warn("Failed to retrieve parameter from Parameter Store: {}", parameterName, e);
            return null;
        }
    }

    /**
     * Get utility threshold from Parameter Store.
     */
    public double getUtilityThreshold() {
        String value = getParameter("utility-threshold");
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid utility threshold value: {}", value);
            }
        }
        return 0.3; // Default threshold
    }

    /**
     * Get retirement decision window from Parameter Store.
     */
    public int getRetirementDecisionWindowDays() {
        String value = getParameter("retirement-window-days");
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid retirement window value: {}", value);
            }
        }
        return 7; // Default 7 days
    }

    /**
     * Get maximum dependency threshold from Parameter Store.
     */
    public int getMaxDependencyThreshold() {
        String value = getParameter("max-dependency-threshold");
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid max dependency threshold: {}", value);
            }
        }
        return 5; // Default 5 dependents
    }

    /**
     * Check if autonomous retirement is enabled.
     */
    public boolean isAutonomousRetirementEnabled() {
        String value = getParameter("autonomous-retirement-enabled");
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return true; // Default enabled
    }

    /**
     * Close the SSM client.
     */
    public void close() {
        if (ssmClient != null) {
            ssmClient.close();
        }
    }
}

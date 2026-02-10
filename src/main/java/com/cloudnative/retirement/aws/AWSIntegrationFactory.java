package com.cloudnative.retirement.aws;

import com.cloudnative.retirement.model.RetirementEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Unified AWS integration facade combining all AWS services.
 */
public class AWSIntegrationFactory {
    private static final Logger logger = LoggerFactory.getLogger(AWSIntegrationFactory.class);

    private final AWSServiceConfig config;
    private final CloudWatchMetricsPublisher metricsPublisher;
    private final DynamoDBDecisionStore decisionStore;
    private final SNSEventPublisher eventPublisher;
    private final S3LogArchiver logArchiver;
    private final ParameterStoreConfigManager configManager;

    public AWSIntegrationFactory(AWSServiceConfig config) {
        this.config = config;
        
        if (config.isAwsEnabled()) {
            logger.info("Initializing AWS Integration...");
            this.metricsPublisher = new CloudWatchMetricsPublisher(config);
            this.decisionStore = new DynamoDBDecisionStore(config);
            this.eventPublisher = new SNSEventPublisher(config);
            this.logArchiver = new S3LogArchiver(config);
            this.configManager = new ParameterStoreConfigManager(config);
            logger.info("AWS Integration initialized successfully");
        } else {
            logger.info("AWS Integration disabled");
            this.metricsPublisher = null;
            this.decisionStore = null;
            this.eventPublisher = null;
            this.logArchiver = null;
            this.configManager = null;
        }
    }

    /**
     * Process a retirement event with all AWS integrations.
     */
    public void processRetirementEvent(RetirementEvent event) {
        if (!config.isAwsEnabled()) {
            return;
        }

        try {
            // Store decision in DynamoDB
            if (decisionStore != null) {
                decisionStore.storeRetirementDecision(event);
            }

            // Publish metrics to CloudWatch
            if (metricsPublisher != null) {
                metricsPublisher.publishUtilityScore(event.getServiceId(), event.getUtilityScore());
                metricsPublisher.publishRetirementDecision(event.getServiceId(), 
                        "RETIRE".equals(event.getDecision()), event.getReason());
                metricsPublisher.publishDependencyCount(event.getServiceId(), event.getDependencyCount());
                metricsPublisher.publishCpuFreed(event.getServiceId(), event.getCpuFreed());
                metricsPublisher.flush();
            }

            // Publish event to SNS
            if (eventPublisher != null) {
                eventPublisher.publishRetirementEvent(event);
            }

            logger.info("Processed retirement event for service: {} with AWS integrations", 
                    event.getServiceId());
        } catch (Exception e) {
            logger.error("Error processing retirement event with AWS integrations", e);
        }
    }

    /**
     * Archive logs to S3.
     */
    public void archiveLogsToS3(Path logFilePath, String logFileName) {
        if (logArchiver != null) {
            logArchiver.archiveLogFile(logFilePath, logFileName);
        }
    }

    /**
     * Archive evidence report to S3.
     */
    public void archiveEvidenceReportToS3(Path reportPath, String reportName) {
        if (logArchiver != null) {
            logArchiver.archiveEvidenceReport(reportPath, reportName);
        }
    }

    /**
     * Publish summary report to SNS.
     */
    public void publishSummaryReport(int totalServices, int retiredCount, double totalCpuFreed) {
        if (eventPublisher != null) {
            eventPublisher.publishRetirementSummary(totalServices, retiredCount, totalCpuFreed);
        }
    }

    /**
     * Get configuration from Parameter Store.
     */
    public double getUtilityThreshold() {
        return configManager != null ? configManager.getUtilityThreshold() : 0.3;
    }

    public int getRetirementWindowDays() {
        return configManager != null ? configManager.getRetirementDecisionWindowDays() : 7;
    }

    public int getMaxDependencyThreshold() {
        return configManager != null ? configManager.getMaxDependencyThreshold() : 5;
    }

    public boolean isAutonomousRetirementEnabled() {
        return configManager != null ? configManager.isAutonomousRetirementEnabled() : true;
    }

    /**
     * Shutdown all AWS services.
     */
    public void shutdown() {
        try {
            if (metricsPublisher != null) metricsPublisher.close();
            if (decisionStore != null) decisionStore.close();
            if (eventPublisher != null) eventPublisher.close();
            if (logArchiver != null) logArchiver.close();
            if (configManager != null) configManager.close();
            logger.info("AWS Integration shut down successfully");
        } catch (Exception e) {
            logger.error("Error shutting down AWS Integration", e);
        }
    }

    // Getters
    public CloudWatchMetricsPublisher getMetricsPublisher() {
        return metricsPublisher;
    }

    public DynamoDBDecisionStore getDecisionStore() {
        return decisionStore;
    }

    public SNSEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public S3LogArchiver getLogArchiver() {
        return logArchiver;
    }

    public ParameterStoreConfigManager getConfigManager() {
        return configManager;
    }

    public AWSServiceConfig getConfig() {
        return config;
    }

    public boolean isAwsEnabled() {
        return config.isAwsEnabled();
    }
}

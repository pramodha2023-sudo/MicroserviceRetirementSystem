package com.cloudnative.retirement.aws;

import com.cloudnative.retirement.model.RetirementEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Example usage of AWS integration with the Microservice Retirement System.
 * This class demonstrates how to integrate AWS services into your retirement workflow.
 */
public class AWSIntegrationExample {
    private static final Logger logger = LoggerFactory.getLogger(AWSIntegrationExample.class);

    /**
     * Example 1: Basic retirement event processing with AWS
     */
    public static void exampleBasicRetirementProcessing() {
        logger.info("=== Example 1: Basic Retirement Event Processing ===");
        
        // Initialize AWS configuration
        AWSServiceConfig config = new AWSServiceConfig();
        
        // Create integration factory
        AWSIntegrationFactory awsFactory = new AWSIntegrationFactory(config);
        
        // Create and process retirement events
        RetirementEvent[] events = {
            new RetirementEvent("user-service-v1", 0.25, 3, "RETIRE", 15.5, "Low utility score"),
            new RetirementEvent("payment-service-v2", 0.85, 8, "RETAIN", 0.0, "High criticality"),
            new RetirementEvent("email-notification-v3", 0.15, 1, "RETIRE", 8.2, "Redundant service")
        };
        
        for (RetirementEvent event : events) {
            awsFactory.processRetirementEvent(event);
        }
        
        // Publish summary report
        awsFactory.publishSummaryReport(3, 2, 23.7);
        
        // Cleanup
        awsFactory.shutdown();
    }

    /**
     * Example 2: Archiving logs and reports to S3
     */
    public static void exampleS3Archival() {
        logger.info("=== Example 2: S3 Log and Report Archival ===");
        
        AWSServiceConfig config = new AWSServiceConfig();
        AWSIntegrationFactory awsFactory = new AWSIntegrationFactory(config);
        
        // Archive log files
        Path logPath = Paths.get("logs/retirement-2026-02-09.log");
        awsFactory.archiveLogsToS3(logPath, "retirement-2026-02-09.log");
        
        // Archive evidence reports
        Path reportPath = Paths.get("reports/evidence-report.csv");
        awsFactory.archiveEvidenceReportToS3(reportPath, "evidence-report-2026-02-09.csv");
        
        logger.info("Logs and reports archived to S3");
        awsFactory.shutdown();
    }

    /**
     * Example 3: Retrieving configuration from AWS Parameter Store
     */
    public static void exampleParameterStoreConfiguration() {
        logger.info("=== Example 3: AWS Parameter Store Configuration ===");
        
        AWSServiceConfig config = new AWSServiceConfig();
        AWSIntegrationFactory awsFactory = new AWSIntegrationFactory(config);
        
        // Retrieve configuration values
        double utilityThreshold = awsFactory.getUtilityThreshold();
        int retirementDays = awsFactory.getRetirementWindowDays();
        int maxDependencies = awsFactory.getMaxDependencyThreshold();
        boolean autonomousEnabled = awsFactory.isAutonomousRetirementEnabled();
        
        logger.info("Configuration from Parameter Store:");
        logger.info("  Utility Threshold: {}", utilityThreshold);
        logger.info("  Retirement Window: {} days", retirementDays);
        logger.info("  Max Dependencies: {}", maxDependencies);
        logger.info("  Autonomous Retirement: {}", autonomousEnabled);
        
        awsFactory.shutdown();
    }

    /**
     * Example 4: Custom AWS region and configuration
     */
    public static void exampleCustomConfiguration() {
        logger.info("=== Example 4: Custom AWS Configuration ===");
        
        // Create config for EU region
        AWSServiceConfig config = new AWSServiceConfig(
            software.amazon.awssdk.regions.Region.EU_WEST_1,
            "retirement-decisions-eu",
            "arn:aws:sns:eu-west-1:123456789:microservice-retirement-events",
            "retirement-archive-eu",
            true
        );
        
        AWSIntegrationFactory awsFactory = new AWSIntegrationFactory(config);
        
        // Process events with EU region configuration
        RetirementEvent event = new RetirementEvent(
            "eu-user-service", 0.30, 2, "RETIRE", 12.0, "Replaced by v2"
        );
        awsFactory.processRetirementEvent(event);
        
        awsFactory.shutdown();
    }

    /**
     * Example 5: Batch processing multiple retirement events
     */
    public static void exampleBatchRetirementProcessing() {
        logger.info("=== Example 5: Batch Retirement Event Processing ===");
        
        AWSServiceConfig config = new AWSServiceConfig();
        AWSIntegrationFactory awsFactory = new AWSIntegrationFactory(config);
        
        // Simulate batch processing of retirement decisions
        List<RetirementEvent> retirementEvents = new ArrayList<>();
        
        // Generate simulated retirement events
        String[] services = {
            "user-service", "payment-service", "notification-service",
            "analytics-service", "audit-service", "cache-service"
        };
        
        double[] utilityScores = { 0.25, 0.85, 0.15, 0.45, 0.55, 0.20 };
        int[] dependencies = { 3, 8, 1, 5, 4, 2 };
        String[] decisions = { "RETIRE", "RETAIN", "RETIRE", "RETAIN", "RETAIN", "RETIRE" };
        double[] cpuFreed = { 15.5, 0.0, 8.2, 0.0, 0.0, 12.0 };
        String[] reasons = {
            "Low utility score",
            "High criticality",
            "Redundant service",
            "Performance critical",
            "Audit requirement",
            "Deprecated version"
        };
        
        for (int i = 0; i < services.length; i++) {
            retirementEvents.add(new RetirementEvent(
                services[i] + "-v1",
                utilityScores[i],
                dependencies[i],
                decisions[i],
                cpuFreed[i],
                reasons[i]
            ));
        }
        
        // Process all events
        int retiredCount = 0;
        double totalCpuFreed = 0;
        
        for (RetirementEvent event : retirementEvents) {
            awsFactory.processRetirementEvent(event);
            if ("RETIRE".equals(event.getDecision())) {
                retiredCount++;
                totalCpuFreed += event.getCpuFreed();
            }
        }
        
        // Publish summary
        awsFactory.publishSummaryReport(services.length, retiredCount, totalCpuFreed);
        
        logger.info("Batch processing complete - {} services retired, {:.1f}% CPU freed", 
                    retiredCount, totalCpuFreed);
        
        awsFactory.shutdown();
    }

    /**
     * Example 6: Direct access to individual AWS services
     */
    public static void exampleDirectServiceAccess() {
        logger.info("=== Example 6: Direct Service Access ===");
        
        AWSServiceConfig config = new AWSServiceConfig();
        AWSIntegrationFactory awsFactory = new AWSIntegrationFactory(config);
        
        if (awsFactory.isAwsEnabled()) {
            // Access individual services directly
            CloudWatchMetricsPublisher metrics = awsFactory.getMetricsPublisher();
            DynamoDBDecisionStore store = awsFactory.getDecisionStore();
            SNSEventPublisher snsPub = awsFactory.getEventPublisher();
            
            // Direct metric publishing
            if (metrics != null) {
                metrics.publishUtilityScore("custom-service", 0.5);
                metrics.publishCpuFreed("custom-service", 10.0);
                metrics.flush();
            }
            
            // Direct database query
            if (store != null) {
                store.queryDecisionsForService("user-service-v1");
            }
            
            logger.info("Direct service access completed");
        }
        
        awsFactory.shutdown();
    }

    /**
     * Main method for running examples
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            logger.info("No example specified. Running all examples...");
            // Only run safe examples by default
            exampleParameterStoreConfiguration();
            System.out.println("\n");
            exampleBatchRetirementProcessing();
        } else {
            switch (args[0]) {
                case "1":
                    exampleBasicRetirementProcessing();
                    break;
                case "2":
                    exampleS3Archival();
                    break;
                case "3":
                    exampleParameterStoreConfiguration();
                    break;
                case "4":
                    exampleCustomConfiguration();
                    break;
                case "5":
                    exampleBatchRetirementProcessing();
                    break;
                case "6":
                    exampleDirectServiceAccess();
                    break;
                default:
                    logger.error("Unknown example: {}", args[0]);
                    logger.info("Usage: AWSIntegrationExample [1-6]");
            }
        }
    }
}

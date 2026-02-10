package com.cloudnative.retirement.aws;

import com.cloudnative.retirement.model.RetirementEvent;

/**
 * Quick local test - no AWS credentials required
 */
public class LocalTest {
    public static void main(String[] args) {
        System.out.println("=== Microservice Retirement System - Local Test ===\n");
        
        // Disable AWS integration for local testing
        System.setProperty("AWS_ENABLED", "false");
        
        // Initialize
        AWSServiceConfig config = new AWSServiceConfig();
        AWSIntegrationFactory factory = new AWSIntegrationFactory(config);
        
        System.out.println("AWS Enabled: " + factory.isAwsEnabled());
        System.out.println("Region: " + config.getRegion());
        System.out.println("DynamoDB Table: " + config.getDynamoDBTableName());
        System.out.println("\n--- Processing Retirement Events (AWS Disabled) ---\n");
        
        // Create test retirement events
        RetirementEvent[] events = {
            new RetirementEvent("user-service-v1", 0.25, 3, "RETIRE", 15.5, "Low utility"),
            new RetirementEvent("payment-service-v2", 0.85, 8, "RETAIN", 0.0, "Critical"),
            new RetirementEvent("email-service-v3", 0.15, 1, "RETIRE", 8.2, "Deprecated"),
            new RetirementEvent("cache-service-v4", 0.65, 5, "RETAIN", 0.0, "High performance"),
            new RetirementEvent("audit-service-v2", 0.45, 2, "RETAIN", 0.0, "Compliance")
        };
        
        int retiredCount = 0;
        double totalCpuFreed = 0;
        
        // Process each event
        for (RetirementEvent event : events) {
            System.out.printf("Processing: %s - Utility: %.2f - Decision: %s%n", 
                event.getServiceId(), 
                event.getUtilityScore(), 
                event.getDecision());
            
            factory.processRetirementEvent(event);
            
            if ("RETIRE".equals(event.getDecision())) {
                retiredCount++;
                totalCpuFreed += event.getCpuFreed();
            }
        }
        
        System.out.println("\n--- Summary ---");
        System.out.printf("Total Services: %d%n", events.length);
        System.out.printf("Retired: %d%n", retiredCount);
        System.out.printf("Total CPU Freed: %.1f%%%n", totalCpuFreed);
        System.out.printf("Efficiency Rate: %.1f%%%n", (retiredCount * 100.0 / events.length));
        
        factory.shutdown();
        System.out.println("\n✓ Local test completed successfully!");
    }
}

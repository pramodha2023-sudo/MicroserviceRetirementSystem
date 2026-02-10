package com.cloudnative.retirement.aws;

import com.cloudnative.retirement.model.RetirementEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for AWS integration (AWS disabled for testing)
 */
public class AWSIntegrationTest {
    
    private AWSServiceConfig config;
    private AWSIntegrationFactory factory;
    
    @Before
    public void setUp() {
        // Use explicit constructor to disable AWS for testing (bypasses environment variables)
        software.amazon.awssdk.regions.Region region = software.amazon.awssdk.regions.Region.US_EAST_1;
        config = new AWSServiceConfig(region, "retirement-decisions", "", "", false);
        factory = new AWSIntegrationFactory(config);
    }
    
    @Test
    public void testConfigInitialization() {
        assertNotNull(config);
        assertEquals("retirement-decisions", config.getDynamoDBTableName());
        assertFalse(config.isAwsEnabled());
    }
    
    @Test
    public void testAWSIntegrationFactoryCreation() {
        assertNotNull(factory);
        assertFalse(factory.isAwsEnabled());
    }
    
    @Test
    public void testRetirementEventProcessing() {
        RetirementEvent event = new RetirementEvent(
            "test-service",
            0.5,
            2,
            "RETIRE",
            10.0,
            "Test reason"
        );
        
        assertNotNull(event);
        assertEquals("test-service", event.getServiceId());
        assertEquals(0.5, event.getUtilityScore(), 0.01);
        assertEquals("RETIRE", event.getDecision());
    }
    
    @Test
    public void testConfigurationRetrieval() {
        // These should return defaults when AWS is disabled
        double threshold = factory.getUtilityThreshold();
        int windowDays = factory.getRetirementWindowDays();
        int maxDeps = factory.getMaxDependencyThreshold();
        
        assertTrue(threshold > 0);
        assertTrue(windowDays > 0);
        assertTrue(maxDeps > 0);
    }
    
    @Test
    public void testFactoryShutdown() {
        // Should not throw exception
        factory.shutdown();
        assertTrue(true);
    }
}

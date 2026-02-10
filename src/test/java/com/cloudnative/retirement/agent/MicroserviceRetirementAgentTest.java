package com.cloudnative.retirement.agent;

import com.cloudnative.retirement.model.Microservice;
import com.cloudnative.retirement.model.RetirementEvent;
import com.cloudnative.retirement.modules.DependencyAwarenessModule;
import com.cloudnative.retirement.modules.LifecycleLearningEngine;
import com.cloudnative.retirement.modules.UtilityAssessmentModule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MicroserviceRetirementAgentTest {
    private Microservice service;
    private UtilityAssessmentModule utilityModule;
    private LifecycleLearningEngine learningEngine;
    private DependencyAwarenessModule dependencyModule;
    private MicroserviceRetirementAgent agent;

    @Before
    public void setUp() {
        service = new Microservice("S1", "TestService");
        utilityModule = new UtilityAssessmentModule();
        learningEngine = new LifecycleLearningEngine(10);
        dependencyModule = new DependencyAwarenessModule();

        agent = new MicroserviceRetirementAgent(
            service,
            utilityModule,
            learningEngine,
            dependencyModule,
            0.3,
            3
        );
    }

    @Test
    public void testAgentInitialization() {
        assertNotNull(agent);
        assertFalse(service.isRetired());
        assertEquals("S1", service.getServiceId());
    }

    @Test
    public void testLowUtilityDetection() {
        // Simulate low utility
        service.setRequestCount(10);
        service.setUtilizationRate(0.05);
        service.setSlaContribution(0.1);

        RetirementEvent event = agent.evaluateRetirement();
        assertNotNull(event);
        assertEquals("RETAIN", event.getDecision()); // First low-utility cycle
    }

    @Test
    public void testDependencyPrevention() {
        // Add critical dependencies
        dependencyModule.registerDependency("S2", "S1");
        dependencyModule.registerDependency("S3", "S1");

        // Simulate very low utility
        service.setRequestCount(5);
        service.setUtilizationRate(0.01);
        service.setSlaContribution(0.05);

        // Multiple evaluations to reach threshold
        for (int i = 0; i < 10; i++) {
            agent.evaluateRetirement();
        }

        // Should NOT retire due to dependencies
        assertFalse(service.isRetired());
    }

    @Test
    public void testUtilityAssessment() {
        service.setRequestCount(500);
        service.setSlaContribution(0.8);
        service.setDependentServiceCount(3);

        double utility = utilityModule.computeUtility(service);
        assertTrue(utility >= 0.0 && utility <= 1.0);
        assertTrue(utility > 0.4); // Should have reasonable utility
    }

    @Test
    public void testSafeRetirement() {
        // No dependencies
        service.setRequestCount(10);
        service.setUtilizationRate(0.05);
        service.setSlaContribution(0.1);

        // Trigger retirement
        for (int i = 0; i < 5; i++) {
            agent.evaluateRetirement();
        }

        assertTrue(service.isRetired());
    }
}

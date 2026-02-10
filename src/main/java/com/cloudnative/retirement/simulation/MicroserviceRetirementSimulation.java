package com.cloudnative.retirement.simulation;

import com.cloudnative.retirement.agent.MicroserviceRetirementAgent;
import com.cloudnative.retirement.model.Microservice;
import com.cloudnative.retirement.model.RetirementEvent;
import com.cloudnative.retirement.modules.*;
import com.cloudnative.retirement.logging.EvidenceLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Microservice Retirement Simulation Framework
 * Simulates a cloud-native environment with multiple microservices,
 * each with autonomous retirement agents making independent decisions.
 */
public class MicroserviceRetirementSimulation {
    private static final Logger logger = LoggerFactory.getLogger(MicroserviceRetirementSimulation.class);

    private final List<Microservice> services;
    private final List<MicroserviceRetirementAgent> agents;
    private final UtilityAssessmentModule utilityModule;
    private final LifecycleLearningEngine learningEngine;
    private final DependencyAwarenessModule dependencyModule;
    private final EvidenceLogger logger_evidence;

    private int currentCycle;
    private final int totalCycles;
    private final Random random;

    public MicroserviceRetirementSimulation(int numServices, int totalCycles, String logDirectory) throws java.io.IOException {
        this.services = new CopyOnWriteArrayList<>();
        this.agents = new CopyOnWriteArrayList<>();
        this.totalCycles = totalCycles;
        this.currentCycle = 0;
        this.random = new Random(42); // Fixed seed for reproducibility

        // Initialize modules
        this.utilityModule = new UtilityAssessmentModule();
        this.learningEngine = new LifecycleLearningEngine(20);
        this.dependencyModule = new DependencyAwarenessModule();
        this.logger_evidence = new EvidenceLogger(logDirectory);

        // Initialize microservices
        initializeServices(numServices);
        logger.info("Simulation initialized with {} services and {} cycles", numServices, totalCycles);
    }

    /**
     * Initializes microservices with agents and random dependencies.
     */
    private void initializeServices(int numServices) {
        for (int i = 0; i < numServices; i++) {
            String serviceId = "S" + (i + 1);
            String serviceName = "Service-" + (i + 1);
            
            Microservice service = new Microservice(serviceId, serviceName);
            services.add(service);

            // Create retirement agent for this service
            double utilityThreshold = 0.25 + random.nextDouble() * 0.2; // 0.25-0.45
            int retentionWindow = 5 + random.nextInt(5); // 5-10 cycles
            
            MicroserviceRetirementAgent agent = new MicroserviceRetirementAgent(
                service,
                utilityModule,
                learningEngine,
                dependencyModule,
                utilityThreshold,
                retentionWindow
            );

            agents.add(agent);
            logger.debug("Initialized agent for {}", serviceId);
        }

        // Create random dependencies
        for (int i = 1; i < services.size(); i++) {
            if (random.nextDouble() < 0.4) { // 40% chance of dependency
                String provider = services.get(random.nextInt(i)).getServiceId();
                String dependent = services.get(i).getServiceId();
                dependencyModule.registerDependency(dependent, provider);
            }
        }

        logger.info("Service initialization complete: {}", dependencyModule.getDependencyStats());
    }

    /**
     * Runs the simulation for the specified number of cycles.
     */
    public void run() {
        logger.info("Starting simulation - {} cycles", totalCycles);

        for (currentCycle = 0; currentCycle < totalCycles; currentCycle++) {
            simulateCycle();
            
            if (currentCycle % 10 == 0) {
                logger.info("Simulation cycle {}/{} - Active services: {}, Retired services: {}",
                           currentCycle, totalCycles, getActiveServiceCount(), getRetiredServiceCount());
            }
        }

        finalizeSim();
    }

    /**
     * Simulates a single time cycle.
     */
    private void simulateCycle() {
        // Step 1: Update service metrics (simulated workload)
        for (Microservice service : services) {
            if (!service.isRetired()) {
                updateServiceMetrics(service);
            }
        }

        // Step 2: Run retirement evaluation for each agent
        for (MicroserviceRetirementAgent agent : agents) {
            if (!agent.getMicroservice().isRetired()) {
                RetirementEvent event = agent.evaluateRetirement();
                if (event != null) {
                    logger_evidence.recordEvent(event);
                    
                    // Record utility in learning engine
                    learningEngine.recordUtility(agent.getMicroservice().getServiceId(), event.getUtilityScore());
                    
                    // Clean up dependencies if retired
                    if ("RETIRE".equals(event.getDecision())) {
                        dependencyModule.clearDependenciesForRetiredService(agent.getMicroservice().getServiceId());
                    }
                }
            }
        }
    }

    /**
     * Simulates workload changes and metric updates for a service.
     */
    private void updateServiceMetrics(Microservice service) {
        // Simulate feature churn and workload decay
        int age = (int)(java.time.temporal.ChronoUnit.SECONDS
                       .between(service.getCreatedAt(), java.time.LocalDateTime.now()) / 60);
        
        // Request count decays with age
        int baseRequests = 800;
        double decayFactor = Math.pow(0.97, age / 100.0); // Decays over time
        int requests = (int)(baseRequests * decayFactor * (0.5 + random.nextDouble()));
        service.setRequestCount(requests);

        // Utilization correlates with requests
        double utilization = requests / 1000.0;
        service.setUtilizationRate(utilization);

        // SLA contribution can vary
        double slaDrift = 0.1 * (random.nextDouble() - 0.5); // +/- 5%
        service.setSlaContribution(Math.max(0, service.getSlaContribution() + slaDrift));
    }

    /**
     * Finalizes simulation and generates reports.
     */
    private void finalizeSim() {
        logger.info("Simulation complete after {} cycles", totalCycles);
        
        // Log summary
        logger.info(logger_evidence.generateSummaryReport());
        logger_evidence.logDetailedStatistics();

        // Export CSV
        String csvFilename = "retirement_events_" + System.currentTimeMillis() + ".csv";
        logger_evidence.exportToCSV(csvFilename);
        logger.info("Results exported to: {}", csvFilename);
    }

    /**
     * Gets the count of active services.
     */
    public int getActiveServiceCount() {
        return (int) services.stream().filter(s -> !s.isRetired()).count();
    }

    /**
     * Gets the count of retired services.
     */
    public int getRetiredServiceCount() {
        return (int) services.stream().filter(Microservice::isRetired).count();
    }

    /**
     * Gets simulation progress as percentage.
     */
    public double getProgress() {
        return (currentCycle * 100.0) / totalCycles;
    }

    /**
     * Gets performance metrics.
     */
    public SimulationMetrics getMetrics() {
        return new SimulationMetrics(
            services.size(),
            getActiveServiceCount(),
            getRetiredServiceCount(),
            logger_evidence.getTotalCpuFreed(),
            logger_evidence.getDecisionCount("RETIRE"),
            logger_evidence.getDecisionCount("RETAIN")
        );
    }

    /**
     * Inner class for simulation metrics.
     */
    public static class SimulationMetrics {
        public final int totalServices;
        public final int activeServices;
        public final int retiredServices;
        public final double cpuFreed;
        public final int totalRetirements;
        public final int totalRetentions;

        SimulationMetrics(int total, int active, int retired, double cpu, int retirements, int retentions) {
            this.totalServices = total;
            this.activeServices = active;
            this.retiredServices = retired;
            this.cpuFreed = cpu;
            this.totalRetirements = retirements;
            this.totalRetentions = retentions;
        }

        @Override
        public String toString() {
            return String.format(
                "Metrics{total:%d, active:%d, retired:%d, cpuFreed:%.2f, retirements:%d, retentions:%d}",
                totalServices, activeServices, retiredServices, cpuFreed, totalRetirements, totalRetentions
            );
        }
    }
}

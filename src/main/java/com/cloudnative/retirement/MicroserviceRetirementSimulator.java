package com.cloudnative.retirement;

import com.cloudnative.retirement.simulation.MicroserviceRetirementSimulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Microservice Retirement Simulation.
 * Demonstrates the Agent-Driven Microservice Retirement pattern in action.
 */
public class MicroserviceRetirementSimulator {
    private static final Logger logger = LoggerFactory.getLogger(MicroserviceRetirementSimulator.class);

    public static void main(String[] args) {
        logger.info(repeatString("=", 80));
        logger.info("Agent-Driven Microservice Retirement System");
        logger.info("Autonomous lifecycle management with self-deletion logic");
        logger.info(repeatString("=", 80));

        try {
            // Configuration
            int numServices = 12;
            int totalCycles = 40;
            String logDirectory = "./retirement_logs";

            logger.info("Configuration:");
            logger.info("  - Number of Microservices: {}", numServices);
            logger.info("  - Simulation Cycles: {}", totalCycles);
            logger.info("  - Log Directory: {}", logDirectory);
            logger.info("");

            // Initialize and run simulation
            long startTime = System.currentTimeMillis();
            MicroserviceRetirementSimulation simulation = new MicroserviceRetirementSimulation(
                numServices,
                totalCycles,
                logDirectory
            );

            simulation.run();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Print final metrics
            MicroserviceRetirementSimulation.SimulationMetrics metrics = simulation.getMetrics();
            logger.info("");
            logger.info("Simulation Execution Complete");
            logger.info("Duration: {} ms ({} seconds)", duration, duration / 1000.0);
            logger.info("Final Metrics: {}", metrics);
            logger.info("");
            logger.info("Performance Summary:");
            logger.info("  - Resource Reclamation Efficiency: {:.1f}% CPU freed from {} retirements",
                       (metrics.cpuFreed / (metrics.totalServices * 20.0)) * 100,
                       metrics.totalRetirements);
            logger.info("  - Service Sprawl Reduction: {:.1f}% of services retired",
                       (metrics.retiredServices * 100.0) / metrics.totalServices);
            logger.info("  - Active Service Count: {}/{}", metrics.activeServices, metrics.totalServices);
            logger.info("");
            logger.info(repeatString("=", 80));
            logger.info("Simulation Results: SUCCESS");
            logger.info("CSV Report generated in: {}/retirement_events_*.csv", logDirectory);
            logger.info(repeatString("=", 80));

        } catch (Exception e) {
            logger.error("Simulation failed with error", e);
            System.exit(1);
        }
    }

    private static String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}

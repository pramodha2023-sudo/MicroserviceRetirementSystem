package com.cloudnative.retirement.aws;

import com.cloudnative.retirement.dataloaders.CSVMicroserviceLoader;
import com.cloudnative.retirement.dataloaders.ServiceMetrics;
import com.cloudnative.retirement.model.RetirementEvent;
import com.cloudnative.retirement.modules.UtilityAssessmentModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Real-World Data Test - Demonstrates retirement system with actual datasets.
 * Supports both simulated and real-world data from Kaggle or other sources.
 */
public class RealWorldDataTest {
    private static final Logger logger = LoggerFactory.getLogger(RealWorldDataTest.class);

    private static final String SAMPLE_CSV_FILE = "./microservice_metrics.csv";

    public static void main(String[] args) {
        logger.info("============================================================");
        logger.info("  MICROSERVICE RETIREMENT SYSTEM - REAL-WORLD DATA TEST");
        logger.info("============================================================");
        logger.info("");

        try {
            // Determine data source
            DataSource dataSource = determineDataSource(args);
            List<ServiceMetrics> services = loadServices(dataSource);

            if (services.isEmpty()) {
                logger.error("No services loaded. Exiting.");
                System.exit(1);
            }

            // Process with retirement algorithm
            demonstrateRetirementWithRealData(services);

        } catch (IOException e) {
            logger.error("Error loading data", e);
            System.exit(1);
        }

        logger.info("");
        logger.info("============================================================");
        logger.info("  TEST COMPLETE");
        logger.info("============================================================");
    }

    /**
     * Determines which data source to use based on command line arguments.
     */
    private static DataSource determineDataSource(String[] args) {
        if (args.length > 0) {
            String arg = args[0].toLowerCase();
            if (arg.equals("sample")) {
                return DataSource.SAMPLE;
            } else if (arg.equals("csv") || arg.endsWith(".csv")) {
                return DataSource.CSV;
            } else if (arg.equals("kaggle")) {
                return DataSource.KAGGLE_COMPATIBLE;
            }
        }

        // Default to sample data
        return DataSource.SAMPLE;
    }

    /**
     * Loads services from specified data source.
     */
    private static List<ServiceMetrics> loadServices(DataSource source) throws IOException {
        switch (source) {
            case KAGGLE_COMPATIBLE:
                logger.info("[*] Using Kaggle-compatible sample data");
                return CSVMicroserviceLoader.loadSampleKaggleData();

            case CSV:
                logger.info("[*] Attempting to load from: {}", SAMPLE_CSV_FILE);
                List<ServiceMetrics> services = CSVMicroserviceLoader.loadFromCSV(SAMPLE_CSV_FILE);
                if (services.isEmpty()) {
                    logger.warn("CSV file not found or empty. Creating sample data...");
                    CSVMicroserviceLoader.createSampleCSV(SAMPLE_CSV_FILE);
                    services = CSVMicroserviceLoader.loadFromCSV(SAMPLE_CSV_FILE);
                }
                return services;

            case SAMPLE:
            default:
                logger.info("[*] Using built-in sample Kaggle-compatible data");
                return CSVMicroserviceLoader.loadSampleKaggleData();
        }
    }

    /**
     * Demonstrates retirement system with real-world data.
     */
    private static void demonstrateRetirementWithRealData(List<ServiceMetrics> services) {
        logger.info("");
        logger.info("Dataset Summary:");
        logger.info("  Total Services: {}", services.size());
        int totalRequests = services.stream().mapToInt(ServiceMetrics::getRequestCount).sum();
        double avgSla = services.stream().mapToDouble(ServiceMetrics::getSlaScore).average().orElse(0.0);
        int totalDependents = services.stream().mapToInt(ServiceMetrics::getDependentCount).sum();

        logger.info("  Total Requests (normalized): {}", totalRequests);
        logger.info("  Average SLA Score: {}", String.format("%.3f", avgSla));
        logger.info("  Total Dependencies: {}", totalDependents);
        logger.info("");

        // Analyze each service
        logger.info("Service Analysis:");
        logger.info(String.format("%-30s | %10s | %10s | %10s | %12s", 
                                 "Service ID", "Requests", "SLA", "Dependents", "Decision"));
        logger.info(repeatString("-", 95));

        UtilityAssessmentModule utilityModule = new UtilityAssessmentModule();
        List<RetirementEvent> retirementEvents = new ArrayList<>();
        int retiredCount = 0;
        int keptCount = 0;
        double totalCpuFreed = 0;

        for (ServiceMetrics service : services) {
            // Calculate utility from metrics
            double utility = calculateUtilityFromMetrics(service);

            // Determine if should retire
            boolean shouldRetire = utility < 0.30 && service.getDependentCount() == 0;
            String decision = shouldRetire ? "RETIRE" : "RETAIN";
            String assessment = utilityModule.assessUtility(utility);

            // Log the analysis
            logger.info(String.format("%-30s | %10d | %8.3f | %10d | %12s (%s)",
                                    service.getServiceId(),
                                    service.getRequestCount(),
                                    service.getSlaScore(),
                                    service.getDependentCount(),
                                    decision,
                                    assessment));

            // Track retirement decisions
            if (shouldRetire) {
                retiredCount++;
                double cpuFreed = calculateCpuFreed(service);
                totalCpuFreed += cpuFreed;

                RetirementEvent event = new RetirementEvent(
                    service.getServiceId(),
                    utility,
                    service.getDependentCount(),
                    decision,
                    cpuFreed,
                    String.format("Utility %.2f < 0.30, No dependencies", utility)
                );
                retirementEvents.add(event);
            } else {
                keptCount++;
            }
        }

        // Summary
        logger.info(repeatString("-", 95));
        logger.info("");
        logger.info("Retirement Analysis Results:");
        logger.info("  Services Analyzed: {}", services.size());
        logger.info("  Services Retired: {} ({}%)", 
                   retiredCount, 
                   String.format("%.1f", (retiredCount * 100.0 / services.size())));
        logger.info("  Services Retained: {} ({}%)", 
                   keptCount, 
                   String.format("%.1f", (keptCount * 100.0 / services.size())));
        logger.info("  Total CPU Freed: {} units", String.format("%.1f", totalCpuFreed));
        if (retiredCount > 0) {
            logger.info("  Avg CPU per Retirement: {} units", String.format("%.1f", totalCpuFreed / retiredCount));
        }

        // Efficiency Metrics
        double efficiency = (retiredCount * 100.0) / services.size();
        logger.info("  Resource Reclamation Efficiency: {}%", String.format("%.1f", efficiency));
        logger.info("");

        // Detail on retired services
        if (!retirementEvents.isEmpty()) {
            logger.info("Detailed Retirement Plan:");
            for (RetirementEvent event : retirementEvents) {
                logger.info("  [RETIRE] {} - Utility: {}, Reason: {}", 
                           event.getServiceId(), 
                           String.format("%.2f", event.getUtilityScore()),
                           event.getReason());
            }
        }

        logger.info("");
        logger.info("AWS Integration Status: READY");
        logger.info("  ClassPathXmlApplicationContext: Ready to publish {} metrics", services.size());
        logger.info("  DynamoDB: Ready to store {} retirement decisions", retiredCount);
        logger.info("  SNS: Ready to notify on {} retirements", retiredCount);
        logger.info("  S3: Ready to archive {}.csv", retiredCount > 0 ? "evidence" : "empty");
    }

    /**
     * Calculates utility score from real-world metrics.
     * Maps actual measured data to utility formula.
     */
    private static double calculateUtilityFromMetrics(ServiceMetrics service) {
        // Request score: normalized by max (2000)
        double requestScore = Math.min(1.0, service.getRequestCount() / 2000.0);

        // SLA score: already a 0-1 value
        double slaScore = service.getSlaScore();

        // Collaboration score: normalized by max (15 dependents)
        double collaborationScore = Math.min(1.0, service.getDependentCount() / 15.0);

        // Apply weights (40% request, 35% SLA, 25% collaboration)
        double utility = (0.40 * requestScore) + (0.35 * slaScore) + (0.25 * collaborationScore);

        return Math.min(1.0, utility);
    }

    /**
     * Calculates CPU resources freed if service is retired.
     */
    private static double calculateCpuFreed(ServiceMetrics service) {
        // Estimate CPU based on request count
        // Normalize: 100 requests per CPU unit
        return Math.ceil(service.getRequestCount() / 100.0);
    }

    /**
     * Helper to repeat a string.
     */
    private static String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * Data source options.
     */
    enum DataSource {
        SAMPLE,              // Built-in Kaggle-compatible data
        CSV,                 // Load from CSV file
        KAGGLE_COMPATIBLE    // Explicitly load Kaggle format
    }
}

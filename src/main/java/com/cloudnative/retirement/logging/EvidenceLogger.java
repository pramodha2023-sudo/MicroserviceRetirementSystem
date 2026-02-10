package com.cloudnative.retirement.logging;

import com.cloudnative.retirement.model.RetirementEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Evidence Logger
 * Logs retirement decisions, triggers, and system impact.
 * Provides CSV export for validation and metrics analysis.
 */
public class EvidenceLogger {
    private static final Logger logger = LoggerFactory.getLogger(EvidenceLogger.class);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<RetirementEvent> events;
    private final String outputDirectory;

    public EvidenceLogger(String outputDirectory) throws java.io.IOException {
        this.events = new CopyOnWriteArrayList<>();
        this.outputDirectory = outputDirectory;
        
        // Create output directory if it doesn't exist
        java.nio.file.Files.createDirectories(java.nio.file.Paths.get(outputDirectory));
        logger.info("Evidence Logger initialized with output directory: {}", outputDirectory);
    }

    /**
     * Records a retirement event.
     */
    public void recordEvent(RetirementEvent event) {
        events.add(event);
        logger.info("Recorded event: {}", event);
    }

    /**
     * Exports all events to CSV format for metrics analysis.
     * Format: Time,ServiceID,UtilityScore,DependencyCount,RetirementDecision,CPU_Freed
     */
    public void exportToCSV(String filename) {
        String filepath = outputDirectory + java.io.File.separator + filename;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            // Write header
            writer.write("Time,ServiceID,UtilityScore,DependencyCount,RetirementDecision,CPU_Freed,Reason\n");

            // Write events
            for (RetirementEvent event : events) {
                String line = String.format("%s,%s,%.2f,%d,%s,%.2f,\"%s\"\n",
                    TIMESTAMP_FORMATTER.format(event.getTimestamp()),
                    event.getServiceId(),
                    event.getUtilityScore(),
                    event.getDependencyCount(),
                    event.getDecision(),
                    event.getCpuFreed(),
                    event.getReason()
                );
                writer.write(line);
            }

            logger.info("Exported {} events to CSV: {}", events.size(), filepath);
        } catch (IOException e) {
            logger.error("Failed to export CSV", e);
        }
    }

    /**
     * Generates a summary report of all events.
     */
    public String generateSummaryReport() {
        long totalRetirements = events.stream()
                                     .filter(e -> "RETIRE".equals(e.getDecision()))
                                     .count();
        
        long totalRetentions = events.stream()
                                    .filter(e -> "RETAIN".equals(e.getDecision()))
                                    .count();

        double totalCpuFreed = events.stream()
                                    .filter(e -> "RETIRE".equals(e.getDecision()))
                                    .mapToDouble(RetirementEvent::getCpuFreed)
                                    .sum();

        double avgUtilityRetired = events.stream()
                                        .filter(e -> "RETIRE".equals(e.getDecision()))
                                        .mapToDouble(RetirementEvent::getUtilityScore)
                                        .average()
                                        .orElse(0);

        long totalDependenciesManaged = events.stream()
                                             .mapToLong(RetirementEvent::getDependencyCount)
                                             .sum();

        StringBuilder report = new StringBuilder();
        report.append("\n================== MICROSERVICE RETIREMENT SUMMARY ==================\n");
        report.append(String.format("Total Events Logged: %d\n", events.size()));
        report.append(String.format("Total Retirements: %d\n", totalRetirements));
        report.append(String.format("Total Retentions: %d\n", totalRetentions));
        report.append(String.format("CPU Resources Freed: %.2f units\n", totalCpuFreed));
        report.append(String.format("Average Utility Score (Retired Services): %.3f\n", avgUtilityRetired));
        report.append(String.format("Total Dependencies Managed: %d\n", totalDependenciesManaged));
        report.append(String.format("Retirement Rate: %.1f%%\n", 
                                   (totalRetirements * 100.0) / events.size()));
        report.append("========================================================================\n");

        return report.toString();
    }

    /**
     * Logs detailed statistics about retirement decisions.
     */
    public void logDetailedStatistics() {
        logger.info("\n========== DETAILED RETIREMENT STATISTICS ==========");
        
        // Retirements by reason
        Map<String, Long> reasonCounts = new HashMap<>();
        events.stream()
              .filter(e -> "RETIRE".equals(e.getDecision()))
              .forEach(e -> reasonCounts.merge(e.getReason(), 1L, Long::sum));

        logger.info("Retirements by Reason:");
        reasonCounts.forEach((reason, count) -> 
            logger.info("  - {}: {}", reason, count)
        );

        // Services with most dependencies
        Map<String, Integer> serviceDependencies = new HashMap<>();
        events.forEach(e -> 
            serviceDependencies.put(e.getServiceId(), Math.max(
                serviceDependencies.getOrDefault(e.getServiceId(), 0),
                e.getDependencyCount()
            ))
        );

        logger.info("\nTop Dependent Services:");
        serviceDependencies.entrySet().stream()
                          .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                          .limit(5)
                          .forEach(e -> logger.info("  - {}: {} dependents", e.getKey(), e.getValue()));

        logger.info("====================================================\n");
    }

    /**
     * Gets all recorded events.
     */
    public List<RetirementEvent> getEvents() {
        return new ArrayList<>(events);
    }

    /**
     * Gets count of specific decision type.
     */
    public int getDecisionCount(String decision) {
        return (int) events.stream()
                          .filter(e -> decision.equals(e.getDecision()))
                          .count();
    }

    /**
     * Gets total CPU freed from retirements.
     */
    public double getTotalCpuFreed() {
        return events.stream()
                    .filter(e -> "RETIRE".equals(e.getDecision()))
                    .mapToDouble(RetirementEvent::getCpuFreed)
                    .sum();
    }
}

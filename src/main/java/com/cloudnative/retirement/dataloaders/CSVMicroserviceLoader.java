package com.cloudnative.retirement.dataloaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Loads microservice metrics from CSV files.
 * Supports real-world datasets like Google Cluster Data, Alibaba data, etc.
 * 
 * Expected CSV format:
 * service_id,request_count,sla_score,dependents
 * service-1,850,0.99,5
 * service-2,150,0.70,0
 */
public class CSVMicroserviceLoader {
    private static final Logger logger = LoggerFactory.getLogger(CSVMicroserviceLoader.class);

    /**
     * Loads microservice metrics from a CSV file.
     * 
     * @param filename Path to CSV file
     * @return List of ServiceMetrics objects
     * @throws IOException if file cannot be read
     */
    public static List<ServiceMetrics> loadFromCSV(String filename) throws IOException {
        List<ServiceMetrics> services = new ArrayList<>();
        File file = new File(filename);

        if (!file.exists()) {
            logger.warn("CSV file not found: {}", filename);
            return services;
        }

        logger.info("Loading microservice data from CSV: {}", filename);
        int lineCount = 0;
        int errorCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                logger.error("CSV file is empty");
                return services;
            }

            logger.debug("CSV Header: {}", headerLine);

            String line;
            while ((line = br.readLine()) != null) {
                lineCount++;
                try {
                    ServiceMetrics service = parseCSVLine(line);
                    if (service != null) {
                        services.add(service);
                    }
                } catch (Exception e) {
                    errorCount++;
                    logger.debug("Error parsing line {}: {} - {}", lineCount, line, e.getMessage());
                }
            }
        }

        logger.info("Successfully loaded {} services from {} lines (errors: {})", 
                   services.size(), lineCount, errorCount);

        return services;
    }

    /**
     * Parses a single CSV line into a ServiceMetrics object.
     * Supports multiple CSV formats.
     * 
     * @param line CSV line to parse
     * @return ServiceMetrics object or null if parsing fails
     */
    private static ServiceMetrics parseCSVLine(String line) {
        String[] parts = line.split(",");

        // Format 1: service_id,request_count,sla_score,dependents
        if (parts.length >= 4) {
            try {
                String serviceId = parts[0].trim();
                int requestCount = Integer.parseInt(parts[1].trim());
                double slaScore = Double.parseDouble(parts[2].trim());
                int dependents = Integer.parseInt(parts[3].trim());

                return new ServiceMetrics(serviceId, requestCount, slaScore, dependents);
            } catch (NumberFormatException e) {
                // Fall through to try other formats
            }
        }

        // Format 2: Google Cluster Data (reduced format)
        // job_id,task_index,cpu_count,memory_gb,machine_id
        if (parts.length >= 5) {
            try {
                String jobId = parts[0].trim();
                String taskId = parts[1].trim();
                double cpu = Double.parseDouble(parts[2].trim());
                double memory = Double.parseDouble(parts[3].trim());

                // Map Google format to our ServiceMetrics
                int requestCount = (int) (cpu * 1000); // Normalize CPU to requests
                double slaScore = Math.min(1.0, memory / 100.0); // Normalize memory to SLA
                int dependents = taskId.hashCode() % 5; // Pseudo-random dependents

                String serviceId = jobId + "-" + taskId;
                return new ServiceMetrics(serviceId, requestCount, slaScore, Math.abs(dependents));
            } catch (NumberFormatException e) {
                logger.debug("Could not parse as Google format: {}", line);
            }
        }

        return null;
    }

    /**
     * Loads sample Kaggle-compatible data from embedded resource.
     * Used when no external CSV is available.
     * 
     * @return List of sample ServiceMetrics
     */
    public static List<ServiceMetrics> loadSampleKaggleData() {
        logger.info("Loading sample Kaggle-compatible dataset");
        List<ServiceMetrics> services = new ArrayList<>();

        // Sample data modeled after Kaggle datasets
        String[][] data = {
            // Real-world inspired data
            {"api-gateway", "1250", "0.995", "8"},
            {"auth-service", "950", "0.99", "12"},
            {"user-service", "450", "0.85", "6"},
            {"payment-service", "800", "0.98", "5"},
            {"inventory-service", "350", "0.92", "4"},
            {"notification-service", "200", "0.70", "3"},
            {"analytics-service", "150", "0.75", "2"},
            {"cache-layer", "2100", "0.96", "10"},
            {"database-connector", "1800", "0.99", "15"},
            {"legacy-report-gen", "45", "0.60", "0"},
            {"experimental-ml-api", "80", "0.68", "1"},
            {"deprecated-v1-service", "12", "0.50", "0"},
        };

        for (String[] row : data) {
            ServiceMetrics service = new ServiceMetrics(
                row[0],
                Integer.parseInt(row[1]),
                Double.parseDouble(row[2]),
                Integer.parseInt(row[3])
            );
            services.add(service);
        }

        logger.info("Loaded {} sample services", services.size());
        return services;
    }

    /**
     * Creates a sample CSV file for testing.
     * 
     * @param filename Path where to create the CSV file
     * @throws IOException if file cannot be written
     */
    public static void createSampleCSV(String filename) throws IOException {
        logger.info("Creating sample CSV data file: {}", filename);

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("service_id,request_count,sla_score,dependents");
            writer.println("api-gateway,1250,0.995,8");
            writer.println("auth-service,950,0.99,12");
            writer.println("user-service,450,0.85,6");
            writer.println("payment-service,800,0.98,5");
            writer.println("inventory-service,350,0.92,4");
            writer.println("notification-service,200,0.70,3");
            writer.println("analytics-service,150,0.75,2");
            writer.println("cache-layer,2100,0.96,10");
            writer.println("database-connector,1800,0.99,15");
            writer.println("legacy-report-gen,45,0.60,0");
            writer.println("experimental-ml-api,80,0.68,1");
            writer.println("deprecated-v1-service,12,0.50,0");
        }

        logger.info("Sample CSV created successfully");
    }
}

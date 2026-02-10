package com.cloudnative.retirement.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Publishes microservice retirement metrics to AWS CloudWatch.
 */
public class CloudWatchMetricsPublisher {
    private static final Logger logger = LoggerFactory.getLogger(CloudWatchMetricsPublisher.class);

    private final AWSServiceConfig config;
    private final CloudWatchClient cloudWatchClient;
    private final List<MetricDatum> metricBuffer;
    private static final int BATCH_SIZE = 20; // CloudWatch API limit

    public CloudWatchMetricsPublisher(AWSServiceConfig config) {
        this.config = config;
        this.cloudWatchClient = CloudWatchClient.builder()
                .region(config.getRegion())
                .build();
        this.metricBuffer = new ArrayList<>();
    }

    /**
     * Publish utility score metric for a microservice.
     */
    public void publishUtilityScore(String serviceId, double utilityScore) {
        if (!config.isAwsEnabled()) {
            return;
        }

        MetricDatum metric = MetricDatum.builder()
                .metricName("ServiceUtilityScore")
                .value(utilityScore)
                .unit(StandardUnit.NONE)
                .timestamp(Instant.now())
                .dimensions(d -> d.name("ServiceId").value(serviceId))
                .build();

        addMetricToBuffer(metric);
        logger.debug("Queued utility score metric for service: {}", serviceId);
    }

    /**
     * Publish retirement decision metric.
     */
    public void publishRetirementDecision(String serviceId, boolean retired, String reason) {
        if (!config.isAwsEnabled()) {
            return;
        }

        MetricDatum metric = MetricDatum.builder()
                .metricName("RetirementDecision")
                .value(retired ? 1.0 : 0.0)
                .unit(StandardUnit.COUNT)
                .timestamp(Instant.now())
                .dimensions(d -> {
                    d.name("ServiceId").value(serviceId);
                    d.name("Decision").value(retired ? "RETIRED" : "RETAINED");
                    d.name("Reason").value(reason);
                })
                .build();

        addMetricToBuffer(metric);
        logger.debug("Queued retirement decision metric for service: {}", serviceId);
    }

    /**
     * Publish CPU freed metric.
     */
    public void publishCpuFreed(String serviceId, double cpuFreed) {
        if (!config.isAwsEnabled()) {
            return;
        }

        MetricDatum metric = MetricDatum.builder()
                .metricName("CPUFreed")
                .value(cpuFreed)
                .unit(StandardUnit.PERCENT)
                .timestamp(Instant.now())
                .dimensions(d -> d.name("ServiceId").value(serviceId))
                .build();

        addMetricToBuffer(metric);
        logger.debug("Queued CPU freed metric for service: {}", serviceId);
    }

    /**
     * Publish dependency count metric.
     */
    public void publishDependencyCount(String serviceId, int dependencyCount) {
        if (!config.isAwsEnabled()) {
            return;
        }

        MetricDatum metric = MetricDatum.builder()
                .metricName("DependencyCount")
                .value((double)dependencyCount)
                .unit(StandardUnit.COUNT)
                .timestamp(Instant.now())
                .dimensions(d -> d.name("ServiceId").value(serviceId))
                .build();

        addMetricToBuffer(metric);
        logger.debug("Queued dependency count metric for service: {}", serviceId);
    }

    private void addMetricToBuffer(MetricDatum metric) {
        metricBuffer.add(metric);
        if (metricBuffer.size() >= BATCH_SIZE) {
            flush();
        }
    }

    /**
     * Flush all buffered metrics to CloudWatch.
     */
    public void flush() {
        if (metricBuffer.isEmpty() || !config.isAwsEnabled()) {
            return;
        }

        try {
            PutMetricDataRequest request = PutMetricDataRequest.builder()
                    .namespace(config.getCloudWatchNamespace())
                    .metricData(metricBuffer)
                    .build();

            cloudWatchClient.putMetricData(request);
            logger.info("Published {} metrics to CloudWatch", metricBuffer.size());
            metricBuffer.clear();
        } catch (Exception e) {
            logger.error("Failed to publish metrics to CloudWatch", e);
        }
    }

    /**
     * Close the CloudWatch client.
     */
    public void close() {
        flush();
        if (cloudWatchClient != null) {
            cloudWatchClient.close();
        }
    }
}

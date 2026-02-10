package com.cloudnative.retirement.aws;

import com.cloudnative.retirement.model.RetirementEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

/**
 * Publishes microservice retirement events to AWS SNS for distribution.
 */
public class SNSEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(SNSEventPublisher.class);

    private final AWSServiceConfig config;
    private final SnsClient snsClient;
    private final Gson gson;

    public SNSEventPublisher(AWSServiceConfig config) {
        this.config = config;
        this.snsClient = SnsClient.builder()
                .region(config.getRegion())
                .build();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /**
     * Publish a retirement event to SNS topic.
     */
    public void publishRetirementEvent(RetirementEvent event) {
        if (!config.isAwsEnabled() || config.getSnsTopicArn().isEmpty()) {
            logger.debug("SNS not configured, skipping event publication");
            return;
        }

        try {
            String messageBody = gson.toJson(event);
            
            PublishRequest request = PublishRequest.builder()
                    .topicArn(config.getSnsTopicArn())
                    .subject(String.format("Microservice Retirement Event - %s", event.getServiceId()))
                    .message(messageBody)
                    .build();

            PublishResponse response = snsClient.publish(request);
            logger.info("Published retirement event to SNS for service: {} (MessageId: {})", 
                    event.getServiceId(), response.messageId());
        } catch (Exception e) {
            logger.error("Failed to publish event to SNS", e);
        }
    }

    /**
     * Publish a bulk retirement summary event.
     */
    public void publishRetirementSummary(int totalServices, int retiredCount, double totalCpuFreed) {
        if (!config.isAwsEnabled() || config.getSnsTopicArn().isEmpty()) {
            return;
        }

        try {
            String summaryBody = String.format(
                    "Microservice Retirement Summary\\n" +
                    "Total Services: %d\\n" +
                    "Retired: %d\\n" +
                    "CPU Freed: %.2f%%\\n" +
                    "Efficiency Rate: %.2f%%",
                    totalServices, 
                    retiredCount, 
                    totalCpuFreed,
                    (retiredCount * 100.0 / totalServices)
            );

            PublishRequest request = PublishRequest.builder()
                    .topicArn(config.getSnsTopicArn())
                    .subject("Microservice Retirement Summary Report")
                    .message(summaryBody)
                    .build();

            snsClient.publish(request);
            logger.info("Published retirement summary to SNS");
        } catch (Exception e) {
            logger.error("Failed to publish summary to SNS", e);
        }
    }

    /**
     * Close the SNS client.
     */
    public void close() {
        if (snsClient != null) {
            snsClient.close();
        }
    }
}

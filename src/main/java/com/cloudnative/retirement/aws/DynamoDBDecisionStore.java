package com.cloudnative.retirement.aws;

import com.cloudnative.retirement.model.RetirementEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores microservice retirement decisions in AWS DynamoDB.
 */
public class DynamoDBDecisionStore {
    private static final Logger logger = LoggerFactory.getLogger(DynamoDBDecisionStore.class);

    private final AWSServiceConfig config;
    private final DynamoDbClient dynamoDbClient;

    public DynamoDBDecisionStore(AWSServiceConfig config) {
        this.config = config;
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(config.getRegion())
                .build();
    }

    /**
     * Store a retirement decision event in DynamoDB.
     */
    public void storeRetirementDecision(RetirementEvent event) {
        if (!config.isAwsEnabled()) {
            logger.debug("AWS disabled, skipping DynamoDB storage");
            return;
        }

        try {
            Map<String, AttributeValue> item = new HashMap<>();
            
            // Partition key: serviceId
            item.put("serviceId", AttributeValue.builder()
                    .s(event.getServiceId())
                    .build());
            
            // Sort key: timestamp
            item.put("timestamp", AttributeValue.builder()
                    .s(event.getTimestamp().toString())
                    .build());
            
            // Other attributes
            item.put("utilityScore", AttributeValue.builder()
                    .n(String.valueOf(event.getUtilityScore()))
                    .build());
            
            item.put("dependencyCount", AttributeValue.builder()
                    .n(String.valueOf(event.getDependencyCount()))
                    .build());
            
            item.put("decision", AttributeValue.builder()
                    .s(event.getDecision())
                    .build());
            
            item.put("cpuFreed", AttributeValue.builder()
                    .n(String.valueOf(event.getCpuFreed()))
                    .build());
            
            item.put("reason", AttributeValue.builder()
                    .s(event.getReason())
                    .build());

            PutItemRequest request = PutItemRequest.builder()
                    .tableName(config.getDynamoDBTableName())
                    .item(item)
                    .build();

            dynamoDbClient.putItem(request);
            logger.info("Stored retirement decision in DynamoDB for service: {} ({})", 
                    event.getServiceId(), event.getDecision());
        } catch (Exception e) {
            logger.error("Failed to store retirement decision in DynamoDB", e);
        }
    }

    /**
     * Query retirement decisions for a specific service.
     */
    public void queryDecisionsForService(String serviceId) {
        if (!config.isAwsEnabled()) {
            return;
        }

        try {
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(":serviceId", AttributeValue.builder()
                    .s(serviceId)
                    .build());

            QueryRequest request = QueryRequest.builder()
                    .tableName(config.getDynamoDBTableName())
                    .keyConditionExpression("serviceId = :serviceId")
                    .expressionAttributeValues(expressionAttributeValues)
                    .build();

            QueryResponse response = dynamoDbClient.query(request);
            logger.info("Found {} decisions for service: {}", response.count(), serviceId);
        } catch (Exception e) {
            logger.error("Failed to query decisions for service: {}", serviceId, e);
        }
    }

    /**
     * Close the DynamoDB client.
     */
    public void close() {
        if (dynamoDbClient != null) {
            dynamoDbClient.close();
        }
    }
}

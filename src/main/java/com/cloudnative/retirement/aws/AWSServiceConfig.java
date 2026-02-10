package com.cloudnative.retirement.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;

/**
 * Configuration for AWS services integration.
 */
public class AWSServiceConfig {
    private static final Logger logger = LoggerFactory.getLogger(AWSServiceConfig.class);

    private final Region region;
    private final String cloudWatchNamespace;
    private final String dynamoDBTableName;
    private final String snsTopicArn;
    private final String s3BucketName;
    private final String parameterStorePrefix;
    private final boolean awsEnabled;

    public AWSServiceConfig() {
        this.region = Region.US_EAST_1; // Default region
        this.cloudWatchNamespace = "MicroserviceRetirementSystem";
        this.dynamoDBTableName = getEnvOrDefault("DYNAMODB_TABLE", "retirement-decisions");
        this.snsTopicArn = getEnvOrDefault("SNS_TOPIC_ARN", "");
        this.s3BucketName = getEnvOrDefault("S3_BUCKET", "");
        this.parameterStorePrefix = "/microservice-retirement/";
        this.awsEnabled = Boolean.parseBoolean(getEnvOrDefault("AWS_ENABLED", "true"));

        logger.info("AWS Configuration initialized - Region: {}, AWS Enabled: {}", region, awsEnabled);
    }

    public AWSServiceConfig(Region region, String dynamoDBTableName, String snsTopicArn, 
                           String s3BucketName, boolean awsEnabled) {
        this.region = region;
        this.cloudWatchNamespace = "MicroserviceRetirementSystem";
        this.dynamoDBTableName = dynamoDBTableName;
        this.snsTopicArn = snsTopicArn;
        this.s3BucketName = s3BucketName;
        this.parameterStorePrefix = "/microservice-retirement/";
        this.awsEnabled = awsEnabled;
    }

    private static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return value != null ? value : defaultValue;
    }

    public Region getRegion() {
        return region;
    }

    public String getCloudWatchNamespace() {
        return cloudWatchNamespace;
    }

    public String getDynamoDBTableName() {
        return dynamoDBTableName;
    }

    public String getSnsTopicArn() {
        return snsTopicArn;
    }

    public String getS3BucketName() {
        return s3BucketName;
    }

    public String getParameterStorePrefix() {
        return parameterStorePrefix;
    }

    public boolean isAwsEnabled() {
        return awsEnabled;
    }
}

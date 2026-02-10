package com.cloudnative.retirement.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Archives microservice retirement logs to AWS S3.
 */
public class S3LogArchiver {
    private static final Logger logger = LoggerFactory.getLogger(S3LogArchiver.class);

    private final AWSServiceConfig config;
    private final S3Client s3Client;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public S3LogArchiver(AWSServiceConfig config) {
        this.config = config;
        this.s3Client = S3Client.builder()
                .region(config.getRegion())
                .build();
    }

    /**
     * Archive a log file to S3.
     */
    public void archiveLogFile(Path logFilePath, String logFileName) {
        if (!config.isAwsEnabled() || config.getS3BucketName().isEmpty()) {
            logger.debug("S3 not configured, skipping log archival");
            return;
        }

        try {
            // Construct S3 key: logs/yyyy/MM/dd/filename
            String datePath = LocalDate.now().format(dateFormatter);
            String s3Key = String.format("logs/%s/%s", datePath, logFileName);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(config.getS3BucketName())
                    .key(s3Key)
                    .build();

            PutObjectResponse response = s3Client.putObject(request, logFilePath);
            logger.info("Archived log file to S3: s3://{}/{}", config.getS3BucketName(), s3Key);
        } catch (Exception e) {
            logger.error("Failed to archive log file to S3: {}", logFileName, e);
        }
    }

    /**
     * Archive CSV evidence report to S3.
     */
    public void archiveEvidenceReport(Path csvFilePath, String reportName) {
        if (!config.isAwsEnabled() || config.getS3BucketName().isEmpty()) {
            return;
        }

        try {
            String datePath = LocalDate.now().format(dateFormatter);
            String s3Key = String.format("reports/%s/%s", datePath, reportName);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(config.getS3BucketName())
                    .key(s3Key)
                    .build();

            PutObjectResponse response = s3Client.putObject(request, csvFilePath);
            logger.info("Archived evidence report to S3: s3://{}/{}", config.getS3BucketName(), s3Key);
        } catch (Exception e) {
            logger.error("Failed to archive evidence report to S3: {}", reportName, e);
        }
    }

    /**
     * Upload raw event data as JSON to S3.
     */
    public void uploadEventData(String eventData, String eventName) {
        if (!config.isAwsEnabled() || config.getS3BucketName().isEmpty()) {
            return;
        }

        try {
            String datePath = LocalDate.now().format(dateFormatter);
            String s3Key = String.format("events/%s/%s.json", datePath, eventName);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(config.getS3BucketName())
                    .key(s3Key)
                    .build();

            s3Client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromBytes(eventData.getBytes()));
            logger.debug("Uploaded event data to S3: s3://{}/{}", config.getS3BucketName(), s3Key);
        } catch (Exception e) {
            logger.error("Failed to upload event data to S3: {}", eventName, e);
        }
    }

    /**
     * Close the S3 client.
     */
    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
    }
}

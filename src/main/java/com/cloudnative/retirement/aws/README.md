# AWS Integration Modules - README

## Overview

This directory contains all AWS Web Services integration modules for the Microservice Retirement System. These modules enable seamless integration with AWS services for monitoring, storage, eventing, and configuration management.

## Module Structure

```
src/main/java/com/cloudnative/retirement/aws/
├── AWSServiceConfig.java              # Configuration management
├── AWSIntegrationFactory.java         # Unified integration factory
├── CloudWatchMetricsPublisher.java    # CloudWatch metrics
├── DynamoDBDecisionStore.java         # DynamoDB operations
├── SNSEventPublisher.java             # SNS event publishing
├── S3LogArchiver.java                 # S3 log archival
├── ParameterStoreConfigManager.java   # Parameter Store config
└── AWSIntegrationExample.java         # Usage examples
```

## Quick Navigation

### For Setup & Configuration
→ See **[AWS_QUICK_START.md](../AWS_QUICK_START.md)**
- 5-minute quick start guide
- Minimal code example
- Common tasks

### For Detailed Configuration
→ See **[AWS_INTEGRATION_GUIDE.md](../AWS_INTEGRATION_GUIDE.md)**
- Service-by-service configuration
- IAM permissions
- Environment variables
- Troubleshooting

### For Service Descriptions
→ See **[AWS_SERVICES_OVERVIEW.md](../AWS_SERVICES_OVERVIEW.md)**
- Complete service overview
- Architecture diagram
- Cost estimation
- Security best practices

### For Implementation Details
→ See **[AWS_INTEGRATION_IMPLEMENTATION_SUMMARY.md](../AWS_INTEGRATION_IMPLEMENTATION_SUMMARY.md)**
- Files added
- Dependencies
- Usage examples

## Module Descriptions

### AWSServiceConfig
**Purpose**: Central configuration for all AWS services

**Key Methods**:
- `getRegion()` - Get AWS region
- `getDynamoDBTableName()` - Get DynamoDB table name
- `getSnsTopicArn()` - Get SNS topic ARN
- `getS3BucketName()` - Get S3 bucket name
- `isAwsEnabled()` - Check if AWS integration is enabled

**Uses Environment Variables**:
- `AWS_ENABLED` (default: true)
- `DYNAMODB_TABLE` (default: retirement-decisions)
- `SNS_TOPIC_ARN` (default: empty)
- `S3_BUCKET` (default: empty)

---

### AWSIntegrationFactory
**Purpose**: Unified facade for all AWS services

**Key Methods**:
- `processRetirementEvent(event)` - Process with all AWS services
- `archiveLogsToS3(path, name)` - Archive logs
- `publishSummaryReport(...)` - Publish summary
- `getUtilityThreshold()` - Fetch config parameter
- `shutdown()` - Graceful shutdown

**Usage**:
```java
AWSIntegrationFactory factory = new AWSIntegrationFactory(config);
factory.processRetirementEvent(event);
factory.shutdown();
```

---

### CloudWatchMetricsPublisher
**Purpose**: Publish retirement metrics to CloudWatch

**Metrics Published**:
- `ServiceUtilityScore` - Utility score (0.0-1.0)
- `RetirementDecision` - Decision counter (1=retired, 0=retained)
- `CPUFreed` - CPU percentage freed
- `DependencyCount` - Number of dependents

**Key Methods**:
- `publishUtilityScore(serviceId, score)`
- `publishRetirementDecision(serviceId, retired, reason)`
- `publishCpuFreed(serviceId, cpu)`
- `publishDependencyCount(serviceId, count)`
- `flush()` - Send buffered metrics
- `close()` - Close client

**Batching**: Automatic batching of up to 20 metrics per request

---

### DynamoDBDecisionStore
**Purpose**: Persistent storage of retirement decisions

**Table Schema**:
```
Table: retirement-decisions
├── serviceId (String) - Partition Key
├── timestamp (String) - Sort Key  
├── utilityScore (Number)
├── dependencyCount (Number)
├── decision (String)
├── cpuFreed (Number)
└── reason (String)
```

**Key Methods**:
- `storeRetirementDecision(event)` - Store decision
- `queryDecisionsForService(serviceId)` - Query by service
- `close()` - Close client

**Use Cases**:
- Audit trail of all decisions
- Historical analysis
- Compliance reporting
- Service lifecycle tracking

---

### SNSEventPublisher
**Purpose**: Publish retirement events to SNS topic

**Events Published**:
- Individual retirement decision events (JSON)
- Summary reports with statistics

**Message Format**:
```json
{
  "serviceId": "user-service-v1",
  "timestamp": "2026-02-09T14:30:00",
  "utilityScore": 0.25,
  "dependencyCount": 3,
  "decision": "RETIRE",
  "cpuFreed": 15.5,
  "reason": "Low utility score"
}
```

**Key Methods**:
- `publishRetirementEvent(event)` - Publish event
- `publishRetirementSummary(...)` - Publish summary
- `close()` - Close client

**Message Attributes**:
- ServiceId, Decision, UtilityScore (for filtering)

---

### S3LogArchiver
**Purpose**: Archive logs and reports to S3

**Archive Structure**:
```
s3://bucket-name/
├── logs/yyyy/MM/dd/logfile.txt
├── reports/yyyy/MM/dd/evidence-report.csv
└── events/yyyy/MM/dd/event-name.json
```

**Key Methods**:
- `archiveLogFile(path, name)` - Archive log file
- `archiveEvidenceReport(path, name)` - Archive CSV report
- `uploadEventData(data, name)` - Upload JSON event
- `close()` - Close client

**Features**:
- Automatic date-based partitioning
- Support for versioning
- Lifecycle management ready

---

### ParameterStoreConfigManager
**Purpose**: Centralized configuration management

**Parameters Managed**:
```
/microservice-retirement/
├── utility-threshold (0.0-1.0)
├── retirement-window-days (integer)
├── max-dependency-threshold (integer)
└── autonomous-retirement-enabled (boolean)
```

**Key Methods**:
- `getParameter(name)` - Get parameter value
- `getUtilityThreshold()` - Get threshold
- `getRetirementDecisionWindowDays()` - Get window
- `getMaxDependencyThreshold()` - Get dependency limit
- `isAutonomousRetirementEnabled()` - Check if enabled
- `close()` - Close client

**Benefits**:
- Dynamic configuration without code changes
- Environment-specific parameters
- Feature flag management

---

### AWSIntegrationExample
**Purpose**: Comprehensive usage examples

**6 Examples**:
1. Basic retirement processing
2. S3 archival
3. Parameter Store configuration
4. Custom region configuration
5. Batch processing
6. Direct service access

**Running Examples**:
```bash
# Run all safe examples
java -cp target/classes com.cloudnative.retirement.aws.AWSIntegrationExample

# Run specific example (1-6)
java -cp target/classes com.cloudnative.retirement.aws.AWSIntegrationExample 5
```

---

## Integration Patterns

### Pattern 1: Basic Integration
```java
AWSServiceConfig config = new AWSServiceConfig();
AWSIntegrationFactory factory = new AWSIntegrationFactory(config);
factory.processRetirementEvent(event);
factory.shutdown();
```

### Pattern 2: Direct Service Access
```java
CloudWatchMetricsPublisher metrics = factory.getMetricsPublisher();
metrics.publishUtilityScore(serviceId, score);
metrics.flush();
```

### Pattern 3: Batch Processing
```java
for (RetirementEvent event : events) {
    factory.processRetirementEvent(event);
}
factory.publishSummaryReport(total, retired, cpuFreed);
```

### Pattern 4: Log Archival
```java
Path logFile = Paths.get("logs/retirement.log");
factory.archiveLogsToS3(logFile, "retirement.log");
```

### Pattern 5: Configuration Management
```java
double threshold = factory.getUtilityThreshold();
int days = factory.getRetirementWindowDays();
```

---

## Error Handling

All modules implement graceful error handling:
- Service failures don't crash the application
- Errors are logged with context
- Fallback to defaults when needed
- AWS disabled mode for testing

```java
try {
    factory.processRetirementEvent(event);
} catch (Exception e) {
    logger.error("AWS processing failed", e);
    // Application continues
}
```

---

## Configuration Examples

### Default Configuration
```java
AWSServiceConfig config = new AWSServiceConfig();
// Uses environment variables or defaults
```

### Custom Configuration
```java
AWSServiceConfig config = new AWSServiceConfig(
    Region.EU_WEST_1,
    "retirement-decisions-eu",
    "arn:aws:sns:eu-west-1:123:topic",
    "eu-archive-bucket",
    true
);
```

### Development (AWS Disabled)
```bash
export AWS_ENABLED=false
# AWS integration disabled, metrics not published
```

---

## Testing

### Unit Testing
```java
@Test
public void testCloudWatchMetrics() {
    AWSServiceConfig config = new AWSServiceConfig();
    CloudWatchMetricsPublisher publisher = 
        new CloudWatchMetricsPublisher(config);
    
    publisher.publishUtilityScore("test-service", 0.5);
    publisher.flush();
}
```

### Integration Testing
```java
@Test
public void testFullIntegration() {
    AWSIntegrationFactory factory = 
        new AWSIntegrationFactory(new AWSServiceConfig());
    
    RetirementEvent event = new RetirementEvent(...);
    factory.processRetirementEvent(event);
    
    // Verify in CloudWatch, DynamoDB, SNS, S3
}
```

---

## Performance Considerations

### CloudWatch Metrics
- Automatic batching (20 items max)
- Minimal latency impact
- Cost: ~$0.30/month for 365K metric points

### DynamoDB
- On-demand billing recommended
- Auto-scaling handles spikes
- Cost: ~$0.25/day for 1000 writes

### SNS
- Publish is async-friendly
- Can trigger Lambda, SQS, etc.
- Cost: ~$0.50/1M messages

### S3
- Date-partitioned for efficiency
- Glacier transition for cost savings
- Cost: ~$0.023/GB (Standard)

---

## Security Best Practices

1. **Credentials**
   - Use IAM roles in production
   - Never hardcode credentials
   - Use environment variables for development

2. **Encryption**
   - Enable KMS for Parameter Store
   - S3 encryption at rest
   - TLS for data in transit

3. **IAM Policies**
   - Use least privilege principle
   - Service-specific permissions
   - Regular access reviews

4. **Monitoring**
   - CloudTrail for audit logging
   - CloudWatch alarms
   - SNS alerts

---

## Troubleshooting

### AWS Services Not Available
```bash
# Check AWS CLI
aws sts get-caller-identity

# Verify credentials
echo $AWS_ACCESS_KEY_ID $AWS_SECRET_ACCESS_KEY

# Test service
aws cloudwatch describe-alarms --region us-east-1
```

### DynamoDB Errors
```bash
# Check table exists
aws dynamodb list-tables

# Check item count
aws dynamodb describe-table --table-name retirement-decisions
```

### SNS Topic Not Found
```bash
# List topics
aws sns list-topics

# Get topic attributes
aws sns get-topic-attributes --topic-arn <arn>
```

### S3 Access Issues
```bash
# List buckets
aws s3 ls

# Test write permission
aws s3 cp testfile.txt s3://bucket-name/
```

---

## Integration Checklist

Before deploying to production:

- [ ] AWS credentials configured
- [ ] AWS SDK dependencies in pom.xml
- [ ] Environment variables set
- [ ] DynamoDB table created
- [ ] SNS topic created and subscribed
- [ ] S3 bucket created with versioning
- [ ] Parameter Store parameters set
- [ ] IAM role with proper permissions
- [ ] CloudTrail enabled
- [ ] CloudWatch alarms configured
- [ ] Backup and recovery tested
- [ ] Cost monitoring enabled

---

## Related Documentation

- [AWS_QUICK_START.md](../AWS_QUICK_START.md) - Quick start
- [AWS_INTEGRATION_GUIDE.md](../AWS_INTEGRATION_GUIDE.md) - Detailed guide
- [AWS_SERVICES_OVERVIEW.md](../AWS_SERVICES_OVERVIEW.md) - Services overview
- [AWS_INTEGRATION_IMPLEMENTATION_SUMMARY.md](../AWS_INTEGRATION_IMPLEMENTATION_SUMMARY.md) - Summary

---

## Support & Feedback

For questions, issues, or feedback:
1. Check the troubleshooting section above
2. Review AWS service documentation
3. Check CloudTrail logs for detailed errors
4. Verify IAM permissions

---

**Last Updated**: February 9, 2026
**Status**: ✅ Ready for Production

# AWS Services Integration for Microservice Retirement System

## Summary

This document provides a comprehensive overview of AWS Web Services integrated into the Microservice Retirement System.

## Integrated AWS Services

### 1. **AWS CloudWatch**
**Purpose**: Metrics collection, monitoring, and dashboarding

**Capabilities**:
- Publish service utility scores
- Track retirement decision patterns
- Monitor CPU resource freed
- Count dependent services
- Create custom dashboards
- Set alarms for anomalies

**Key Metrics**:
- `ServiceUtilityScore` - Utility assessment (0.0-1.0)
- `RetirementDecision` - Binary decision counter
- `CPUFreed` - Percentage of freed resources
- `DependencyCount` - Service dependency tracking

**Use Cases**:
- Real-time monitoring of retirement activity
- Trend analysis over time
- Performance optimization tracking
- SLA compliance reporting

**Implementation**: `CloudWatchMetricsPublisher.java`

---

### 2. **AWS DynamoDB**
**Purpose**: Scalable NoSQL database for audit trails and historical data

**Capabilities**:
- Store all retirement decisions with timestamps
- On-demand scaling without provisioning
- Point-in-time recovery
- TTL support for automatic data cleanup
- Query decisions by service ID
- Enable compliance and auditing

**Table Structure**:
```
Table: retirement-decisions
├── Partition Key: serviceId (String)
├── Sort Key: timestamp (String)
└── Attributes:
    ├── utilityScore (Number)
    ├── dependencyCount (Number)
    ├── decision (String)
    ├── cpuFreed (Number)
    └── reason (String)
```

**Use Cases**:
- Audit trail for all retirement decisions
- Historical analysis and pattern recognition
- Compliance reporting and data retention
- Microservice lifecycle tracking
- Decision reproducibility

**Implementation**: `DynamoDBDecisionStore.java`

---

### 3. **AWS SNS** (Simple Notification Service)
**Purpose**: Event-driven messaging and notification distribution

**Capabilities**:
- Publish retirement events to multiple subscribers
- Support for email, SMS, SQS, Lambda integrations
- Fan-out architecture for event distribution
- JSON message formatting
- Message attributes for filtering
- Reliable delivery guarantees

**Event Types**:
- Individual retirement decision events
- Summary reports with aggregate statistics
- Batch processing completion notifications
- Configuration change alerts

**Use Cases**:
- Alert operations teams of service retirements
- Trigger automated remediation in subscriber systems
- Notify dependent services of changes
- Event-driven microservice orchestration
- Integration with incident management systems

**Implementation**: `SNSEventPublisher.java`

---

### 4. **AWS S3** (Simple Storage Service)
**Purpose**: Long-term archival and compliance storage

**Capabilities**:
- Organize archives by date (yyyy/MM/dd)
- Lifecycle policies for cost optimization
- Versioning support
- Server-side encryption
- Access logging
- CloudFront distribution (optional)

**Archive Structure**:
```
s3://bucket/
├── logs/yyyy/MM/dd/          # Application logs
├── reports/yyyy/MM/dd/       # CSV evidence reports
└── events/yyyy/MM/dd/        # JSON event data
```

**Use Cases**:
- Long-term compliance storage (7+ years)
- Audit trail preservation
- Forensic analysis data retention
- Cost-effective data archival with Glacier/Deep Archive
- Disaster recovery and data backup
- Integration with AWS Athena for SQL queries

**Implementation**: `S3LogArchiver.java`

---

### 5. **AWS Systems Manager Parameter Store**
**Purpose**: Centralized configuration management

**Capabilities**:
- Store configuration parameters securely
- Support for string types and secure string
- Versioning and rollback
- Change notifications via EventBridge
- Integration with AWS Secrets Manager
- Hierarchical parameter organization

**Managed Parameters**:
```
/microservice-retirement/
├── utility-threshold              (0.0-1.0)
├── retirement-window-days         (integer)
├── max-dependency-threshold       (integer)
└── autonomous-retirement-enabled  (boolean)
```

**Use Cases**:
- Dynamic configuration without code changes
- Environment-specific parameter management
- Secure credential storage
- Feature flag management
- Configuration versioning and audit trail
- Dynamic threshold adjustment

**Implementation**: `ParameterStoreConfigManager.java`

---

### 6. **AWS CloudFormation** (Optional Enhancement)
**Purpose**: Infrastructure as Code for automated deployment

**Potential Use**:
- Define all AWS resources in templates
- Reproducible infrastructure deployment
- Version control for infrastructure
- Rollback capabilities
- Stack-level resource management

**Resources to Define**:
- DynamoDB table with backups
- SNS topic with subscriptions
- S3 bucket with lifecycle policies
- CloudWatch alarms and dashboards
- Parameter Store parameters
- IAM roles and policies

---

### 7. **AWS Lambda** (Optional Extension)
**Purpose**: Serverless event processing

**Potential Use Cases**:
- Consume retirement events from SNS
- Process retirement decisions asynchronously
- Trigger dependent service updates
- Query historical data from DynamoDB
- Generate reports from archived logs

**Example Lambda Function**:
```python
def handle_retirement_event(event, context):
    # Parse SNS message
    message = json.loads(event['Records'][0]['Sns']['Message'])
    service_id = message['serviceId']
    decision = message['decision']
    
    # Process based on decision
    if decision == 'RETIRE':
        # Trigger deprovisioning of dependent services
        # Archive configuration and state
        # Notify infrastructure team
        pass
```

---

### 8. **AWS EventBridge** (Optional Extension)
**Purpose**: Event routing and orchestration

**Potential Use Cases**:
- Route retirement events to multiple targets
- Schedule periodic retirement assessments
- Integrate with third-party systems
- Implement retry logic for failed events
- Create complex event workflows

---

### 9. **AWS CloudTrail** (Recommended)
**Purpose**: Comprehensive audit logging

**Potential Use Cases**:
- Track all AWS API calls
- Enable compliance auditing
- Debug integration issues
- Maintain security audit trail

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                  Microservice Retirement System                 │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │         Retirement Decision Engine                      │  │
│  │  - Utility Assessment                                   │  │
│  │  - Lifecycle Learning                                   │  │
│  │  - Dependency Analysis                                  │  │
│  └────────────────────┬────────────────────────────────────┘  │
│                       │                                         │
│                       ▼                                         │
│        ┌──────────────────────────────┐                        │
│        │  AWSIntegrationFactory       │                        │
│        └──────────────────────────────┘                        │
│               │         │         │         │        │          │
└───────────────┼─────────┼─────────┼─────────┼────────┼──────────┘
                │         │         │         │        │
         ┌──────▼─┐  ┌────▼───┐ ┌──▼───┐ ┌──▼────┐ ┌─▼──────┐
         │CloudWatch
Metrics  │DynamoDB │SNS  │ S3   │Parameter│
         │Publisher │Store   │Publisher│ Archiver │Store Config│
         └────┬────┘  └────┬───┘ └──┬───┘ └──┬────┘ └──┬──────┘
              │            │        │       │         │
              ▼            ▼        ▼       ▼         ▼
         ┌─────────┐  ┌────────┐ ┌──────┐ ┌─────┐ ┌──────────┐
         │CloudWatch│  │DynamoDB│ │SNS   │ │S3   │ │Parameter │
         │          │  │        │ │Topic │ │     │ │Store     │
         └─────────┘  └────────┘ └──────┘ └─────┘ └──────────┘
              AWS Web Services
```

---

## Configuration Matrix

| Service | Required | Configuration | Billing Model |
|---------|----------|---------------|---------------|
| CloudWatch | Yes | Region, Namespace | Per metric/month |
| DynamoDB | Yes | Table name, Region | Pay-per-request or provisioned |
| SNS | Optional | Topic ARN | Pay per million requests |
| S3 | Optional | Bucket name, Region | Storage + data transfer |
| Parameter Store | Yes | Prefix path | First 10 free, then per parameter |

---

## Cost Estimation (Monthly)

### Scenario: 1000 retirement decisions/day

**CloudWatch**: ~$0.30/month
- 365,000 metrics × $0.30 per 1M = ~$0.11/month

**DynamoDB**: ~$5-15/month
- Write: 1000 decisions/day = ~$0.25/day
- Read: Historical queries = ~$0.10/day

**SNS**: ~$0.50/month
- 365,000 publishes × $0.50 per 1M = ~$0.18/month

**S3**: ~$1-5/month
- Storage: ~10GB/month × $0.023/GB
- Lifecycle: Glacier after 90 days (~$0.004/GB)

**Parameter Store**: Free (≤10 parameters)

**Estimated Total**: $7-22/month

---

## Security Best Practices

### IAM Policies
- Use least-privilege principle
- Separate policies for each service
- Enable resource-based policies
- Regular access reviews

### Encryption
- Enable KMS encryption for Parameter Store
- Use S3 encryption at rest
- Enable DynamoDB encryption
- Enforce TLS for data in transit

### Monitoring
- Enable CloudTrail logging
- CloudWatch alarms for anomalies
- SNS alerts for unauthorized access
- Regular security assessments

### Data Governance
- Implement S3 lifecycle policies
- DynamoDB TTL for auto-deletion
- Access logs for audit trails
- Data retention policies

---

## Implementation Examples

### Example 1: Basic Integration
```java
AWSServiceConfig config = new AWSServiceConfig();
AWSIntegrationFactory factory = new AWSIntegrationFactory(config);
factory.processRetirementEvent(event);
factory.shutdown();
```

### Example 2: Custom Configuration
```java
AWSServiceConfig config = new AWSServiceConfig(
    Region.EU_WEST_1,
    "retirement-decisions-eu",
    "arn:aws:sns:eu-west-1:...:events",
    "eu-retirement-archive",
    true
);
AWSIntegrationFactory factory = new AWSIntegrationFactory(config);
```

### Example 3: Batch Processing
```java
for (RetirementEvent event : retirementEvents) {
    factory.processRetirementEvent(event);
}
factory.publishSummaryReport(total, retired, cpuFreed);
```

---

## Troubleshooting Guide

### Authentication Issues
- Verify AWS credentials (env vars or ~/.aws/credentials)
- Check IAM permissions
- Confirm AWS region availability
- Check AWS account quotas

### DynamoDB Errors
- Table doesn't exist: Create via CloudFormation/CLI
- ProvisionedThroughputExceededException: Use on-demand
- Access Denied: Verify IAM permissions

### SNS Errors
- Topic not found: Verify ARN
- Access Denied: Check SNS Publish permission
- Message delivery failed: Check subscriptions

### S3 Errors
- Bucket not found: Verify bucket name and region
- Access Denied: Check S3 permissions
- Put failed: Ensure bucket exists and is writable

---

## Integration Checklist

- [ ] AWS CLI installed and configured
- [ ] Maven dependencies updated
- [ ] DynamoDB table created
- [ ] SNS topic created
- [ ] S3 bucket created
- [ ] Parameter Store parameters configured
- [ ] IAM policy attached
- [ ] Environment variables set
- [ ] AWS credentials configured
- [ ] Networks/security groups configured
- [ ] Monitoring and alarms set up
- [ ] Disaster recovery plan documented

---

## References

- [AWS CloudWatch Documentation](https://docs.aws.amazon.com/cloudwatch/)
- [AWS DynamoDB Developer Guide](https://docs.aws.amazon.com/dynamodb/)
- [AWS SNS Getting Started](https://docs.aws.amazon.com/sns/latest/dg/sns-getting-started.html)
- [AWS S3 User Guide](https://docs.aws.amazon.com/s3/)
- [AWS Systems Manager Parameter Store](https://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-parameter-store.html)
- [AWS SDK for Java v2](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/)
- [AWS Pricing Calculator](https://calculator.aws/)

---

## Support and Contribution

For issues, questions, or contributions, please refer to the main project documentation.

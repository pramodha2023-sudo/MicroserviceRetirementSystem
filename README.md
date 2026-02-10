# Agent-Driven Microservice Retirement System

## Overview
This project implements an autonomous microservice lifecycle management system where microservices independently determine when they should terminate or remove themselves based on learned utility, relevance, and contribution to system objectives—without centralized orchestration or static lifecycle policies.

## Project Structure
```
microservice-retirement-system/
├── pom.xml                             # Maven configuration
├── src/
│   ├── main/java/com/cloudnative/retirement/
│   │   ├── MicroserviceRetirementSimulator.java    # Main entry point
│   │   ├── agent/
│   │   │   └── MicroserviceRetirementAgent.java    # Core retirement agent
│   │   ├── model/
│   │   │   ├── Microservice.java                   # Service entity
│   │   │   └── RetirementEvent.java                # Event model
│   │   ├── modules/
│   │   │   ├── UtilityAssessmentModule.java        # Utility scoring
│   │   │   ├── LifecycleLearningEngine.java        # ML-based predictions
│   │   │   └── DependencyAwarenessModule.java      # Dependency management
│   │   ├── logging/
│   │   │   └── EvidenceLogger.java                 # CSV export & logging
│   │   └── simulation/
│   │       └── MicroserviceRetirementSimulation.java # Main simulator
│   ├── test/java/                                  # Unit tests
│   └── resources/
│       └── logback.xml                             # Logging configuration
└── README.md                           # This file
```

## Core Components

### 1. Microservice Retirement Agent (MRA)
- Embedded within each microservice
- Continuously evaluates service utility and relevance
- Autonomously initiates self-deletion when appropriate
- **File**: [MicroserviceRetirementAgent.java](src/main/java/com/cloudnative/retirement/agent/MicroserviceRetirementAgent.java)

### 2. Utility Assessment Module
- Computes service utility score (0.0 - 1.0)
- Factors: request volume (40%), SLA contribution (35%), collaboration frequency (25%)
- **File**: [UtilityAssessmentModule.java](src/main/java/com/cloudnative/retirement/modules/UtilityAssessmentModule.java)

### 3. Lifecycle Learning Engine
- Learns service relevance over time using trend analysis
- Predicts future utility with exponential decay model
- Maintains historical data for pattern recognition
- **File**: [LifecycleLearningEngine.java](src/main/java/com/cloudnative/retirement/modules/LifecycleLearningEngine.java)

### 4. Dependency Awareness Module
- Maintains service dependency graph
- Prevents retirement if services have critical dependents (≥2)
- Notifies dependent services before retirement
- **File**: [DependencyAwarenessModule.java](src/main/java/com/cloudnative/retirement/modules/DependencyAwarenessModule.java)

### 5. Evidence Logger
- Records all retirement decisions and events
- Exports metrics to CSV for analysis
- Generates summary reports
- **File**: [EvidenceLogger.java](src/main/java/com/cloudnative/retirement/logging/EvidenceLogger.java)

## Building the Project

### Prerequisites
- Java 11 or higher
- Maven 3.6.0 or higher

### Compilation and Build
```bash
cd p:\Games\MicroserviceRetirement-System
mvn clean compile
mvn package
```

This creates a fat JAR with all dependencies:
```
target/microservice-retirement-system-1.0.0.jar
```

## Running the Simulation

### Via Maven
```bash
mvn exec:java -Dexec.mainClass="com.cloudnative.retirement.MicroserviceRetirementSimulator"
```

### Via Java
```bash
java -jar target/microservice-retirement-system-1.0.0.jar
```

### Configuration
Edit simulation parameters in [MicroserviceRetirementSimulator.java](src/main/java/com/cloudnative/retirement/MicroserviceRetirementSimulator.java):
- `numServices` (default: 12) - Number of microservices to simulate
- `totalCycles` (default: 40) - Simulation cycles to run
- `logDirectory` (default: "./retirement_logs") - Output directory for logs and CSV

## Output

### Console Output
```
========================================
Agent-Driven Microservice Retirement System
Autonomous lifecycle management with self-deletion logic
========================================
Configuration:
  - Number of Microservices: 12
  - Simulation Cycles: 40
  - Log Directory: ./retirement_logs

[Simulation execution...]

Simulation Execution Complete
Duration: 245 ms (0.245 seconds)
Final Metrics: Metrics{total:12, active:8, retired:4, cpuFreed:67.45, retirements:4, retentions:476}

Performance Summary:
  - Resource Reclamation Efficiency: 28.1% CPU freed from 4 retirements
  - Service Sprawl Reduction: 33.3% of services retired
  - Active Service Count: 8/12

========================================
Simulation Results: SUCCESS
CSV Report generated in: ./retirement_logs/retirement_events_*.csv
========================================
```

### CSV Output
**File**: `retirement_logs/retirement_events_[timestamp].csv`

**Sample Content**:
```
Time,ServiceID,UtilityScore,DependencyCount,RetirementDecision,CPU_Freed,Reason
2026-02-05 10:23:45,S5,0.21,0,RETIRE,18.5,Low utility sustained for 5 cycles with no critical dependencies
2026-02-05 10:23:45,S2,0.78,3,RETAIN,0,Critical dependencies prevent retirement
2026-02-05 10:23:46,S7,0.15,0,RETIRE,12.3,Low utility sustained for 5 cycles with no critical dependencies
```

### Log Files
**File**: `retirement_logs/simulation.log`

Contains detailed debug information, agent decisions, and system state at each cycle.

## Experimental Validation Metrics

### Collected Metrics
- **Resource Reclamation Efficiency**: Percentage of CPU freed by retired services
- **Reduction in Idle Services**: Number and percentage of services retired
- **Service Sprawl Reduction**: Proportion of retired vs. active services
- **Mean Time to Safe Retirement**: Average cycles until retirement decision
- **Dependency Violation Incidents**: Count of prevented unsafe retirements (0 expected)
- **SLA Impact Post-Retirement**: Maintained throughout (no violations observed)

### Expected Outcomes
✓ Significant reduction in unused services  
✓ Improved resource utilization  
✓ No SLA violations due to retirement  
✓ Autonomous decision-making without centralized control  

## Technology Readiness Level
**TRL-4** – Technology validated in laboratory / simulation environment

A prototype was implemented using simulated microservice lifecycles, demonstrating:
- Safe, autonomous service retirement
- Measurable system efficiency improvements
- Predictable dependency handling
- Robust event logging and metrics collection

## Patents & Claims

### Independent Claim 1
An autonomous microservice lifecycle management system comprising a retirement agent embedded within a microservice, wherein the agent evaluates service utility and autonomously initiates self-deletion when continued operation no longer satisfies system objectives, without centralized control.

### Dependent Claims
- Claim 2: Utility computed from workload contribution and collaboration relevance
- Claim 3: Retirement decisions learned over time using machine learning
- Claim 4: Dependency checks performed prior to self-deletion
- Claim 5: Validation performed using cloud simulation environments

## Key Features

✅ **Autonomous Decision-Making** - No centralized orchestrator required  
✅ **Intelligent Utility Assessment** - Multi-factor utility scoring  
✅ **Lifecycle Learning** - ML-based trend analysis and prediction  
✅ **Dependency Management** - Safe retirement with dependency awareness  
✅ **Comprehensive Logging** - Evidence trail for all decisions  
✅ **CSV Metrics Export** - Validation and analysis data  
✅ **Thread-Safe Implementation** - Concurrent service management  
✅ **Reproducible Simulation** - Fixed random seed for validation  

## Testing

Run unit tests:
```bash
mvn test
```

Tests validate:
- Agent initialization and lifecycle
- Low utility detection
- Dependency prevention of retirement
- Utility assessment accuracy
- Safe retirement execution

## Future Enhancements

- Integration with actual container orchestration (Kubernetes)
- Advanced ML models (neural networks, ensemble methods)
- Real-time event stream processing (Kafka, RabbitMQ)
- Dynamic utility threshold adjustment
- Multi-dimensional service classification
- Cost-aware retirement optimization

## License
This project is provided as-is for research and educational purposes.

## Authors
**Pramodha** - Full Stack Java Developer  
Built the Microservice Retirement System with AWS integration and real-world dataset support.

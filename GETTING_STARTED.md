# 🚀 Getting Started - Microservice Retirement System

## **Where to Start** 📍

Your project is located at:
```
p:\Games\MicroserviceRetirement-System\
```

---

## **Prerequisites** ✅

Make sure you have:
- **Java 17+** installed (check: `java -version`)
- **Maven 3.9+** installed (check: `mvn -version`)
- **Project already built** (shaded jar created)

**Verify setup:**
```powershell
java -version
mvn -version
```

---

## **Quick Start (3 Commands)** 🎯

### **1. Open Terminal & Navigate to Project**
```powershell
cd p:\Games\MicroserviceRetirement-System
```

### **2. Run with Simulated Data (Demo)**
```powershell
java -cp target/microservice-retirement-system-1.0.0-shaded.jar com.cloudnative.retirement.aws.LocalTest
```
**Result:** Shows 5 simulated services analyzed

### **3. Run with Real Data (Google/Alibaba/SaaS)**

**Google Cluster Data:**
```powershell
cp google_cluster_data.csv microservice_metrics.csv
java -cp target/microservice-retirement-system-1.0.0-shaded.jar com.cloudnative.retirement.aws.RealWorldDataTest csv
```

**Alibaba Cluster Data:**
```powershell
cp alibaba_cluster_data.csv microservice_metrics.csv
java -cp target/microservice-retirement-system-1.0.0-shaded.jar com.cloudnative.retirement.aws.RealWorldDataTest csv
```

**SaaS Startup Data:**
```powershell
cp saas_startup_data.csv microservice_metrics.csv
java -cp target/microservice-retirement-system-1.0.0-shaded.jar com.cloudnative.retirement.aws.RealWorldDataTest csv
```

---

## **Step-by-Step First Run** 📝

### **Step 1: Open PowerShell**
Press `Win + R`, type `powershell`, press Enter

### **Step 2: Navigate to Project**
```powershell
cd p:\Games\MicroserviceRetirement-System
```

### **Step 3: Check Files Are Ready**
```powershell
ls *.csv
ls target/microservice-retirement-system-1.0.0-shaded.jar
```

You should see:
```
google_cluster_data.csv
alibaba_cluster_data.csv
saas_startup_data.csv
microservice_metrics.csv
target/microservice-retirement-system-1.0.0-shaded.jar
```

### **Step 4: Run First Test (Simulated)**
```powershell
java -cp target/microservice-retirement-system-1.0.0-shaded.jar com.cloudnative.retirement.aws.LocalTest
```

**Expected Output:**
```
=== Microservice Retirement System - Local Test ===
Analyzing 5 services...

Service: user-auth-service
  - Requests/sec: 1500
  - SLA Score: 0.99
  - Dependents: 8
  - Utility Score: 0.85
  → DECISION: RETAIN (has critical dependencies)

... (more services)

Summary:
- Services Analyzed: 5
- Services Retired: 2 (40%)
- Total Dependencies: 20
```

### **Step 5: Run with Real Data**
```powershell
cp google_cluster_data.csv microservice_metrics.csv
java -cp target/microservice-retirement-system-1.0.0-shaded.jar com.cloudnative.retirement.aws.RealWorldDataTest csv
```

**Expected Output:**
```
=== Microservice Retirement System - Real-World Data Analysis ===
Loading services from CSV...
✓ Loaded 17 services

Analyzing services...
Services Analyzed: 17
Services Retired: 5 (29.4%)
Total Dependencies: 162

Detailed Retirement Recommendations:
- goog-ancient-service-017: RETIRE (utility: 0.12, deps: 0)
- goog-old-api: RETIRE (utility: 0.18, deps: 0)
... (more services)

AWS Integration Status:
✓ CloudWatch: Ready to publish metrics
✓ DynamoDB: Ready to store decisions
✓ SNS: Ready to send notifications
✓ S3: Ready to archive logs
```

---

## **Key Files to Know** 📁

| File | Purpose |
|------|---------|
| `google_cluster_data.csv` | Real Google cluster data (17 services) |
| `alibaba_cluster_data.csv` | Real Alibaba cluster data (20 services) |
| `saas_startup_data.csv` | Real SaaS startup architecture (24 services) |
| `microservice_metrics.csv` | Active dataset (auto-loads when program runs) |
| `target/microservice-retirement-system-1.0.0-shaded.jar` | Executable Java jar file |
| `REAL_WORLD_TEST_RESULTS.md` | Analysis results for all 3 datasets |
| `AWS_INTEGRATION_GUIDE.md` | How AWS services work |

---

## **Run Everything Automatically** 🤖

Run all tests and demos in one command:
```powershell
.\run-all.bat
```

This executes:
1. ✅ LocalTest (simulated data - 5 services)
2. ✅ AWS Integration Example (6 feature demos)
3. ✅ Real-World Data Tests (Google, Alibaba, SaaS)
4. ✅ Unit Tests (10/10 passing)

---

## **If Files Don't Exist** ⚠️

### **If jar doesn't exist, rebuild:**
```powershell
cd p:\Games\MicroserviceRetirement-System
mvn clean install -DskipTests
```

### **If csv files are missing:**
They should be in the project root. Verify:
```powershell
ls *.csv
```

If not found, create them from project root.

---

## **Common Commands Reference** 📖

```powershell
# Navigate to project
cd p:\Games\MicroserviceRetirement-System

# List all csv data files
ls *.csv

# List available Java classes
ls src/main/java/com/cloudnative/retirement/**/*.java

# View analysis for Google data
cp google_cluster_data.csv microservice_metrics.csv
java -cp target/microservice-retirement-system-1.0.0-shaded.jar com.cloudnative.retirement.aws.RealWorldDataTest csv

# View analysis for Alibaba data
cp alibaba_cluster_data.csv microservice_metrics.csv
java -cp target/microservice-retirement-system-1.0.0-shaded.jar com.cloudnative.retirement.aws.RealWorldDataTest csv

# View analysis for SaaS data
cp saas_startup_data.csv microservice_metrics.csv
java -cp target/microservice-retirement-system-1.0.0-shaded.jar com.cloudnative.retirement.aws.RealWorldDataTest csv

# Rebuild project
mvn clean install -DskipTests

# Run unit tests
mvn test

# Clean old builds
mvn clean
```

---

## **What Each Run Shows** 📊

### **LocalTest Output:**
- 5 simulated microservices
- Service names, request volume, SLA scores
- Calculated utility scores
- Retirement recommendations
- AWS integration status

### **RealWorldDataTest Output:**
- Number of services loaded from CSV
- Service-by-service analysis
- Utility score calculation
- Retirement decisions
- Cost savings estimate
- AWS services ready status

---

## **Output Interpretation** 🔍

When you see this:
```
Service: payment-processor
  - Requests/sec: 5800
  - SLA Score: 0.9995
  - Dependents: 12
  - Utility Score: 0.89
  → DECISION: RETAIN (critical service)
```

It means:
- ✅ Service is used heavily (5800 requests/second)
- ✅ Service is reliable (99.95% SLA)
- ✅ Other services depend on it (12 dependents)
- ✅ System calculated it's valuable (0.89 utility)
- ✅ **System recommends keeping it**

When you see this:
```
Service: legacy-api-v1
  - Requests/sec: 15
  - SLA Score: 0.45
  - Dependents: 0
  - Utility Score: 0.12
  → DECISION: RETIRE ⚠️ (can be safely removed)
```

It means:
- ⚠️ Service is barely used (15 requests/second)
- ⚠️ Service is unreliable (45% SLA)
- ⚠️ Nothing depends on it (0 dependents)
- ⚠️ System calculated it's not valuable (0.12 utility)
- ✅ **System recommends retiring it (safe removal)**

---

## **Troubleshooting** 🔧

### **"Command not found: java"**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.18"
java -version
```

### **"File not found: microservice_metrics.csv"**
```powershell
cp google_cluster_data.csv microservice_metrics.csv
```

### **"BUILD FAILURE" when rebuilding**
```powershell
mvn clean install -DskipTests
```

### **Want to see full output (longer than screen)?**
```powershell
cp google_cluster_data.csv microservice_metrics.csv
java -cp target/microservice-retirement-system-1.0.0-shaded.jar com.cloudnative.retirement.aws.RealWorldDataTest csv > output.txt
cat output.txt
```

---

## **Next Steps** 📚

1. ✅ **Run LocalTest** - See it work with simulated data
2. ✅ **Run with Google Data** - See real production analysis
3. ✅ **Run with Alibaba Data** - See different architecture
4. ✅ **Run with SaaS Data** - See startup pattern
5. 📖 **Read Results** - Check `REAL_WORLD_TEST_RESULTS.md`
6. 🔧 **Explore Code** - Look at source files in `src/`
7. 🌐 **AWS Integration** - Read `AWS_INTEGRATION_GUIDE.md`

---

## **One-Minute Quick Start** ⚡

```powershell
cd p:\Games\MicroserviceRetirement-System
java -cp target/microservice-retirement-system-1.0.0-shaded.jar com.cloudnative.retirement.aws.LocalTest
```

Done! You should see 5 services analyzed and 2 retirements recommended.

---

**Ready to explore? Start with the LocalTest command above!** 🚀

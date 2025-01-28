# Aurora
Distributed Task Scheduler

# 🌟 Overview
Aurora is a distributed task scheduling system designed for high reliability and scalability. It provides automatic failover, task recovery, and distributed coordination across multiple nodes using Apache ZooKeeper.

## Key Features to be Implemented

- Distributed Coordination: Leader election ensures reliable task distribution
- Automatic Failover: Seamless recovery from node failures
- Priority-based Scheduling: Support for task prioritization
- Retry Mechanism: Configurable retry policies with exponential backoff
- Task Recovery: Automatic task recovery after system failures
- Monitoring & Metrics: Built-in monitoring for task execution and system health
- Pluggable Task System: Easy integration of custom task types
- Scalable Architecture: Horizontal scaling with dynamic worker registration

# 🚀 Getting Started

## Prerequisites

- Java 17 or higher
- Apache ZooKeeper 3.7+
- Maven 3.6+
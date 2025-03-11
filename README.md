# DRIVER-RIDER-springboot-


# Java-based Ride-Hailing System

A Spring Boot-based ride-hailing system where riders can find available drivers, start rides, stop rides, and calculate fares.

## Problem Statement

The goal of this project is to build a ride-hailing system that allows users (riders) to:

- Book rides
- Match with the nearest available driver
- Start and stop rides
- Calculate fares based on distance and time

The system should also handle various edge cases like drivers not being available, duplicate ride IDs, and stopping non-existent rides.

## Features

- Add and manage drivers and riders
- Assign the nearest available driver to a rider
- Start and stop rides
- Calculate ride fares based on distance and time
- Handle scenarios like driver not available, duplicate ride ID, stopping a non-existent ride
- REST API endpoints for managing rides and drivers
- Database integration with MySQL using Spring Data JPA
- Automated table creation using Hibernate

## Prerequisites

Before running the project, ensure you have the following installed:

- Java 17 or later (JDK 17 recommended)
- Maven (for dependency management and building the project)
- Spring Boot (for backend development)
- MySQL (for database storage)
- Postman or cURL (for API testing)
- An IDE (IntelliJ IDEA, Eclipse, or VS Code) for development
- Git (optional, for version control)

## Project Structure

```
src/
│── main/
│   ├── controllers/   # Handles ride-related commands
│   ├── models/        # Entity classes (Driver, Rider, Ride)
│   ├── repository/    # Data storage (Spring Data JPA Repositories)
│   ├── services/      # Business logic (RideManager, DriverManager, BillingCalculator, DistanceCalculator)
│── test/             # JUnit test cases
```

## Sample Input & Output

### Sample Input:
```
ADD_DRIVER D1 1 1
ADD_DRIVER D2 4 5
ADD_RIDER R1 0 0
MATCH R1
START_RIDE RIDE-001 D2 R1
STOP_RIDE RIDE-001 4 5 32
BILL RIDE-001
```

### Expected Output:
```
DRIVERS_MATCHED D1 D2
RIDE_STARTED RIDE-001
RIDE_STOPPED RIDE-001
BILL RIDE-001 D2 186.72
```


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
- Discount for frequent riders
- Allow riders to rate drivers after completing a ride
- Let riders mark a driver as “preferred” for future rides
- Automatically prioritize preferred drivers during ride assignment if available

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
│   ├── services/      # Business logic (RideManager, DriverManager, BillingCalculator, DistanceCalculator,Ride service)
│── test/             # JUnit test cases
```

**Sample cURL API Calls**

**Add a driver**
```bash
curl -X POST "http://localhost:8080/drivers/add" \
     -H "Content-Type: application/json" \
     -d '{"id":"D1","x":1,"y":1}'
```
**Add a rider**
```bash
curl -X POST "http://localhost:8080/riders/add" \
     -H "Content-Type: application/json" \
     -d '{"id":"R1","name":"John Doe","pickupLocation":"37.7749,-122.4194"}'
```
**Match drivers for a rider**
```bash
curl -X GET "http://localhost:8080/rides/match?riderId=R1"
```
**start a ride (auto-select driver or use preferred driver)**
```bash
curl -X POST "http://localhost:8080/rides/start?riderId=R1"
```
**Start a ride with specific driver**
```bash
curl -X POST "http://localhost:8080/rides/start?riderId=R1&driverId=D1"
```
**Stop a ride**
```bash
curl -X POST "http://localhost:8080/rides/stop?rideId=RIDE-001&destX=4&destY=5&Timetaken=32"
```
**Get bill for a ride**
```bash
curl -X GET "http://localhost:8080/rides/bill?rideId=RIDE-001"
```
## Sample Input & Output

**Database Schema Overview**

Rider Table
| Field               | Type         | Null | Key | Default | Extra |
|--------------------|--------------|------|-----|---------|-------|
| id                 | varchar(255) | NO   | PRI | NULL    |       |
| x                  | int          | NO   |     | NULL    |       |
| y                  | int          | NO   |     | NULL    |       |
| preferred_driver   | varchar(255) | YES  |     | NULL    |       |
| discount_percentage| int          | YES  |     | 0       |       |
| num_rides          | int          | YES  |     | 0       |       |
| preferred_driver_id| varchar(255) | YES  |     | NULL    |       |

Driver Table
| Field         | Type         | Null | Key | Default | Extra |
|--------------|--------------|------|-----|---------|-------|
| id           | varchar(255) | NO   | PRI | NULL    |       |
| available    | bit(1)       | NO   |     | NULL    |       |
| x_coordinate | int          | NO   |     | NULL    |       |
| y_coordinate | int          | NO   |     | NULL    |       |
| rating       | float        | YES  |     | NULL    |       |
| total_ratings| int          | YES  |     | 0       |       |

Ride Table
| Field               | Type         | Null | Key | Default | Extra |
|--------------------|--------------|------|-----|---------|-------|
| ride_id            | varchar(255) | NO   | PRI | NULL    |       |
| active             | bit(1)       | NO   |     | NULL    |       |
| startx             | int          | NO   |     | NULL    |       |
| starty             | int          | NO   |     | NULL    |       |
| endx               | int          | NO   |     | NULL    |       |
| endy               | int          | NO   |     | NULL    |       |
| time_taken         | int          | NO   |     | NULL    |       |
| driver_id          | varchar(255) | YES  | MUL | NULL    |       |
| rider_id           | varchar(255) | YES  | MUL | NULL    |       |
| preferred_driver_id| varchar(255) | YES  |     | NULL    |       |

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


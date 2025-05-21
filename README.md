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

## Getting Started

### Clone the Repository

```sh
git clone https://github.com/1005vishnu/DRIVER-RIDER-springboot-.git
cd DRIVER-RIDER-springboot-
```

### Configure Environment Variables

1. Copy the example environment file:
   ```sh
   cp .env.example .env
   # Edit .env and set your local MySQL credentials (see .env.example for guidance)
   ```
2. **Important:** If you clone this repo or move it to a new environment, always update your `.env` file to match your local database and environment settings.

### Build the Project

```sh
mvn clean install
```

### Run Tests

```sh
mvn test
```

### Run the Application

#### Run Locally (with your own MySQL)

1. Start your local MySQL server (ensure the database and user match your `.env`).
2. Load environment variables and start the Spring Boot app:
   ```sh
   export $(grep -v '^#' .env | xargs)
   ./mvnw spring-boot:run
   ```
3. The API will be available at [http://localhost:8080](http://localhost:8080)

#### Run with Docker Compose (App + MySQL in containers)

1. Ensure Docker and Docker Compose are installed.
2. From the project root, build and start all services:
   ```sh
   docker-compose up --build
   ```


## Running the Application

### Run Locally (with your own MySQL)

1. **Copy and configure environment variables:**
   ```sh
   cp .env.example .env
   # Edit .env and set your local MySQL credentials (see .env.example for guidance)
   ```
2. **Start your local MySQL server** (make sure the database and user exist as in your .env).
3. **Load environment variables and start the Spring Boot app:**
   ```sh
   export $(grep -v '^#' .env | xargs)
   ./mvnw spring-boot:run
   ```
4. The API will be available at [http://localhost:8080](http://localhost:8080)

### Run with Docker Compose (App + MySQL in containers)

1. **Ensure Docker and Docker Compose are installed.**
2. **From the project root, build and start all services:**
   ```sh
   docker-compose up --build
   ```
3. This will start both the Spring Boot app and a MySQL database as containers. The app will automatically connect to the MySQL container using the credentials defined in `docker-compose.yml`.
4. The API will be available at [http://localhost:8080](http://localhost:8080)
5. **To stop the containers:**
   ```sh
   docker-compose down
   ```

- When running in Docker Compose, you do NOT need to run your own MySQL or export .env variables manually.
- When running locally, you must ensure your own MySQL is running and accessible.

## Local Development Setup

1. Copy `.env.example` to `.env` and fill in your local database credentials.
2. Load the environment variables and start the app:
   ```sh
   export $(grep -v '^#' .env | xargs)
   ./mvnw spring-boot:run
   ```

- Docker Compose will use its own environment variables automatically.

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

## Related Projects

You may also want to explore a similar ride-hailing project for reference:
- [Riding](https://github.com/VAMSIKRISHNA2210/Riding.git)

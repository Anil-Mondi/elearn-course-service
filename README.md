# Course Service

## Overview

The **Course Service** is responsible for managing all course-related operations within the E-Learn Microservices Platform. It provides APIs for creating, updating, retrieving, and managing courses while maintaining course metadata such as pricing, ratings, categories, and review statistics.

This service acts as the central repository for all learning content available on the platform.

---

## Responsibilities

* Course Management
* Course CRUD Operations
* Course Details
* Course Categories
* Course Pricing
* Average Rating Calculation
* Review Count Management
* Course Search
* Course Filtering
* Course Sorting
* Course Discovery
* Publish Course Events (Future)

---

## Technology Stack

* Java 17
* Spring Boot 3
* Spring Data JPA
* Spring Cloud Eureka Client
* Spring Cloud OpenFeign
* H2 Database (Current)
* MySQL (Future)
* Apache Kafka
* Spring Boot Actuator
* OpenAPI / Swagger

---

## Features

### Course Management

* Create Course
* Update Course
* Delete Course
* Get Course By ID
* Get All Courses

---

### Course Discovery

* Search Courses
* Filter by Category
* Sort by Rating
* Sort by Price
* Browse Available Courses

---

### Course Statistics

* Average Rating
* Review Count
* Course Price
* Category Information

---

## Architecture

```text
                Angular Frontend
                        │
                        ▼
                  API Gateway
                        │
                        ▼
                  Course Service
                        │
             ┌──────────┴──────────┐
             ▼                     ▼
        Course Database      Review Service
                                     │
                                     ▼
                              Average Rating
                              Review Count
```

---

## REST APIs

### Course Management

| Method | Endpoint            | Description      |
| ------ | ------------------- | ---------------- |
| GET    | `/api/courses`      | Get All Courses  |
| GET    | `/api/courses/{id}` | Get Course By ID |
| POST   | `/api/courses`      | Create Course    |
| PUT    | `/api/courses/{id}` | Update Course    |
| DELETE | `/api/courses/{id}` | Delete Course    |

---

## Database

Current Database

* H2 Database

Future Migration

* MySQL

Future Improvements

* Flyway Migration
* Optimized Indexing
* Course Analytics
* Audit Fields

---

## Service Communication

The Course Service communicates with other microservices using Spring Cloud OpenFeign.

Current integrations include:

* Review Service
* Purchase Service (Future)
* Analytics Service (Future)

---

## Eureka Integration

The service automatically registers with Eureka Server.

Service Name:

```text
COURSE-SERVICE
```

This allows dynamic service discovery without hardcoded URLs.

---

## Monitoring

Spring Boot Actuator endpoints are enabled.

Available endpoints:

* `/actuator/health`
* `/actuator/info`
* `/actuator/prometheus`
* `/actuator/metrics`

---

## Future Enhancements

* Course Thumbnail Upload
* Course Videos
* Chapters & Lessons
* Course Attachments
* Instructor Profiles
* Course Difficulty Levels
* Course Duration
* Course Recommendations
* Popular Courses
* Redis Caching
* Elasticsearch Search
* Kafka Event Publishing

---

## Project Structure

```text
course-service
│
├── controller
├── service
├── repository
├── entity
├── dto
├── mapper
├── exception
├── config
├── util
├── constant
├── resources
└── CourseServiceApplication.java
```

---

## Role in E-Learn Platform

The Course Service is the core content management service of the E-Learn platform. It manages course information, pricing, categories, ratings, and metadata while providing searchable and scalable APIs for learners to discover educational content.

---

## Future Event Flow

```text
Create Course
      │
      ▼
Course Service
      │
      ▼
Kafka Event
      │
      ▼
Analytics Service
      │
      ▼
Update Course Statistics
```

---

## Future Architecture

```text
                API Gateway
                     │
                     ▼
              Course Service
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
   MySQL        Redis Cache     Kafka
        │                         │
        ▼                         ▼
Course Metadata         Analytics Service
```

---

## Author

**Anil Mondi**

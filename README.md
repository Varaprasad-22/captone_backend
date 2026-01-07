# 🎫 Smart Ticket Management System

A **Spring Boot–based Microservices Ticketing Platform** designed for enterprise-grade issue tracking, SLA enforcement, notifications, and role-based access.

---

## 📌 Key Features

- Role-based access (**User, Agent, Manager, Admin**)
- Ticket lifecycle management
- SLA tracking & escalation
- Email notifications via RabbitMQ
- Centralized configuration using **Spring Cloud Config**
- Service discovery via **Eureka**
- API Gateway routing
- Modular microservice architecture

---

## 🧱 Architecture Overview

### Core Components
- **API Gateway** – Central entry point & routing
- **Eureka Server** – Service discovery
- **Config Server** – Centralized configuration
- **Auth Service** – Authentication & authorization
- **Ticket Service** – Ticket creation & lifecycle
- **Assignment Service** – Agent assignment & SLA enforcement
- **Notification Service** – Email notifications

---

## 🛠 Tech Stack

| Layer | Technology |
|-----|-----------|
| Language | Java 17 |
| Framework | Spring Boot 4.x |
| Config | Spring Cloud Config |
| Discovery | Eureka |
| Messaging | RabbitMQ |
| Database | MySQL, MongoDb |
| Security | Spring Security + JWT |
| Build Tool | Maven |

---

## 📁 Project Structure

ticket_system/
│
├── api-gateway/ # Central API routing & security
├── eureka-server/ # Service discovery
├── config-server/ # Centralized configuration
├── auth-service/ # Authentication & authorization
├── ticket-service/ # Ticket creation & lifecycle management
├── assignment-service/ # Agent assignment & SLA enforcement
├── notification-service/ # Email notifications
│
└── README.md

### 📌 Module Responsibilities

- **api-gateway**  
  Acts as the single entry point for all client requests. It handles routing, security checks, and forwards requests to appropriate services.

- **eureka-server**  
  Enables service discovery, allowing microservices to dynamically register and communicate with each other.

- **config-server**  
  Provides centralized configuration for all services, ensuring consistent and manageable environment-specific settings.

- **auth-service**  
  Manages user registration, authentication, and role-based authorization using JWT.

- **ticket-service**  
  Handles ticket creation, updates, status changes, comments, and attachments.

- **assignment-service**  
  Responsible for assigning tickets to agents and enforcing SLA rules such as escalation and breach detection.

- **notification-service**  
  Listens to system events and sends email notifications. Also maintains notification logs for audit purposes.

---

This modular structure ensures:
- Clear separation of responsibilities  
- Independent service development and testing  
- Easy scalability and future extensibility  


### Start Infrastructure Services

```bash
git clone https://github.com/Varaprasad-22/captone_backend.git
cd capstone-backend
```
### Build the Project

Using the root pom.xml:

```bash
mvn clean install 
```
```bash
cd eureka-server
mvn spring-boot:run
```
```bash
cd config-server
mvn spring-boot:run
```
wait till above servers load

Now Start the rest
```bash
cd api-gateway
mvn spring-boot:run
```
```bash
cd assignment-service
mvn spring-boot:run
```
```bash
cd auth-service
mvn spring-boot:run
```
```bash
cd notification-service
mvn spring-boot:run
```
```bash
cd ticket-service
mvn spring-boot:run
```

## 📑 API Documentation

This section describes the REST APIs exposed by each microservice.  
All APIs are accessed through the **API Gateway** and secured using **JWT-based authentication**, unless marked as public.

---

### 🔐 Auth Service

The **Auth Service** is responsible for user registration, authentication, and user management.  
It issues **JWT tokens** that are required to access protected APIs across the system.

#### Base Path

#### Available Endpoints

| Method | Endpoint | Description | Access Level |
|------|---------|-------------|--------------|
| POST | `/auth/register` | Register a new user in the system | Public |
| POST | `/auth/login` | Authenticate user and issue JWT token | Public |
| GET | `/auth/getAll` | Retrieve all registered users | Admin |

#### Notes
- Public endpoints do **not** require authentication
- Protected endpoints require a valid **JWT token**
- Few APIs enforce **role-based authorization**

## 🧪 Testing & Build Verification

This project follows standard **Maven build lifecycle practices** to ensure code quality, stability, and correctness.

---

### ▶ Running Tests

Use the following command to execute all **unit tests** in the project:

```bash
mvn test
```
### ▶ Verifying the Build

Use the `verify` phase to validate the **complete Maven build lifecycle**:

```bash
mvn clean verify
```
Code quality validations (JaCoCo)

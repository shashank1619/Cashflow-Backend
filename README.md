# 💰 Cashflow - Expense Monitoring System

A comprehensive **Cash Flow Monitoring System** built with **Java Spring Boot** and **REST APIs** following microservice architecture. This application enables users to track their expenses, manage income credits, set spending thresholds, and receive alerts when limits are exceeded.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [Getting Started](#-getting-started)
- [API Examples](#-api-examples)

---

## ✨ Features

- **User Management**: Register, authenticate, and manage user accounts
- **Expense Tracking**: Log expenses with categories, dates, and payment methods
- **Category Management**: Organize expenses into customizable categories with default templates
- **Credit/Income Tracking**: Track income sources and credits
- **Threshold Alerts**: Set spending limits and receive breach notifications
- **Financial Summary**: View overall expense summaries with category breakdowns
- **Date Range Reports**: Generate expense reports for specific periods

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                              │
│                   (React.js + Bootstrap)                         │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                      REST API LAYER                              │
│  ┌──────────────┬──────────────┬──────────────┬──────────────┐  │
│  │    User      │   Expense    │   Category   │   Threshold  │  │
│  │  Controller  │  Controller  │  Controller  │  Controller  │  │
│  └──────────────┴──────────────┴──────────────┴──────────────┘  │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                    Credit Controller                        │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER                               │
│  ┌────────────┬────────────┬────────────┬────────────────────┐  │
│  │   User     │  Expense   │  Category  │    Threshold       │  │
│  │  Service   │  Service   │  Service   │    Service         │  │
│  └────────────┴────────────┴────────────┴────────────────────┘  │
│  ┌────────────────────────┬────────────────────────────────────┐ │
│  │     Credit Service     │         Alert Service              │ │
│  └────────────────────────┴────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                    REPOSITORY LAYER (JPA)                        │
│  ┌────────────┬────────────┬────────────┬────────────────────┐  │
│  │   User     │  Expense   │  Category  │    Threshold       │  │
│  │   Repo     │   Repo     │   Repo     │      Repo          │  │
│  └────────────┴────────────┴────────────┴────────────────────┘  │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                     Credit Repository                       │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DATABASE LAYER                              │
│                    ┌──────────────────┐                          │
│                    │   MySQL / H2     │                          │
│                    └──────────────────┘                          │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🛠 Tech Stack

| Technology | Purpose |
|------------|---------|
| **Java 17** | Core programming language |
| **Spring Boot 3.2** | Application framework |
| **Spring Data JPA** | Data persistence |
| **Hibernate** | ORM for database operations |
| **MySQL** | Production database |
| **H2** | Development/Testing database |
| **Lombok** | Reducing boilerplate code |
| **Maven** | Build and dependency management |

---

## 📁 Project Structure

```
Cashflow-Backend/
├── src/
│   └── main/
│       ├── java/com/cashflow/
│       │   ├── CashflowApplication.java          # Main entry point
│       │   ├── controller/                        # REST Controllers
│       │   │   ├── UserController.java
│       │   │   ├── ExpenseController.java
│       │   │   ├── CategoryController.java
│       │   │   ├── CreditController.java
│       │   │   └── ThresholdController.java
│       │   ├── service/                           # Business Logic
│       │   │   ├── UserService.java
│       │   │   ├── ExpenseService.java
│       │   │   ├── CategoryService.java
│       │   │   ├── CreditService.java
│       │   │   ├── ThresholdService.java
│       │   │   └── AlertService.java
│       │   ├── repository/                        # Data Access
│       │   │   ├── UserRepository.java
│       │   │   ├── ExpenseRepository.java
│       │   │   ├── CategoryRepository.java
│       │   │   ├── CreditRepository.java
│       │   │   └── ThresholdRepository.java
│       │   ├── model/                             # Entity Classes
│       │   │   ├── User.java
│       │   │   ├── Expense.java
│       │   │   ├── Category.java
│       │   │   ├── Credit.java
│       │   │   └── Threshold.java
│       │   ├── dto/                               # Data Transfer Objects
│       │   │   ├── ApiResponse.java
│       │   │   ├── UserDTO.java
│       │   │   ├── ExpenseDTO.java
│       │   │   ├── CategoryDTO.java
│       │   │   ├── CreditDTO.java
│       │   │   ├── ThresholdDTO.java
│       │   │   ├── AlertDTO.java
│       │   │   └── ExpenseSummaryDTO.java
│       │   └── exception/                         # Exception Handling
│       │       ├── GlobalExceptionHandler.java
│       │       ├── ResourceNotFoundException.java
│       │       ├── DuplicateResourceException.java
│       │       └── ThresholdBreachedException.java
│       └── resources/
│           └── application.properties             # Configuration
├── pom.xml                                        # Maven dependencies
├── .gitignore
└── README.md
```

---

## 📡 API Documentation

### Base URL
```
http://localhost:8080/api
```

### User APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/users/register` | Register a new user |
| `GET` | `/users/{id}` | Get user by ID |
| `GET` | `/users/username/{username}` | Get user by username |
| `GET` | `/users` | Get all users |
| `PUT` | `/users/{id}` | Update user |
| `DELETE` | `/users/{id}` | Delete user |
| `PATCH` | `/users/{id}/deactivate` | Deactivate user |

### Expense APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/expenses` | Add new expense |
| `GET` | `/expenses/{id}` | Get expense by ID |
| `GET` | `/expenses/user/{userId}` | Get all user expenses |
| `GET` | `/expenses/user/{userId}/summary` | **userOverAllExpense** - Get expense summary |
| `GET` | `/expenses/user/{userId}/category/{categoryId}` | **categoryExpense** - Get expenses by category |
| `GET` | `/expenses/user/{userId}/summary/range` | Get summary by date range |
| `PUT` | `/expenses/{id}` | Update expense |
| `DELETE` | `/expenses/{id}` | Delete expense |

### Category APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/categories` | Create new category |
| `GET` | `/categories/{id}` | Get category by ID |
| `GET` | `/categories/user/{userId}` | **expenseCategories** - Get user's categories |
| `PUT` | `/categories/{id}` | Update category |
| `DELETE` | `/categories/{id}` | Delete category |
| `POST` | `/categories/user/{userId}/defaults` | Create default categories |

### Credit APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/credits` | Add new credit |
| `GET` | `/credits/{id}` | Get credit by ID |
| `GET` | `/credits/user/{userId}` | **userCredit** - Get user's credits |
| `GET` | `/credits/user/{userId}/total` | Get total credits |
| `GET` | `/credits/user/{userId}/range` | Get credits by date range |
| `PUT` | `/credits/{id}` | Update credit |
| `DELETE` | `/credits/{id}` | Delete credit |

### Threshold APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/thresholds` | **setThreshold** - Set expense threshold |
| `GET` | `/thresholds/{id}` | Get threshold by ID |
| `GET` | `/thresholds/user/{userId}` | Get all user thresholds |
| `GET` | `/thresholds/user/{userId}/active` | Get active thresholds |
| `GET` | `/thresholds/alerts/{userId}` | **thresholdBreachedAlert** - Get breach alerts |
| `GET` | `/thresholds/check/{userId}` | Check threshold status |
| `PUT` | `/thresholds/{id}` | Update threshold |
| `DELETE` | `/thresholds/{id}` | Delete threshold |
| `PATCH` | `/thresholds/{id}/toggle` | Toggle threshold active status |

---

## 🗄 Database Schema

```sql
-- Users Table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone_number VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Categories Table
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    icon_name VARCHAR(50),
    color_code VARCHAR(10),
    is_default BOOLEAN DEFAULT FALSE,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Expenses Table
CREATE TABLE expenses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    expense_date DATE NOT NULL,
    payment_method VARCHAR(50),
    merchant_name VARCHAR(100),
    is_recurring BOOLEAN DEFAULT FALSE,
    recurring_frequency VARCHAR(20),
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Credits Table
CREATE TABLE credits (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    amount DECIMAL(10,2) NOT NULL,
    source VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    credit_date DATE NOT NULL,
    credit_type VARCHAR(50),
    is_recurring BOOLEAN DEFAULT FALSE,
    recurring_frequency VARCHAR(20),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Thresholds Table
CREATE TABLE thresholds (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    limit_amount DECIMAL(10,2) NOT NULL,
    threshold_type VARCHAR(20) DEFAULT 'MONTHLY',
    alert_percentage INT DEFAULT 80,
    is_active BOOLEAN DEFAULT TRUE,
    is_breached BOOLEAN DEFAULT FALSE,
    last_alert_sent TIMESTAMP,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ (optional, H2 included for development)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/Cashflow-Backend.git
cd Cashflow-Backend
```

2. **Configure Database** (Optional - for MySQL)
```properties
# Edit src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/cashflow_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. **Build the project**
```bash
mvn clean install
```

4. **Run the application**
```bash
mvn spring-boot:run
```

5. **Access the API**
- API: `http://localhost:8080/api`
- H2 Console: `http://localhost:8080/h2-console`

---

## 📝 API Examples

### Register User
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Add Expense
```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50.00,
    "description": "Lunch at restaurant",
    "categoryId": 1,
    "userId": 1,
    "expenseDate": "2024-01-15"
  }'
```

### Set Threshold
```bash
curl -X POST http://localhost:8080/api/thresholds \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "categoryId": 1,
    "limitAmount": 500.00,
    "thresholdType": "MONTHLY",
    "alertPercentage": 80
  }'
```

### Get Threshold Alerts
```bash
curl http://localhost:8080/api/thresholds/alerts/1
```

---

## 📄 License

This project is licensed under the MIT License.

---

## 👨‍💻 Author

**Shashank Kumar**

---

## 🤝 Contributing

Contributions, issues and feature requests are welcome!
// README update

## Version History
- v1.1.0 - September 2024
- v1.0.0 - June 2024

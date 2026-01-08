# 💰 Cashflow - Expense Monitoring System

A comprehensive **Cash Flow Monitoring System** with a **Java Spring Boot** backend and **React** frontend. This full-stack application enables users to track expenses, manage income, set spending thresholds, and receive alerts when limits are exceeded.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![React](https://img.shields.io/badge/React-18.2-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-336791)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Frontend Setup](#-frontend-setup)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [API Examples](#-api-examples)
- [Screenshots](#-screenshots)

---

## ✨ Features

### Backend Features
- **User Management**: Register, authenticate, and manage user accounts
- **Expense Tracking**: Log expenses with categories, dates, and payment methods
- **Category Management**: Organize expenses into customizable categories
- **Credit/Income Tracking**: Track income sources and credits
- **Threshold Alerts**: Set spending limits and receive breach notifications
- **Financial Summary**: View expense summaries with category breakdowns

### Frontend Features
- **Modern UI**: Clean, responsive interface with dark theme
- **Dashboard**: Visual overview of finances with statistics cards
- **CRUD Operations**: Full create, read, update, delete for all entities
- **Real-time Alerts**: Threshold breach notifications
- **Form Validation**: Client-side validation for all inputs
- **Responsive Design**: Works on desktop and mobile devices

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    FRONTEND (React + Vite)                       │
│  ┌──────────────┬──────────────┬──────────────┬──────────────┐  │
│  │   Login      │   Dashboard  │   Expenses   │  Categories  │  │
│  │   Register   │   Credits    │  Thresholds  │   Sidebar    │  │
│  └──────────────┴──────────────┴──────────────┴──────────────┘  │
│                         API Service (Axios)                       │
└─────────────────────────────────────────────────────────────────┘
                               │ HTTP
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                    BACKEND (Spring Boot)                         │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                    REST Controllers                        │   │
│  └──────────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                    Service Layer                           │   │
│  └──────────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                  Repository Layer (JPA)                    │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                      PostgreSQL Database                         │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🛠 Tech Stack

### Backend
| Technology | Purpose |
|------------|---------|
| **Java 17** | Core programming language |
| **Spring Boot 3.2** | Application framework |
| **Spring Data JPA** | Data persistence |
| **Hibernate** | ORM for database operations |
| **PostgreSQL** | Production database |
| **Lombok** | Reducing boilerplate code |
| **Maven** | Build and dependency management |

### Frontend
| Technology | Purpose |
|------------|---------|
| **React 18** | UI library |
| **Vite** | Build tool and dev server |
| **React Router 6** | Client-side routing |
| **Axios** | HTTP client for API calls |
| **CSS3** | Styling with custom properties |

---

## 📁 Project Structure

```
Cashflow-Backend/
├── src/main/java/com/cashflow/
│   ├── CashflowApplication.java
│   ├── controller/
│   │   ├── UserController.java
│   │   ├── ExpenseController.java
│   │   ├── CategoryController.java
│   │   ├── CreditController.java
│   │   └── ThresholdController.java
│   ├── service/
│   │   ├── UserService.java
│   │   ├── ExpenseService.java
│   │   ├── CategoryService.java
│   │   ├── CreditService.java
│   │   ├── ThresholdService.java
│   │   └── AlertService.java
│   ├── repository/
│   ├── model/
│   ├── dto/
│   └── exception/
├── src/main/resources/
│   └── application.properties
├── frontend/
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── App.jsx
│       ├── main.jsx
│       ├── index.css
│       ├── components/
│       │   ├── Sidebar.jsx
│       │   ├── StatsCard.jsx
│       │   └── AlertBanner.jsx
│       ├── pages/
│       │   ├── Login.jsx
│       │   ├── Register.jsx
│       │   ├── Dashboard.jsx
│       │   ├── Categories.jsx
│       │   ├── Expenses.jsx
│       │   ├── Credits.jsx
│       │   └── Thresholds.jsx
│       └── services/
│           └── api.js
├── pom.xml
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Node.js 18+ and npm
- PostgreSQL 14+

### Backend Setup

1. **Clone the repository**
```bash
git clone https://github.com/shashank1619/Cashflow-Backend.git
cd Cashflow-Backend
```

2. **Create PostgreSQL database**
```bash
psql -U postgres -c "CREATE DATABASE cashflow_db;"
```

3. **Configure database** (edit `src/main/resources/application.properties`)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cashflow_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

4. **Build and run**
```bash
mvn clean install
mvn spring-boot:run
```

5. **Access the API**: `http://localhost:8080/api`

---

## 🎨 Frontend Setup

1. **Navigate to frontend directory**
```bash
cd frontend
```

2. **Install dependencies**
```bash
npm install
```

3. **Start development server**
```bash
npm run dev
```

4. **Access the app**: `http://localhost:3000`

### Frontend Pages

| Page | Route | Description |
|------|-------|-------------|
| Login | `/` | User authentication |
| Register | `/register` | New user registration |
| Dashboard | `/dashboard` | Financial overview with stats |
| Categories | `/categories` | Manage expense categories |
| Expenses | `/expenses` | Track and manage expenses |
| Credits | `/credits` | Track income and credits |
| Thresholds | `/thresholds` | Set spending limits |

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
| `POST` | `/users/login` | Authenticate user |
| `GET` | `/users/{id}` | Get user by ID |
| `PUT` | `/users/{id}` | Update user |
| `DELETE` | `/users/{id}` | Delete user |

### Expense APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/expenses` | Add new expense |
| `GET` | `/expenses/user/{userId}` | Get user expenses |
| `GET` | `/expenses/user/{userId}/summary` | Get expense summary |
| `PUT` | `/expenses/{id}` | Update expense |
| `DELETE` | `/expenses/{id}` | Delete expense |

### Category APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/categories` | Create category |
| `GET` | `/categories/user/{userId}` | Get user categories |
| `PUT` | `/categories/{id}` | Update category |
| `DELETE` | `/categories/{id}` | Delete category |

### Credit APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/credits` | Add new credit |
| `GET` | `/credits/user/{userId}` | Get user credits |
| `GET` | `/credits/user/{userId}/total` | Get total credits |
| `PUT` | `/credits/{id}` | Update credit |
| `DELETE` | `/credits/{id}` | Delete credit |

### Threshold APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/thresholds` | Set threshold |
| `GET` | `/thresholds/user/{userId}` | Get user thresholds |
| `GET` | `/thresholds/alerts/{userId}` | Get breach alerts |
| `PUT` | `/thresholds/{id}` | Update threshold |
| `DELETE` | `/thresholds/{id}` | Delete threshold |

---

## 🗄 Database Schema

```sql
-- Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
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
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP
);

-- Expenses Table
CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    expense_date DATE NOT NULL,
    user_id BIGINT REFERENCES users(id),
    category_id BIGINT REFERENCES categories(id),
    created_at TIMESTAMP
);

-- Credits Table
CREATE TABLE credits (
    id BIGSERIAL PRIMARY KEY,
    amount DECIMAL(10,2) NOT NULL,
    source VARCHAR(100) NOT NULL,
    credit_date DATE NOT NULL,
    user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP
);

-- Thresholds Table
CREATE TABLE thresholds (
    id BIGSERIAL PRIMARY KEY,
    limit_amount DECIMAL(10,2) NOT NULL,
    threshold_type VARCHAR(20) DEFAULT 'MONTHLY',
    alert_percentage INT DEFAULT 80,
    is_active BOOLEAN DEFAULT TRUE,
    user_id BIGINT REFERENCES users(id),
    category_id BIGINT REFERENCES categories(id),
    created_at TIMESTAMP
);
```

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

### Login User
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

### Add Expense
```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50.00,
    "description": "Lunch",
    "categoryId": 1,
    "userId": 1,
    "expenseDate": "2024-10-15"
  }'
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

---

## Version History
- v1.2.0 - October 2024 (React Frontend + PostgreSQL)
- v1.1.0 - September 2024
- v1.0.0 - June 2024

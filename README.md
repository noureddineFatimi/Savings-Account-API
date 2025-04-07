# 💰 Savings Account Management API

A Spring Boot REST API for managing savings accounts in a banking system. This project provides endpoints for user management, contract creation, and withdrawal operations, all seamlessly connected to a MySQL database.

## ✨ Features

- 👤 **User Management**: Create, update, view, and delete user accounts  
- 📄 **Contract Management**: Establish and manage savings contracts with customizable terms  
- 💸 **Withdrawal Operations**: Process and track withdrawals with validation  
- 📘 **Swagger Documentation**: Interactive API documentation for easy testing and integration  
- 🗃️ **MySQL Database**: Persistent storage with relational data model  
- 🔐 **Security**: Authentication and authorization mechanisms  

## 🛠️ Tech Stack

- ☕ **Backend**: Java 11, Spring Boot 2.7  
- 🐬 **Database**: MySQL 8.0  
- 🔧 **Build Tool**: Maven  
- 📖 **Documentation**: Swagger UI / OpenAPI 3.0  
- 🛡️ **Security**: Spring Security with JWT authentication  

## 📸 Screenshots

### 📘 Swagger API Documentation
<p align="center">
  <img src="screenshots/swagger-overview.png" alt="Swagger API Documentation" width="700">
</p>
<p align="center"><em>Interactive API documentation with Swagger UI</em></p>

### 👥 User Endpoints
<p align="center">
  <img src="screenshots/user-endpoints.png" alt="User Endpoints" width="700">
</p>
<p align="center"><em>Complete user management functionality</em></p>

### 📄 Contract Operations
<p align="center">
  <img src="screenshots/contract-endpoints.png" alt="Contract Operations" width="700">
</p>
<p align="center"><em>Savings contract creation and management</em></p>

### 🗂️ Database Schema
<p align="center">
  <img src="screenshots/database-schema.png" alt="Database Schema" width="700">
</p>
<p align="center"><em>Relational model showing entities and relationships</em></p>

## 🚀 Installation & Setup

### ✅ Prerequisites
- ☕ Java 11 or higher  
- 🛠️ Maven 3.6 or higher  
- 🐬 MySQL 8.0  

### 🗃️ Database Setup
```sql
CREATE DATABASE cgf_db;

### Accessing Swagger UI
Open your browser and navigate to:
```
http://localhost:8080/swagger-ui/index.html


## 🔗 API Endpoints

### 👥 User Management
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### 📄 Contract Management
- `GET /api/contracts` - Get all contracts
- `GET /api/contracts/{id}` - Get contract by ID
- `POST /api/contracts` - Create new savings contract
- `PUT /api/contracts/{id}` - Update contract
- `DELETE /api/contracts/{id}` - Delete contract

### 💸 Withdrawal Operations
- `GET /api/withdrawals` - Get all withdrawals
- `GET /api/withdrawals/{id}` - Get withdrawal by ID
- `POST /api/withdrawals` - Process new withdrawal
- `PUT /api/withdrawals/{id}` - Update withdrawal status

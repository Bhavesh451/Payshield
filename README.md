# PayShield 💳

PayShield is a secure digital wallet and transaction management platform built using Spring Boot, MySQL and React.

## 🚀 Features

- User registration and login
- JWT-based authentication
- Role-based authorization
- Secure password hashing with BCrypt
- Digital wallet creation
- Deposit and withdrawal
- Wallet-to-wallet money transfer
- Transaction history
- Transaction reference IDs
- Transaction filtering
- Pagination
- Admin dashboard
- User and transaction management
- Global exception handling
- Input validation
- Swagger/OpenAPI documentation
- React frontend
- RESTful APIs
- CORS configuration

## 🛠️ Tech Stack

### Backend

- Java 17
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- MySQL
- Maven

### Frontend

- React
- Vite
- Axios
- JavaScript
- HTML
- CSS

### Tools

- IntelliJ IDEA
- MySQL
- Postman
- Swagger/OpenAPI
- Git
- GitHub

## 🏗️ Architecture

```text
                    ┌──────────────────────┐
                    │     React Frontend   │
                    │      Port: 5173      │
                    └──────────┬───────────┘
                               │
                         REST API / Axios
                               │
                    ┌──────────▼───────────┐
                    │   Spring Boot API    │
                    │      Port: 8080      │
                    └──────────┬───────────┘
                               │
                ┌──────────────┼──────────────┐
                │              │              │
        ┌───────▼──────┐ ┌────▼─────┐ ┌──────▼──────┐
        │ Spring       │ │   JWT    │ │   Services  │
        │ Security     │ │ Security │ │             │
        └──────────────┘ └──────────┘ └──────┬──────┘
                                             │
                                      ┌──────▼──────┐
                                      │ Spring Data │
                                      │     JPA     │
                                      └──────┬──────┘
                                             │
                                      ┌──────▼──────┐
                                      │    MySQL    │
                                      └─────────────┘src/main/java/com/payshield

├── config
│   └── SecurityConfig.java
│
├── controller
│   ├── UserController.java
│   ├── AuthController.java
│   ├── ProtectedController.java
│   ├── WalletController.java
│   ├── TransactionController.java
│   └── AdminController.java
│
├── dto
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   └── TransferResponse.java
│
├── entity
│   ├── User.java
│   ├── Wallet.java
│   └── Transaction.java
│
├── exception
│   └── GlobalExceptionHandler.java
│
├── repository
│   ├── UserRepository.java
│   ├── WalletRepository.java
│   └── TransactionRepository.java
│
├── security
│   ├── JwtUtil.java
│   └── JwtAuthenticationFilter.java
│
└── service
    ├── UserService.java
    ├── CustomUserDetailsService.java
    ├── WalletService.java
    ├── TransactionService.java
    └── AdminService.java## 📁 Frontend Structure

```text
payshield-frontend
│
├── public
│
├── src
│   ├── App.jsx
│   ├── index.css
│   └── main.jsx
│
├── package.json
├── vite.config.js
└── index.htmlUser
 │
 ▼
React Frontend
 │
 │ Axios
 ▼
Spring Boot REST API
 │
 ▼
Spring Security + JWT
 │
 ▼
Service Layer
 │
 ▼
Repository Layer
 │
 ▼
MySQL DatabaseRegister
   ↓
User stored in MySQL
   ↓
Login
   ↓
Spring Security validates credentials
   ↓
JWT Token generated
   ↓
React stores JWT
   ↓
JWT sent with protected requests
   ↓
JwtAuthenticationFilter
   ↓
Protected API access
Create Wallet
      ↓
Initial Balance = ₹0
      ↓
Deposit / Withdraw
      ↓
Wallet Balance Updated
      ↓
Transaction Created
      ↓
Transaction History
Sender Wallet
      │
      │ Transfer Amount
      ▼
Transaction Service
      │
      ├── Debit Sender
      │
      ├── Credit Receiver
      │
      ├── TRANSFER_OUT Transaction
      │
      └── TRANSFER_IN Transaction
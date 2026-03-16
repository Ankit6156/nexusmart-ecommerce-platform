![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/SpringBoot-Backend-green)
![React](https://img.shields.io/badge/React-Frontend-blue)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue)
![JWT](https://img.shields.io/badge/JWT-Security-yellow)
![Razorpay](https://img.shields.io/badge/Razorpay-Payment-purple)

# 🛒 NexusMart – Full Stack E-Commerce Platform

NexusMart is a full stack e-commerce application built using **Java Spring Boot, React.js, and MySQL**.
The platform provides a complete online shopping experience with secure authentication, OTP verification, product management, order processing, and payment integration.

---

# 🚀 Tech Stack

## Backend

* Java
* Spring Boot
* Spring Security
* JWT Authentication
* OTP Verification
* Hibernate / JPA
* RESTful APIs
* MySQL

## Frontend

* React.js
* Vite
* HTML5
* CSS3
* JavaScript

## Payment Integration

* Razorpay REST API

---

# ✨ Key Features

## User Features

* User Registration & Login
* OTP verification for secure login/registration
* JWT-based authentication
* Browse products
* Add products to cart
* Place orders
* Secure payment using Razorpay
* Order history tracking
* Profile management

## Admin Features

* Admin login
* Add new products
* Update product details
* Delete products
* Manage users
* View orders
* Business analytics dashboard

---

# 📊 Business Analytics

Admin dashboard provides revenue insights:

* Daily revenue
* Monthly revenue
* Yearly revenue
* Total sales tracking

---

# 🔐 Security Features

* JWT Token Authentication
* OTP Verification for login/registration
* Role-based access control (Admin / User)
* Secure REST APIs

---

# 🏗 System Architecture

```
User
 │
 ▼
React Frontend
 │
 ▼
Spring Boot REST APIs
 │
 ├── JWT Authentication
 ├── OTP Verification
 ├── Business Logic (Services)
 │
 ▼
MySQL Database
 │
 ▼
Razorpay Payment Gateway
```

---

# ⚙ Backend Architecture

```
Controller → Service → Repository → Database
```

Main components include:

* Controllers (Admin & User APIs)
* Service layer for business logic
* Repository layer using Spring Data JPA
* JWT Authentication filter
* OTP verification system
* Entity models for database mapping

---

# 📁 Project Structure

```
NexusMart-Ecommerce-Platform
│
├── NexusMart-Backend                 # Spring Boot backend application
│   ├── controllers                   # REST API controllers
│   ├── services                      # Business logic layer
│   ├── repositories                  # Database access layer (JPA)
│   ├── entities                      # Database entity models
│   └── security                      # JWT authentication & filters
│
├── NexusMart-Frontend                # React.js frontend application
│   ├── components                    # Reusable UI components
│   ├── pages                         # Application pages
│   ├── assets                        # Images, CSS, styles
│   └── routes                        # Application routing
```

---

# 🔗 API Highlights

## Authentication APIs

* POST `/api/auth/login` – User login
* GET `/api/auth/verify` – Verify JWT token
* POST `/api/auth/forgot-password` – Request password reset
* GET `/api/auth/forgot-password/verify-otp` – Verify OTP for password reset
* POST `/api/auth/forgot-password/reset` – Reset password
* POST `/api/auth/logout` – Logout user

---

## User APIs

* POST `/api/users/register` – Register new user
* GET `/api/users/profile` – Get user profile

---

## Product APIs

* GET `/api/products` – Get all products

---

## Cart APIs

* POST `/api/cart/add` – Add product to cart
* GET `/api/cart/items` – Get cart items
* PUT `/api/cart/update` – Update cart item quantity
* DELETE `/api/cart/delete` – Remove item from cart
* GET `/api/cart/items/count` – Get cart item count

---

## Order APIs

* GET `/api/orders` – Get user orders

---

## Payment APIs

* POST `/api/payment/create` – Create Razorpay order
* POST `/api/payment/verify` – Verify payment

---

## Admin Product APIs

* POST `/admin/products/add` – Add new product
* DELETE `/admin/products/delete` – Delete product

---

## Admin User APIs

* PUT `/admin/user/modify` – Modify user details
* GET `/admin/user/getbyid/{userId}` – Get user by ID

---

## Admin Business Analytics APIs

* GET `/admin/business/daily` – Daily revenue
* GET `/admin/business/monthly` – Monthly revenue
* GET `/admin/business/yearly` – Yearly revenue
* GET `/admin/business/overall` – Overall business revenue

---

# 🔐 Security

The application uses multiple layers of security:

* JWT Token Authentication
* OTP Verification for password reset
* Role-based access control (Admin / User)
* Secure REST APIs
* Authentication filter protecting all `/api/*` and `/admin/*` routes

---

# 🔮 Future Improvements

* Product reviews and ratings
* Email notifications
* Inventory management
* Microservices architecture
* Docker deployment

---

# 👨‍💻 Author

**Ankit Kumar**
Java Full Stack Developer

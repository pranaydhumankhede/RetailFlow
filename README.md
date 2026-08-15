RetailFlow

RetailFlow is a full-stack retail billing and management application built with Spring Boot, React, and MySQL. It provides a complete workflow for managing products and categories, processing customer orders, handling payments, managing users, and viewing sales/order information from an admin dashboard.

Features

Admin authentication with JWT

Role-based access control

Product/item management

Category management with image upload

User management

Order creation and order history

Sales dashboard with recent orders

Cash and UPI payment options

Razorpay payment integration and payment verification

AWS S3 integration for image/file storage

BCrypt password hashing

RESTful backend APIs

Responsive web-based frontend

Tech Stack

Backend

Java 21

Spring Boot 3.4.4

Spring Security

Spring Data JPA / Hibernate

Maven

JWT

BCrypt

Lombok

Frontend

React 19

Vite

JavaScript

Bootstrap 5

Bootstrap Icons

Axios

React Router

React Hot Toast

Database & Services

MySQL 8

AWS S3

Razorpay

Application Modules

Dashboard

Displays today's sales, today's order count, and recent orders with customer, amount, payment method, status, and time.

Explore

Allows users to browse available categories and items, search products, add items to the cart, enter customer details, and place orders.

Manage Items

Admin can add and remove products with their category, price, description, and image.

Manage Categories

Admin can create and delete categories and upload category images.

Manage Users

Admin can register and remove application users.

Order History

Displays previous orders with order ID, customer information, items, total amount, payment method, status, and date.

Project Structure

RetailFlow/
├── billingsoftware/ # Spring Boot backend
│ ├── src/main/java/
│ │ └── in/pranay/billingsoftware/
│ │ ├── config/ # Security, AWS and static resource configuration
│ │ ├── controller/ # REST controllers
│ │ ├── entity/ # JPA entities
│ │ ├── filter/ # JWT request filter
│ │ ├── io/ # Request/response DTOs
│ │ ├── repository/ # JPA repositories
│ │ ├── service/ # Service interfaces
│ │ ├── service/impl/ # Service implementations
│ │ └── util/ # JWT utilities
│ ├── src/main/resources/
│ │ └── application.properties
│ ├── pom.xml
│ └── .gitignore
│
├── client/ # React frontend
│ ├── src/
│ │ ├── components/
│ │ ├── pages/
│ │ ├── Service/
│ │ └── util/
│ ├── package.json
│ └── vite.config.js
│
└── billing_app.sql # MySQL database schema

Backend API

The backend uses the base context path:

/api/v1.0

Main endpoints include:

POST /login
POST /encode

GET /categories
GET /items

GET /orders/latest
POST /orders
DELETE /orders/{id}

POST /payments/create-order
POST /payments/verify

GET /dashboard

POST /admin/register
GET /admin/users
DELETE /admin/users/{id}

POST /admin/categories
DELETE /admin/categories/{categoryId}

POST /admin/items
DELETE /admin/items/{itemId}

Protected APIs use JWT authentication. Administrative APIs require the ADMIN role.

Database

RetailFlow uses MySQL with the billing_app database.

The project includes:

billing_app.sql

which contains the database table definitions for:

Users

Categories

Items

Orders

Order Items

Create the database and import the SQL schema before running the backend.

Local Setup

Prerequisites

JDK 21

Maven

MySQL 8

Node.js and npm

1. Clone the repository

git clone https://github.com/your-username/retailflow.git
cd retailflow

2. Configure MySQL

Create the database:

CREATE DATABASE billing_app;

Import:

billing_app.sql

3. Configure Backend

Set the following values using your local/hosting environment configuration:

Database URL
Database username
Database password
JWT secret
AWS access key
AWS secret key
AWS region
AWS bucket name
Razorpay key ID
Razorpay key secret

Do not commit real credentials or secret keys to GitHub.

4. Start Backend

From the billingsoftware directory:

./mvnw spring-boot:run

On Windows:

mvnw.cmd spring-boot:run

The local backend runs on:

http://localhost:8080

with the API context:

http://localhost:8080/api/v1.0

5. Start Frontend

From the client directory:

npm install
npm run dev

The Vite development server normally runs on:

http://localhost:5173

Payment Integration

RetailFlow supports:

Cash payments

UPI payments through Razorpay

The frontend loads the Razorpay Checkout script, while the backend creates Razorpay orders and verifies the payment response.

For development, use Razorpay Test Mode credentials.

Keep the Razorpay secret key only on the backend. The frontend should use only the publishable Razorpay Key ID.

Security

RetailFlow implements:

JWT-based stateless authentication

BCrypt password hashing

Role-based authorization

Protected admin endpoints

CORS configuration

Server-side Razorpay payment verification

Before publishing the repository, make sure the following are not committed:

Database passwords
JWT secret keys
AWS secret keys
Razorpay secret keys
.env files
IDE/build files

Use environment variables on the deployment platform for production credentials.

Deployment

For production deployment, update the environment-specific configuration for:

MySQL database
Backend URL
Frontend URL
JWT secret
AWS S3 credentials
Razorpay credentials
CORS allowed origin
File upload URL

The frontend API URLs must point to the deployed Spring Boot backend instead of localhost.

The backend should also use the hosting platform's assigned port when required.

Future Improvements

Pagination and advanced search

Product stock/inventory tracking

PDF invoice generation

Sales analytics and charts

Email/SMS order notifications

Cloud-hosted database

Improved production logging and monitoring

License

This project is developed for educational, portfolio, and demonstration purposes.

Author

Pranay Dhumankhede

# Quick Start Guide - Run Application Locally

## Prerequisites
- Java 21+ installed
- MySQL 8.0+ running
- Maven installed

---

## Step 1: Ensure MySQL is Running

Verify MySQL is running and accessible:

```bash
mysql -u root -p

# If you're prompted for password, use: pranay123
# Then exit with: exit
```

---

## Step 2: Build the Application

```bash
# Navigate to project directory
cd billingsoftware

# Clean build (skip tests)
mvn clean package -DskipTests
```

---

## Step 3: Run the Application

### Option 1: Run with Default Dev Configuration
```bash
java -jar target/billingsoftware-0.0.1-SNAPSHOT.jar
```

This will use default settings from `application.properties`:
- **Database:** `jdbc:mysql://localhost:3306/billing_app`
- **Username:** `root`
- **Password:** `pranay123`
- **Frontend URL:** `http://localhost:5173`

### Option 2: Run with Dev Profile
```bash
java -Dspring.profiles.active=dev -jar target/billingsoftware-0.0.1-SNAPSHOT.jar
```

This loads `application-dev.properties` with enhanced debugging.

### Option 3: Run with Custom Environment Variables

```bash
# Set environment variables
export DB_URL="jdbc:mysql://localhost:3306/billing_app"
export DB_USERNAME="root"
export DB_PASSWORD="pranay123"
export JWT_SECRET_KEY="dGhpc2lzYTE2Ymluamlzc2VjcmV0a2V5ZmRldmVsbDBwbWVudA=="
export APP_FRONTEND_URL="http://localhost:5173"

# Run application
java -jar target/billingsoftware-0.0.1-SNAPSHOT.jar
```

### Option 4: Run with Maven (Dev Mode with Hot Reload)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

---

## Step 4: Verify Application Started Successfully

Look for this message in logs:
```
2026-08-15T16:03:38.XXX+05:30  INFO ... : Tomcat initialized with port 8080 (http)
2026-08-15T16:03:38.XXX+05:30  INFO ... : Tomcat started on port(s): 8080
```

---

## Step 5: Test the API

### Health Check
```bash
curl http://localhost:8080/api/v1.0/actuator/health
```

### Login Test
```bash
curl -X POST http://localhost:8080/api/v1.0/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@gmail.com","password":"123456"}'
```

---

## Database Configuration

### If MySQL Database Doesn't Exist
The application will create it automatically (if `spring.jpa.hibernate.ddl-auto=update`).

### To Manually Create Database
```bash
mysql -u root -p pranay123

CREATE DATABASE billing_app;
USE billing_app;
```

---

## Environment Variables Summary

| Variable | Default | Required | Example |
|----------|---------|----------|---------|
| `DB_URL` | `jdbc:mysql://localhost:3306/billing_app` | No | `jdbc:mysql://db-server:3306/billing_app` |
| `DB_USERNAME` | `root` | No | `billing_user` |
| `DB_PASSWORD` | `pranay123` | No | `strong_password` |
| `JWT_SECRET_KEY` | base64 dev key | No | Generated with `openssl rand -base64 32` |
| `APP_FRONTEND_URL` | `http://localhost:5173` | No | `https://billing.example.com` |
| `AWS_ACCESS_KEY_ID` | (empty) | No | Your AWS key |
| `AWS_SECRET_ACCESS_KEY` | (empty) | No | Your AWS secret |
| `RAZORPAY_KEY_ID` | (empty) | No | Your Razorpay key |
| `RAZORPAY_KEY_SECRET` | (empty) | No | Your Razorpay secret |

---

## Troubleshooting

### "Access denied for user 'root'@'localhost' (using password: NO)"
**Cause:** DB_PASSWORD not set or empty
**Fix:** Ensure MySQL password is configured or use default (`pranay123`)

```bash
export DB_PASSWORD="pranay123"
java -jar target/billingsoftware-0.0.1-SNAPSHOT.jar
```

### "Connection refused" for MySQL
**Cause:** MySQL server not running
**Fix:** Start MySQL service

**Windows:**
```bash
net start MySQL80
# or
mysqld --console
```

**Linux/Mac:**
```bash
brew services start mysql
# or
sudo systemctl start mysql
```

### Port 8080 Already in Use
**Fix:** Change port via environment variable

```bash
java -Dserver.port=8081 -jar target/billingsoftware-0.0.1-SNAPSHOT.jar
```

### JWT Secret Too Short
**Error:** `JWT secret key must be at least 256 bits`
**Fix:** Generate valid secret and set environment variable

```bash
# Generate strong secret
openssl rand -base64 32

# Set it (output from above command)
export JWT_SECRET_KEY="<generated_key>"
java -jar target/billingsoftware-0.0.1-SNAPSHOT.jar
```

---

## Next Steps

1. **Run Frontend:** Start React frontend on port 5173
2. **API Documentation:** Visit `http://localhost:8080/api/v1.0/swagger-ui.html` (if Swagger enabled)
3. **Database:** Access MySQL at `localhost:3306` with user `root` / password `pranay123`
4. **Environment:** For production deployment, set environment variables before running JAR

---

**Application Ready:** http://localhost:8080/api/v1.0

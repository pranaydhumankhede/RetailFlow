# Billing Software Deployment Guide

**Updated:** 2026-08-15  
**Deployment Type:** Traditional Server (Non-Docker)

---

## Prerequisites

- Java 21 (JDK installed)
- Maven 3.8+
- MySQL 8.0+
- Environment variables configured

---

## Deployment Steps

### 1. Prepare Environment Variables

Create a `.env` file or export environment variables:

```bash
# Database Configuration
export DB_URL="jdbc:mysql://db-server:3306/billing_app"
export DB_USERNAME="billing_user"
export DB_PASSWORD="your_strong_password"
export JPA_DDL_AUTO="validate"

# AWS Configuration
export AWS_ACCESS_KEY_ID="your_aws_key"
export AWS_SECRET_ACCESS_KEY="your_aws_secret"
export AWS_REGION="ap-south-1"
export AWS_BUCKET_NAME="your-bucket-name"

# JWT Configuration
export JWT_SECRET_KEY="your_base64_secret_key"
export JWT_EXPIRATION_HOURS="10"

# Razorpay Configuration
export RAZORPAY_KEY_ID="your_razorpay_key"
export RAZORPAY_KEY_SECRET="your_razorpay_secret"

# Application URLs
export APP_UPLOAD_BASE_URL="https://cdn.example.com"
export APP_FRONTEND_URL="https://billing.example.com"

# Spring Profile
export SPRING_PROFILES_ACTIVE="prod"
```

### 2. Build the Application

```bash
# Build JAR file
mvn clean package -DskipTests

# Output: target/billingsoftware-0.0.1-SNAPSHOT.jar
```

### 3. Deploy to Server

#### Option A: Traditional Server (Tomcat, etc.)

```bash
# WAR file deployment
mvn clean package -DskipTests -P war

# Copy to Tomcat:
cp target/billingsoftware-0.0.1-SNAPSHOT.war /path/to/tomcat/webapps/
```

#### Option B: Standalone JAR

```bash
# Run with environment variables
export $(cat .env | xargs)
java -jar target/billingsoftware-0.0.1-SNAPSHOT.jar

# Or with inline environment variables
java \
  -Dspring.datasource.url=$DB_URL \
  -Dspring.datasource.username=$DB_USERNAME \
  -Dspring.datasource.password=$DB_PASSWORD \
  -Daws.access.key=$AWS_ACCESS_KEY_ID \
  -Daws.secret.key=$AWS_SECRET_ACCESS_KEY \
  -Djwt.secret.key=$JWT_SECRET_KEY \
  -Drazorpay.key.id=$RAZORPAY_KEY_ID \
  -Drazorpay.key.secret=$RAZORPAY_KEY_SECRET \
  -Dapp.frontend.url=$APP_FRONTEND_URL \
  -jar target/billingsoftware-0.0.1-SNAPSHOT.jar
```

### 4. Deploy to AWS EC2

```bash
# On EC2 instance:
1. Install Java 21
   sudo yum install java-21-openjdk java-21-openjdk-devel

2. Create application directory
   mkdir -p /opt/billing-app
   cd /opt/billing-app

3. Upload JAR file
   scp -i your-key.pem target/billingsoftware-0.0.1-SNAPSHOT.jar ec2-user@your-ec2-ip:/opt/billing-app/

4. Create systemd service file (/etc/systemd/system/billing-app.service)
   [Unit]
   Description=Billing Software Application
   After=network.target

   [Service]
   Type=simple
   User=ec2-user
   WorkingDirectory=/opt/billing-app
   EnvironmentFile=/opt/billing-app/.env
   ExecStart=/usr/bin/java -jar billingsoftware-0.0.1-SNAPSHOT.jar
   Restart=always

   [Install]
   WantedBy=multi-user.target

5. Enable and start service
   sudo systemctl daemon-reload
   sudo systemctl enable billing-app
   sudo systemctl start billing-app

6. View logs
   sudo journalctl -u billing-app -f
```

### 5. Deploy to AWS Elastic Beanstalk

```bash
# 1. Install EB CLI
pip install awsebcli

# 2. Initialize Elastic Beanstalk
eb init -p "Java 21 running on 64bit Amazon Linux 2" billing-app

# 3. Create .ebextensions/java.config
option_settings:
  java:
    environment.variables:
      DB_URL: "jdbc:mysql://rds-endpoint:3306/billing_app"
      DB_USERNAME: "admin"
      DB_PASSWORD: "password"

# 4. Create environment and deploy
eb create billing-env
eb deploy
```

### 6. Deploy to AWS Lambda (Serverless)

For serverless deployment, use Spring Cloud Function:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-function-adapter-aws</artifactId>
</dependency>
```

### 7. Deploy to Cloud Platforms

#### Heroku
```bash
# 1. Create Procfile
echo "web: java -jar target/billingsoftware-0.0.1-SNAPSHOT.jar" > Procfile

# 2. Deploy
git push heroku main
```

#### Azure App Service
```bash
az webapp up --name billing-software --resource-group myResourceGroup --runtime "JAVA|21"
```

#### Google Cloud Run
```bash
gcloud run deploy billing-software \
  --source . \
  --platform managed \
  --region us-central1
```

---

## Environment Variables Reference

| Variable | Required | Description |
|----------|----------|-------------|
| `DB_URL` | ✅ | Database connection URL |
| `DB_USERNAME` | ✅ | Database username |
| `DB_PASSWORD` | ✅ | Database password (strong) |
| `AWS_ACCESS_KEY_ID` | ✅ | AWS access key |
| `AWS_SECRET_ACCESS_KEY` | ✅ | AWS secret key |
| `AWS_REGION` | ✅ | AWS region (ap-south-1) |
| `AWS_BUCKET_NAME` | ✅ | S3 bucket name |
| `JWT_SECRET_KEY` | ✅ | JWT signing secret (256+ bits) |
| `RAZORPAY_KEY_ID` | ✅ | Razorpay key ID |
| `RAZORPAY_KEY_SECRET` | ✅ | Razorpay secret |
| `APP_FRONTEND_URL` | ✅ | Frontend URL for CORS |
| `APP_UPLOAD_BASE_URL` | ✅ | Upload base URL (S3 CDN) |
| `JPA_DDL_AUTO` | ⚠️ | `update` (dev) or `validate` (prod) |
| `SPRING_PROFILES_ACTIVE` | ⚠️ | `dev`, `prod`, or `test` |

---

## Verification

After deployment, verify the application is running:

```bash
# Health check
curl http://localhost:8080/api/v1.0/actuator/health

# Logs
tail -f /var/log/billing-software/app.log

# Database connection
curl -X POST http://localhost:8080/api/v1.0/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@gmail.com","password":"123456"}'
```

---

## Production Checklist

Before deploying to production:

- [ ] Environment variables set on server
- [ ] Database migrated and backed up
- [ ] JWT secret key is 256+ bits
- [ ] AWS credentials configured with minimal permissions (IAM role preferred)
- [ ] CORS origins updated for production frontend
- [ ] SSL/TLS certificate installed
- [ ] Logs configured and monitored
- [ ] Database connection pooling optimized
- [ ] Health monitoring configured
- [ ] Backup strategy in place
- [ ] Firewall rules configured
- [ ] Rate limiting enabled

---

## Troubleshooting

### Application won't start
```bash
# Check Java version
java -version

# Check environment variables
env | grep -E "DB_|JWT_|AWS_|RAZORPAY"

# View detailed logs
java -jar app.jar --debug
```

### Database connection failed
```bash
# Test MySQL connection
mysql -h $DB_URL -u $DB_USERNAME -p$DB_PASSWORD -e "SHOW DATABASES;"

# Check connection string format
# Should be: jdbc:mysql://host:3306/database
```

### Port already in use
```bash
# Change port in application.properties or environment
java -Dserver.port=8081 -jar app.jar
```

### Out of memory
```bash
# Increase heap size
java -Xmx2g -Xms512m -jar app.jar
```

---

## Performance Tuning

### Database Connection Pool
Add to `application-prod.properties`:
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=600000
```

### JVM Tuning
```bash
java -Xmx2g -Xms512m \
  -XX:+UseG1GC \
  -XX:+ParallelRefProcEnabled \
  -XX:MaxGCPauseMillis=200 \
  -jar app.jar
```

### Caching
Enable Redis caching for better performance:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

---

## Monitoring & Logging

### Spring Boot Actuator Endpoints
- `/actuator/health` - Health status
- `/actuator/metrics` - Application metrics
- `/actuator/loggers` - Logger configuration

### Recommended Monitoring Tools
- AWS CloudWatch
- New Relic
- DataDog
- Grafana + Prometheus

---

## Security Best Practices

1. **Secrets Management**
   - Use AWS Secrets Manager or environment variables
   - Never commit `.env` or `application.properties`
   - Rotate credentials regularly

2. **Database Security**
   - Use strong passwords (20+ chars)
   - Restrict database access to application server only
   - Enable encryption at rest and in transit

3. **SSL/TLS**
   - Use HTTPS for all endpoints
   - Update certificates before expiration
   - Use strong cipher suites

4. **Application Security**
   - Keep dependencies updated (run `mvn dependency:check`)
   - Enable security headers
   - Rate limiting on API endpoints

---

**Need Help?** Check the security review in `SECURITY_REVIEW.md`


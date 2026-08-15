package in.bushansirgur.billingsoftware.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Load environment variables from .env file for local development.
 * This ensures Spring Boot can access credentials and configuration
 * from the .env file without requiring system-level environment variables.
 */
@Configuration
@Slf4j
public class EnvConfig {

    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
            
            log.info("✓ .env file loaded successfully");
            
            // Set individual known environment variables
            String[] envVars = {
                    "DB_URL", "DB_USERNAME", "DB_PASSWORD", "DB_HOST", "DB_PORT", "DB_NAME",
                    "JPA_DDL_AUTO", "AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY", "AWS_REGION",
                    "AWS_BUCKET_NAME", "JWT_SECRET_KEY", "JWT_EXPIRATION_HOURS",
                    "RAZORPAY_KEY_ID", "RAZORPAY_KEY_SECRET", "APP_UPLOAD_BASE_URL",
                    "APP_FRONTEND_URL", "FRONTEND_URL", "SERVER_PORT", "SERVER_SERVLET_CONTEXT_PATH",
                    "SPRING_PROFILES_ACTIVE"
            };
            
            for (String varName : envVars) {
                String value = dotenv.get(varName);
                if (value != null && !value.isEmpty()) {
                    System.setProperty(varName, value);
                    if (varName.contains("SECRET") || varName.contains("PASSWORD") || varName.contains("KEY")) {
                        log.debug("Loaded env variable: {}", varName);
                    } else {
                        log.debug("Loaded env variable: {} = {}", varName, value);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("⚠️  Could not load .env file. Using system environment variables instead.");
            log.debug("Error details: {}", e.getMessage());
        }
    }
}

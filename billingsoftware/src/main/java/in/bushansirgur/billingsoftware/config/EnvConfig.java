package in.bushansirgur.billingsoftware.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.nio.file.Paths;

/**
 * Load environment variables from .env file for local development.
 * This initializer runs BEFORE Spring Boot starts, ensuring all
 * environment variables are available for property resolution.
 */
public class EnvConfig {

    static {
        try {
            // Try to load .env from multiple locations
            String envPath = System.getProperty("user.dir");
            
            System.out.println("🔍 Current working directory: " + envPath);
            System.out.println("🔍 Looking for .env file...");
            
            Dotenv dotenv = Dotenv.configure()
                    .directory(envPath)
                    .ignoreIfMissing()
                    .load();
            
            System.out.println("✓ .env file loaded successfully");
            
            // Set individual known environment variables
            String[] envVars = {
                    "DB_URL", "DB_USERNAME", "DB_PASSWORD", "DB_HOST", "DB_PORT", "DB_NAME",
                    "JPA_DDL_AUTO", "AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY", "AWS_REGION",
                    "AWS_BUCKET_NAME", "JWT_SECRET_KEY", "JWT_EXPIRATION_HOURS",
                    "RAZORPAY_KEY_ID", "RAZORPAY_KEY_SECRET", "APP_UPLOAD_BASE_URL",
                    "APP_FRONTEND_URL", "FRONTEND_URL", "SERVER_PORT", "SERVER_SERVLET_CONTEXT_PATH",
                    "SPRING_PROFILES_ACTIVE"
            };
            
            int loaded = 0;
            for (String varName : envVars) {
                String value = dotenv.get(varName);
                if (value != null && !value.isEmpty()) {
                    System.setProperty(varName, value);
                    loaded++;
                    if (varName.contains("SECRET") || varName.contains("PASSWORD") || varName.contains("KEY")) {
                        System.out.println("  ✓ " + varName);
                    } else {
                        System.out.println("  ✓ " + varName + " = " + value);
                    }
                }
            }
            
            System.out.println("\n✓ Loaded " + loaded + " environment variables");
            
            // Debug: Verify DB_URL was set
            String dbUrl = System.getProperty("DB_URL");
            System.out.println("🔍 DB_URL in system properties = " + dbUrl);
            
        } catch (Exception e) {
            System.err.println("⚠️  Could not load .env file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

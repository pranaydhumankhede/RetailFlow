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
            // Try multiple locations for .env file
            // 1. Current working directory (works for JAR)
            // 2. Project root (works for IntelliJ)
            String[] searchPaths = {
                System.getProperty("user.dir"),
                System.getProperty("user.dir") + "/billingsoftware"
            };
            
            Dotenv dotenv = null;
            String loadedFrom = null;
            
            for (String path : searchPaths) {
                System.out.println("🔍 Looking for .env in: " + path);
                try {
                    dotenv = Dotenv.configure()
                            .directory(path)
                            .ignoreIfMissing()
                            .load();
                    
                    // Test if any variables were actually loaded
                    if (dotenv.get("RAZORPAY_KEY_ID") != null || dotenv.get("DB_URL") != null) {
                        loadedFrom = path;
                        break;
                    }
                } catch (Exception e) {
                    // Try next path
                }
            }
            
            if (dotenv == null || loadedFrom == null) {
                System.out.println("⚠️  No .env file found in any location");
                System.out.println("⚠️  Using system environment variables");
                // Variables should already be set via IntelliJ or system env
            } else {
                    System.out.println("✓ .env file loaded from: " + loadedFrom);
                
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
                
                System.out.println("\n✓ Loaded " + loaded + " environment variables\n");
            }
            
        } catch (Exception e) {
            System.err.println("⚠️  Could not load .env file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

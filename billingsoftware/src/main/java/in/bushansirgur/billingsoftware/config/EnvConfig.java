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
            
            // Map dotenv variables to system properties for Spring to access
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                System.setProperty(key, value);
                if (key.contains("SECRET") || key.contains("PASSWORD") || key.contains("KEY")) {
                    log.debug("Loaded env variable: {}", key);
                } else {
                    log.debug("Loaded env variable: {} = {}", key, value);
                }
            });
        } catch (Exception e) {
            log.warn("⚠️  Could not load .env file. Using system environment variables instead.");
            log.debug("Error details: {}", e.getMessage());
        }
    }
}

package in.pranay.billingsoftware.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS Configuration
 * AWS S3 client will only be created if AWS_ACCESS_KEY_ID and
 * AWS_SECRET_ACCESS_KEY are set.
 * For local development without AWS, these can be left empty and S3 upload
 * features will be skipped.
 */
@Configuration
public class AWSConfig {

    @Value("${aws.access.key:}")
    private String accessKey;

    @Value("${aws.secret.key:}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    /**
     * Creates S3Client bean only if AWS credentials are provided.
     * This allows local development to run without AWS configuration.
     * 
     * @return S3Client if credentials are available, null otherwise
     */
    @Bean
    @ConditionalOnProperty(name = "aws.access.key", matchIfMissing = false)
    public S3Client s3Client() {
        if (isAwsConfigured()) {
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)))
                    .build();
        }
        return null;
    }

    /**
     * Check if AWS is properly configured
     */
    public boolean isAwsConfigured() {
        return accessKey != null && !accessKey.isBlank() &&
                secretKey != null && !secretKey.isBlank();
    }
}

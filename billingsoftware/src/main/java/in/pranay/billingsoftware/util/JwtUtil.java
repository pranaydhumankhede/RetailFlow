package in.pranay.billingsoftware.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Value("${jwt.expiration.hours:10}")
    private int expirationHours;

    /**
     * Validate JWT secret key on application startup
     * Minimum 256 bits (32 bytes) when base64 decoded for HS256 algorithm
     */
    @PostConstruct
    public void validateSecretKey() {
        if (SECRET_KEY == null || SECRET_KEY.isBlank()) {
            throw new IllegalArgumentException(
                    "JWT_SECRET_KEY environment variable is not set. " +
                            "Generate one with: openssl rand -base64 32");
        }

        try {
            byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY);
            if (decodedKey.length < 32) {
                throw new IllegalArgumentException(
                        String.format("JWT secret key must be at least 256 bits (32 bytes). Current: %d bytes. " +
                                "Generate with: openssl rand -base64 32",
                                decodedKey.length));
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("bit")) {
                throw e;
            }
            throw new IllegalArgumentException(
                    "JWT_SECRET_KEY must be a valid base64-encoded string. " +
                            "Generate with: openssl rand -base64 32",
                    e);
        }
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + 1000L * 60 * 60 * expirationHours);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

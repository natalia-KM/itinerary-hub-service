package com.ih.itinerary_hub_service.integration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableConfigurationProperties
@ActiveProfiles("test")
@Transactional
@Sql("/users-data.sql")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseIntegrationTest {

    private static final String SECRET_KEY = "this-is-a-very-long-key-only-for-testing-spring-pls-stop-complaining";
    protected static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    protected final UUID GUEST_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    protected final UUID GOOGLE_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    protected final UUID NON_EXISTING_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    private final String guestUserAccessToken = generateValidAccessToken("guest-user-0123", GUEST_USER_ID);
    private final String googleUserAccessToken = generateValidAccessToken("google-user-0123", GOOGLE_USER_ID);
    private final String nonExistingUserAccessToken = generateValidAccessToken("ne-user-0123", NON_EXISTING_USER_ID);

    protected final Cookie guestUserAccessTokenCookie = new Cookie("access_token", guestUserAccessToken);
    protected final Cookie googleUserAccessTokenCookie = new Cookie("access_token", googleUserAccessToken);
    protected final Cookie nonExistingUserAccessTokenCookie = new Cookie("access_token", nonExistingUserAccessToken);

    protected final Cookie guestUserIdCookie = new Cookie("user_id", GUEST_USER_ID.toString());
    protected final Cookie googleUserIdCookie = new Cookie("user_id", GOOGLE_USER_ID.toString());
    protected final Cookie nonExistingUserIdCookie = new Cookie("user_id", NON_EXISTING_USER_ID.toString());

    private final String invalidSecretKey = UUID.randomUUID().toString();
    private final SecretKey invalidKey = Keys.hmacShaKeyFor(invalidSecretKey.getBytes(StandardCharsets.UTF_8));
    private final String invalidAccessToken = generateAnyToken("anySubject", GUEST_USER_ID, getAMonthFromNow(), invalidKey);
    protected final Cookie invalidAccessTokenCookie = new Cookie("access_token", invalidAccessToken);

    private final String expiredAccessToken = generateAnyToken("anySubject", GUEST_USER_ID, new Date(), key);
    protected final Cookie expiredAccessTokenCookie = new Cookie("access_token", expiredAccessToken);

    @BeforeAll
    void setup() {
    }

    private Date getAMonthFromNow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }

    private String generateValidAccessToken(String subject, UUID userId) {
        return generateAnyToken(subject, userId, getAMonthFromNow(), key);
    }

    private String generateAnyToken(String subject, UUID userId, Date dateTime, SecretKey secretKey) {
        return Jwts.builder()
                .subject(subject)
                .claim("userId", userId.toString())
                .issuer("itinerary-hub-service")
                .issuedAt(new Date())
                .expiration(dateTime)
                .signWith(secretKey)
                .compact();
    }
}

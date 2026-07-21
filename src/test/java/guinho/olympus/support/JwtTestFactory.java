package guinho.olympus.support;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class JwtTestFactory {
    private static final SecretKey SIGNING_KEY;

    static  {
        String secret = System.getenv("JWT_SECRET");

        if(secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET not defined");
        }

        SIGNING_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateToken(UUID playerId) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + 3600000);

        return Jwts.builder().subject(playerId.toString()).signWith(SIGNING_KEY, Jwts.SIG.HS256).issuedAt(now).expiration(exp).compact();
    }
}

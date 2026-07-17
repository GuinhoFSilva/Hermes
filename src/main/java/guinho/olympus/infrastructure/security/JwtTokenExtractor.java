package guinho.olympus.infrastructure.security;

import guinho.olympus.core.application.abstractions.TokenExtractor;
import guinho.olympus.core.domain.match.valueobject.PlayerId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class JwtTokenExtractor implements TokenExtractor {
    private final SecretKey signingKey;

    public JwtTokenExtractor() {
        String secret = System.getenv("JWT_SECRET");

        if(secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET not defined");
        }

        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public PlayerId extractPlayerId(String token) {
        Claims claims = getPayload(token);
        return PlayerId.of(UUID.fromString(claims.getSubject()));
    }

    private Claims getPayload(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token).getPayload();
    }
}

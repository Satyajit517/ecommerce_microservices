package com.project.ProductService.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService{

    private final SecretKey secretKey;

    public JwtService( @Value("${security.jwt.secret}") String secret )
    {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public Claims extractAllClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserEmail(String token)
    {
        return extractAllClaims(token).getSubject();
    }

    public UUID extractUserId(String token)
    {
        String uuid =  extractAllClaims(token).get("userId", String.class);
        return UUID.fromString(uuid);
    }

    public boolean isAccessToken(String token)
    {
        String type = extractAllClaims(token).get("type", String.class);

        return "access".equals(type);
    }

    public boolean isTokenExpired(String token)
    {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}

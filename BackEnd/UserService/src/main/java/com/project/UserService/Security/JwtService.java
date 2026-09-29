package com.project.UserService.Security;

import com.project.UserService.Entity.User;
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

    private final long accessTokenExpiration;

    private final long refreshTokenExpiration;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${security.jwt.refresh-token-expiration}") long refreshTokenExpiration
    )
    {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        this.accessTokenExpiration = accessTokenExpiration;

        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(User user)
    {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId().toString())
                .claim("role", user.getRole().name())
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(User user, String jti)
    {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId().toString())
                .claim("type", "refresh")
                .id(jti)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(secretKey)
                .compact();
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

    public String getJti(String token)
    {
        return extractAllClaims(token).getId();
    }

    public boolean isRefreshToken(String token)
    {
        String type = extractAllClaims(token).get("type", String.class);

        return "refresh".equals(type);
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

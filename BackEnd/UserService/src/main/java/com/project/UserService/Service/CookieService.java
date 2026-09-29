package com.project.UserService.Service;

import com.project.UserService.Exception.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    @Value("${security.jwt.refresh-token-cookie-name}")
    private String refreshTokenCookieName;

    @Value("${security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Value("${security.jwt.cookie-secure}")
    private boolean secure;

    @Value("${security.jwt.cookie-http-only}")
    private boolean httpOnly;

    @Value("${security.jwt.cookie-same-site}")
    private String sameSite;

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken)
    {
        ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieName, refreshToken)
                .httpOnly(httpOnly)
                .secure(secure)
                .sameSite(sameSite)
                .path("/api/auth")
                .maxAge(refreshTokenExpiration/100)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
    }

    public void clearRefreshTokenCookie(HttpServletResponse response)
    {
        ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieName, "")
                .httpOnly(httpOnly)
                .secure(secure)
                .sameSite(sameSite)
                .path("/api/auth")
                .maxAge(0)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
    }

    public String getRefreshTokenCookie(HttpServletRequest request)
    {
        if(request.getCookies() == null)
        {
            throw new UnauthorizedException("Refresh token cookie is missing");
        }

        for(Cookie cookie : request.getCookies())
        {
            if(refreshTokenCookieName.equals(cookie.getName()))
            {
                String value = cookie.getValue();

                if(value == null || value.isBlank()){
                    throw new UnauthorizedException("Refresh token is missing");
                }
                return value;
            }
        }

        throw new UnauthorizedException("Refresh token is missing");
    }
}

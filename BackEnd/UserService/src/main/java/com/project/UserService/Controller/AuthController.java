package com.project.UserService.Controller;


import com.project.UserService.DTO.ApiResponse;
import com.project.UserService.DTO.Auth.AuthResponse;
import com.project.UserService.DTO.Auth.AuthTokenResult;
import com.project.UserService.DTO.User.SellerRegisterRequest;
import com.project.UserService.DTO.User.UserLoginRequest;
import com.project.UserService.DTO.User.UserRegisterRequest;
import com.project.UserService.DTO.User.UserResponse;
import com.project.UserService.Service.AuthService;
import com.project.UserService.Service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegisterRequest request)
    {
        UserResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "User registered successfully",
                                response,
                                Instant.now()
                        )
                );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody UserLoginRequest request,
                                                           HttpServletResponse response)
    {
        AuthTokenResult result = authService.login(request);

        cookieService.addRefreshTokenCookie(response, result.getRefreshToken());

        AuthResponse authResponse = new AuthResponse(result.getAccessToken(), result.getUser());

        return ResponseEntity
                .ok(
                        new ApiResponse<>(
                                true,
                                "Login successful",
                                authResponse,
                                Instant.now()
                        )
                );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(HttpServletRequest request,
                                                                  HttpServletResponse response)
    {
       String refreshToken = cookieService.getRefreshTokenCookie(request);

       AuthTokenResult result = authService.refreshToken(refreshToken);

       cookieService.addRefreshTokenCookie(response, result.getRefreshToken());

       AuthResponse authResponse = new AuthResponse(result.getAccessToken(), result.getUser());

        return ResponseEntity
                .ok(
                        new ApiResponse<>(
                                true,
                                "Access token refreshed successfully",
                                authResponse,
                                Instant.now()
                        )
                );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request,
                                                    HttpServletResponse response)
    {

        String refreshToken = cookieService.getRefreshTokenCookie(request);

        authService.logout(refreshToken);

        cookieService.clearRefreshTokenCookie(response);

        return ResponseEntity
                .ok(
                        new ApiResponse<>(
                                true,
                                "Logout successful",
                                null,
                                Instant.now()
                        )
                );
    }

    @PostMapping("/register/seller")
    public ResponseEntity<ApiResponse<UserResponse>> registerSeller(@Valid @RequestBody SellerRegisterRequest request)
    {
        UserResponse response = authService.registerSeller(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "User registered successfully",
                                response,
                                Instant.now()
                        )
                );
    }
}

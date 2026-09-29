package com.project.UserService.Service.Impl;

import com.project.UserService.DTO.Auth.AuthTokenResult;
import com.project.UserService.DTO.User.SellerRegisterRequest;
import com.project.UserService.DTO.User.UserLoginRequest;
import com.project.UserService.DTO.User.UserRegisterRequest;
import com.project.UserService.DTO.User.UserResponse;
import com.project.UserService.Entity.RefreshToken;
import com.project.UserService.Entity.User;
import com.project.UserService.Exception.UnauthorizedException;
import com.project.UserService.Mapper.UserMapper;
import com.project.UserService.Repository.RefreshTokenRepository;
import com.project.UserService.Repository.UserRepository;
import com.project.UserService.Security.JwtService;
import com.project.UserService.Service.AuthService;
import com.project.UserService.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Value("${security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public UserResponse register(UserRegisterRequest request) {

        return userService.register(request);
    }

    @Override
    @Transactional
    public UserResponse registerSeller(SellerRegisterRequest request) {

        return userService.registerSeller(request);
    }

    @Override
    @Transactional
    public AuthTokenResult login(UserLoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid email or password"));

        String jti = UUID.randomUUID().toString();

        Instant now = Instant.now();

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setJti(jti);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setCreatedAt(now);
        refreshTokenEntity.setExpiresAt(now.plusMillis(refreshTokenExpiration));
        refreshTokenEntity.setRevoked(false);

        refreshTokenRepository.save(refreshTokenEntity);

        String accessToken = jwtService.generateAccessToken(user);

        String refreshToken = jwtService.generateRefreshToken(user, jti);

        UserResponse userResponse = userMapper.toResponse(user);

        return new AuthTokenResult(
                accessToken,
                refreshToken,
                userResponse
        );
    }

    @Override
    @Transactional
    public AuthTokenResult refreshToken(String token) {

        if(!jwtService.isRefreshToken(token))
        {
            throw new UnauthorizedException("Invalid refresh token");
        }

        if(jwtService.isTokenExpired(token))
        {
            throw new UnauthorizedException("Refresh token has expired");
        }

        String jti = jwtService.getJti(token);

        RefreshToken storedToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(()->
                        new UnauthorizedException("Refresh token not found"));

        if(storedToken.isRevoked())
        {
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        UUID userId = jwtService.extractUserId(token);

        if(!storedToken.getUser().getId().equals(userId))
        {
            throw new UnauthorizedException("Refresh token does not belong to the user");
        }

        if(storedToken.getExpiresAt().isBefore(Instant.now()))
        {
            throw new UnauthorizedException("Refresh token has expired");
        }

        User user = storedToken.getUser();

        storedToken.setRevoked(true);

        String newJti = UUID.randomUUID().toString();

        Instant now = Instant.now();

        RefreshToken newRefreshTokenEntity = new RefreshToken();

        newRefreshTokenEntity.setJti(newJti);
        newRefreshTokenEntity.setUser(user);
        newRefreshTokenEntity.setCreatedAt(now);
        newRefreshTokenEntity.setExpiresAt(now.plusMillis(refreshTokenExpiration));
        newRefreshTokenEntity.setRevoked(false);

        refreshTokenRepository.save(newRefreshTokenEntity);

        String newAccessToken = jwtService.generateAccessToken(user);

        String newRefreshToken = jwtService.generateRefreshToken(user, jti);

        UserResponse userResponse = userMapper.toResponse(user);

        return new AuthTokenResult(
                newAccessToken,
                newRefreshToken,
                userResponse
        );
    }

    @Override
    @Transactional
    public void logout(String token) {

        if(!jwtService.isRefreshToken(token))
        {
            throw new UnauthorizedException("Invalid refresh token");
        }

        String jti = jwtService.getJti(token);
        RefreshToken refreshToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(()->
                        new UnauthorizedException("Refresh token not found"));

        refreshToken.setRevoked(true);
    }

}

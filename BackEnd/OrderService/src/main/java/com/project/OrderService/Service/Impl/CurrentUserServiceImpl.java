package com.project.OrderService.Service.Impl;

import com.project.OrderService.Exception.UnauthorizedException;
import com.project.OrderService.Service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {

    @Override
    public UUID getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {

            throw new UnauthorizedException(
                    "User is not authenticated"
            );
        }

        return (UUID) authentication.getPrincipal();
    }
}
package com.project.OrderService.Config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignAuthInterceptor implements RequestInterceptor {

    private final HttpServletRequest request;

    @Override
    public void apply(RequestTemplate template) {

        String authorization =
                request.getHeader("Authorization");

        if (authorization != null && !authorization.isBlank()) {
            template.header("Authorization", authorization);
        }
    }
}
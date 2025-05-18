package com.example.recipes.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class VisitCounterInterceptor implements HandlerInterceptor {

    private final VisitCounterService visitCounterService;

    @Autowired
    public VisitCounterInterceptor(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        String url = request.getRequestURI();
        visitCounterService.incrementVisitCount(url);
        return true;
    }
}


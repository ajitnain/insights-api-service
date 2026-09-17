package com.example.insights.controller;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail badRequest(IllegalArgumentException e) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid request", e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public ProblemDetail unreadableParameter(BindException e) {
        FieldError field = e.getFieldErrors().getFirst();

        return problem(HttpStatus.BAD_REQUEST, "Invalid request",
                field.getField() + " should read like 2026-09-15T08:00, not " + field.getRejectedValue());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail missingParameter(MissingServletRequestParameterException e) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid request",
                "missing required parameter: " + e.getParameterName());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail unauthenticated(IllegalStateException e) {
        return problem(HttpStatus.UNAUTHORIZED, "Unauthorized", e.getMessage());
    }

    private static ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle(title);
        problem.setDetail(detail);

        return problem;
    }
}

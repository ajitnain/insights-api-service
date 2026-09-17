package com.example.insights.controller.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/** Shared query parameters for every metrics endpoint. */
public record MetricsWindow(

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        @Parameter(required = true, examples = {
                @ExampleObject(name = "Start of the day", value = "2026-09-15T00:00"),
                @ExampleObject(name = "From 08:00", value = "2026-09-15T08:00")})
        LocalDateTime from,

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        @Parameter(required = true, examples = {
                @ExampleObject(name = "Start of the day", value = "2026-09-15T00:00"),
                @ExampleObject(name = "Until 18:00", value = "2026-09-15T18:00")})
        LocalDateTime to,

        @Parameter(required = true, examples = {
                @ExampleObject(name = "Per day", value = "day"),
                @ExampleObject(name = "Per hour", value = "hour")})
        String interval,

        @Parameter(required = true, examples = {
                @ExampleObject(name = "India", value = "Asia/Kolkata"),
                @ExampleObject(name = "UTC", value = "UTC"),
                @ExampleObject(name = "UK", value = "Europe/London")})
        String timezone) {
}

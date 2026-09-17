package com.example.insights.controller.dto;

/** One metric for one row. */
public record ValueResponse(String metric, long customerCount, long eventCount) {}
